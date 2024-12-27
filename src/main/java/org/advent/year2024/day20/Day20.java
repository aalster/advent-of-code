package org.advent.year2024.day20;

import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AbstractDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.Set;

public class Day20 extends AbstractDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day20()).run("input.txt");
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", null, null),
				new ExpectedAnswers("input.txt", null, null)
		);
	}
	
	Map<Character, List<Point>> field;
	int savedTime;
	
	@Override
	public void prepare(String file) {
		field = Point.readField(Utils.readLines(Utils.scanFileNearClass(getClass(), file)));
		savedTime = switch (file) {
			case "example.txt" -> 50;
			case "input.txt" -> 100;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		Set<Point> walls = new HashSet<>(field.get('#'));
		Map<Point, Integer> path = findPath(field);
		
		List<Pair<Direction, Direction>> pairs = List.of(Pair.of(Direction.LEFT, Direction.RIGHT), Pair.of(Direction.UP, Direction.DOWN));
		long cheats = 0;
		for (Point wall : walls) {
			for (Pair<Direction, Direction> pair : pairs) {
				Integer left = path.get(wall.shift(pair.left()));
				Integer right = path.get(wall.shift(pair.right()));
				if (left != null && right != null && Math.abs(left - right) - 2 >= savedTime)
					cheats++;
			}
		}
		
		return cheats;
	}
	
	@Override
	public Object part2() {
		SequencedMap<Point, Integer> path = findPath(field);
		return path.entrySet().stream()
				.limit(path.size() - savedTime)
				.mapToLong(entry -> countCheats(entry.getKey(), entry.getValue(), path))
				.sum();
	}
	
	private long countCheats(Point position, int currentStep, Map<Point, Integer> path) {
		return path.entrySet().stream()
				.filter(e -> {
					int distance = e.getKey().distanceTo(position);
					return distance <= 20 && e.getValue() - currentStep - distance >= savedTime;
				})
				.count();
	}
	
	private SequencedMap<Point, Integer> findPath(Map<Character, List<Point>> field) {
		Set<Point> walls = new HashSet<>(field.get('#'));
		Point start = field.get('S').getFirst();
		Point end = field.get('E').getFirst();
		
		SequencedMap<Point, Integer> path = new LinkedHashMap<>();
		Point current = start;
		int steps = 0;
		while (!current.equals(end)) {
			path.put(current, steps);
			current = Direction.stream()
					.map(current::shift)
					.filter(n -> !walls.contains(n))
					.filter(n -> !path.containsKey(n))
					.findFirst()
					.orElseThrow();
			steps++;
		}
		path.put(current, steps);
		return path;
	}
}