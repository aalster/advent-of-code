package org.advent.year2024.day20;

import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SequencedMap;
import java.util.Set;

public class Day20 {
	static final Data example = new Data("example.txt", 50);
	static final Data input = new Data("input.txt", 100);
	static final Data data = input;
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day20.class, data.file);
		Map<Character, List<Point>> field = Point.readField(Utils.readLines(input));
		
		long start = System.currentTimeMillis();
		System.out.println("Answer 1: " + part1(field));
		System.out.println("Answer 2: " + part2(field));
		System.out.println((System.currentTimeMillis() - start) + "ms");
	}
	
	private static long part1(Map<Character, List<Point>> field) {
		Set<Point> walls = new HashSet<>(field.get('#'));
		Map<Point, Integer> path = findPath(field);
		
		List<Pair<Direction, Direction>> pairs = List.of(Pair.of(Direction.LEFT, Direction.RIGHT), Pair.of(Direction.UP, Direction.DOWN));
		long cheats = 0;
		for (Point wall : walls) {
			for (Pair<Direction, Direction> pair : pairs) {
				Integer left = path.get(wall.shift(pair.left()));
				Integer right = path.get(wall.shift(pair.right()));
				if (left != null && right != null && Math.abs(left - right) - 2 >= data.savedTime)
					cheats++;
			}
		}
		
		return cheats;
	}
	
	private static long part2(Map<Character, List<Point>> field) {
		SequencedMap<Point, Integer> path = findPath(field);
		return path.entrySet().stream()
				.limit(path.size() - data.savedTime)
				.mapToLong(entry -> countCheats(entry.getKey(), entry.getValue(), path))
				.sum();
	}
	
	private static long countCheats(Point position, int currentStep, Map<Point, Integer> path) {
		return path.entrySet().stream()
				.filter(e -> {
					int distance = e.getKey().distanceTo(position);
					return distance <= 20 && e.getValue() - currentStep - distance >= data.savedTime;
				})
				.count();
	}
	
	private static SequencedMap<Point, Integer> findPath(Map<Character, List<Point>> field) {
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
	
	record Data(String file, int savedTime) {
	}
}