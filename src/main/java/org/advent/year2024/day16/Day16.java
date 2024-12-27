package org.advent.year2024.day16;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AbstractDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

public class Day16 extends AbstractDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day16()).run("input.txt");
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", null, null),
				new ExpectedAnswers("input.txt", null, null)
		);
	}
	
	Map<Character, List<Point>> field;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		field = Point.readField(Utils.readLines(input));
	}
	
	@Override
	public Object part1() {
		return findMinPaths(field).getFirst().score;
	}
	
	@Override
	public Object part2() {
		return findMinPaths(field).stream().flatMap(p -> p.visited.stream()).distinct().count() + 1;
	}
	
	List<Path> findMinPaths(Map<Character, List<Point>> field) {
		Set<Point> walls = new HashSet<>(field.get('#'));
		Point start = field.get('S').getFirst();
		Point end = field.get('E').getFirst();
		
		Map<Point, Map<Direction, Integer>> cache = new HashMap<>();
		List<Path> minPaths = new ArrayList<>();
		int minScore = Integer.MAX_VALUE;
		
		List<Path> paths = new ArrayList<>(List.of(new Path(Set.of(), start, Direction.RIGHT, 0)));
		while (!paths.isEmpty()) {
			Path path = paths.removeFirst();
			
			if (minScore < path.score)
				continue;
			
			if (path.current.equals(end)) {
				minScore = path.score;
				minPaths.add(path);
				continue;
			}
			
			int bestForLocation = cache.computeIfAbsent(path.current, k -> new HashMap<>())
					.compute(path.direction, (k, v) -> v == null ? path.score : Math.min(v, path.score));
			if (path.score <= bestForLocation)
				path.next(walls).forEach(paths::add);
		}
		int _minScore = minScore;
		return minPaths.stream().filter(p -> p.score == _minScore).toList();
	}
	
	record Path(Set<Point> visited, Point current, Direction direction, int score) {
		
		Stream<Path> next(Set<Point> walls) {
			Set<Point> nextVisited = new HashSet<>(visited);
			nextVisited.add(current);
			
			return Stream.of(direction, direction.rotate(Direction.LEFT), direction.rotate(Direction.RIGHT))
					.map(nextDirection -> {
						Point next = current.shift(nextDirection);
						if (walls.contains(next) || visited.contains(next))
							return null;
						return new Path(nextVisited, next, nextDirection, score + (direction == nextDirection ? 1 : 1001));
					})
					.filter(Objects::nonNull);
		}
	}
}