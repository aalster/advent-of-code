package org.advent.year2019.day18;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day18 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day18()).runAll();
//		new DayRunner(new Day18()).run("example.txt", 1);
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 8, null),
				new ExpectedAnswers("example2.txt", 86, null),
				new ExpectedAnswers("example3.txt", 132, null),
				new ExpectedAnswers("example4.txt", 136, null),
				new ExpectedAnswers("example5.txt", 81, null),
				new ExpectedAnswers("input.txt", null, null)
		);
	}
	
	Field field;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		Map<Character, List<Point>> fieldMap = Point.readField(Utils.readLines(input));
		Point position = fieldMap.get('@').getFirst();
		Map<Character, Point> doors = fieldMap.entrySet().stream()
				.filter(e -> 'A' <= e.getKey() && e.getKey() <= 'Z')
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFirst()));
		Map<Character, Point> keys = fieldMap.entrySet().stream()
				.filter(e -> 'a' <= e.getKey() && e.getKey() <= 'z')
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFirst()));
		Set<Point> walls = new HashSet<>(fieldMap.get('#'));
		walls.addAll(doors.values());
		field = new Field(position, walls, doors, keys, 0);
	}
	
	@Override
	public Object part1() {
		Collection<Field> fields = List.of(field);
		int minTotalDistance = Integer.MAX_VALUE;
		while (!fields.isEmpty()) {
			int _minTotalDistance = minTotalDistance;
			fields = fields.stream()
					.flatMap(Field::next)
					.filter(f -> f.totalDistance < _minTotalDistance)
//					.peek(Field::print)
					.collect(Collectors.toSet());
			minTotalDistance = fields.stream()
					.filter(f -> f.keysLeft.isEmpty())
					.mapToInt(Field::totalDistance)
					.min()
					.orElse(minTotalDistance);
		}
		return minTotalDistance;
	}
	
	@Override
	public Object part2() {
		return null;
	}
	
	record Field(Point position, Set<Point> walls, Map<Character, Point> doors, Map<Character, Point> keysLeft, int totalDistance) {
		
		Stream<Field> next() {
			return keysDistances().entrySet().stream()
					.map(e -> {
						Point nextPosition = keysLeft.get(e.getKey());
						Set<Point> nextWalls = new HashSet<>(walls);
						nextWalls.remove(doors.get(Character.toUpperCase(e.getKey())));
						Map<Character, Point> nextKeysLeft = new HashMap<>(keysLeft);
						nextKeysLeft.remove(e.getKey());
						return new Field(nextPosition, nextWalls, doors, nextKeysLeft, totalDistance + e.getValue());
					});
		}
		
		Map<Character, Integer> keysDistances() {
			Map<Character, Integer> distances = new HashMap<>();
			Set<Point> current = Set.of(position);
			Set<Point> visited = new HashSet<>(current);
			int distance = 0;
			while (distances.size() < keysLeft.size() && !current.isEmpty()) {
				current = current.stream()
						.flatMap(c -> Direction.stream().map(c::shift))
						.filter(n -> !visited.contains(n))
						.filter(n -> !walls.contains(n))
						.collect(Collectors.toSet());
				distance++;
				for (Map.Entry<Character, Point> entry : keysLeft.entrySet())
					if (current.contains(entry.getValue()))
						distances.put(entry.getKey(), distance);
				visited.addAll(current);
			}
//			System.out.println("distances from position: " + position);
//			distances.entrySet().forEach(System.out::println);
			return distances;
		}
		
		void print() {
			System.out.println();
			System.out.println(this);
			Point.printField(walls, p -> {
				if (position.equals(p))
					return '@';
				return walls.contains(p) ? '#' : '.';
			});
		}
	}
}