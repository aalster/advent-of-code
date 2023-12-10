package org.advent.year2021.day15;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day15 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day15.class, "input.txt");
		Map<Point, Integer> field = new HashMap<>();
		int y = 0;
		while (input.hasNext()) {
			String line = input.nextLine();
			for (int x = 0; x < line.length(); x++)
				field.put(new Point(x, y), line.charAt(x) - '0');
			y++;
		}
		
		System.out.println("Answer 1: " + part1(field));
		System.out.println("Answer 2: " + part2(field));
	}
	
	private static long part1(Map<Point, Integer> field) {
		return solve(field);
	}
	
	private static long part2(Map<Point, Integer> field) {
		int width = field.keySet().stream().mapToInt(Point::x).max().orElse(0) + 1;
		int height = field.keySet().stream().mapToInt(Point::y).max().orElse(0) + 1;
		
		Map<Point, Integer> extendedField = new HashMap<>();
		for (int y = 0; y < 5; y++) {
			int _y = y;
			int dy = y * height;
			for (int x = 0; x < 5; x++) {
				int _x = x;
				int dx = x * width;
				extendedField.putAll(field.entrySet().stream().collect(Collectors.toMap(
						e -> e.getKey().shift(dx, dy),
						e -> wrapDangerLevel(e.getValue() + _x + _y)
				)));
			}
		}
		
		return solve(extendedField);
	}
	
	private static int wrapDangerLevel(int sum) {
		return sum <= 9 ? sum : sum - 9;
	}
	
	private static Long solve(Map<Point, Integer> field) {
		List<Path> paths = List.of(new Path(new Point(0, 0), 0));
		Map<Point, Long> shortestPaths = new HashMap<>();
		
		while (!paths.isEmpty())
			paths = paths.stream().flatMap(p -> p.nextPaths(field, shortestPaths)).toList();
		
		Point end = new Point(
				field.keySet().stream().mapToInt(Point::x).max().orElse(0),
				field.keySet().stream().mapToInt(Point::y).max().orElse(0));
		return shortestPaths.getOrDefault(end, -1L);
	}
	
	private record Path(Point currentPosition, long weight) {
		
		public Stream<Path> nextPaths(Map<Point, Integer> field, Map<Point, Long> shortestPaths) {
			if (shortestPaths.getOrDefault(currentPosition(), Long.MAX_VALUE) < weight)
				return Stream.of();
			return Direction.stream()
					.map(d -> d.shift(currentPosition()))
					.filter(field::containsKey)
					.map(p -> new Path(p, weight + field.get(p)))
					.filter(p -> p.weight() < shortestPaths.getOrDefault(p.currentPosition(), Long.MAX_VALUE))
					.peek(p -> shortestPaths.put(p.currentPosition(), p.weight()));
		}
	}
}