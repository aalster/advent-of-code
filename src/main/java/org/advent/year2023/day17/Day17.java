package org.advent.year2023.day17;

import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day17 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day17.class, "input.txt");
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
		return solve(field, 0, 3);
	}
	
	private static long part2(Map<Point, Integer> field) {
		return solve(field, 4, 10);
	}
	
	private static long solve(Map<Point, Integer> field, int minForwardSteps, int maxForwardSteps) {
		Point start = new Point(0, 0);
		Point end = new Point(Point.maxX(field.keySet()), Point.maxY(field.keySet()));
		Rect bounds = new Rect(start, end);
		
		Map<Step, Integer> minHeats = new HashMap<>();
		Map<Step, Integer> currentSteps = Map.of(new Step(start, Direction.RIGHT, 0), 0);
		while (!currentSteps.isEmpty()) {
			currentSteps = currentSteps.entrySet().stream()
					.flatMap(e -> e.getKey().next(minForwardSteps, maxForwardSteps)
							.map(s -> Pair.of(s, e.getValue() + field.getOrDefault(s.position(), 0))))
					.filter(p -> bounds.containsInclusive(p.left().position()))
					.filter(p -> {
						if (p.left().stepsForward() < minForwardSteps)
							return true;
						Integer minHeat = minHeats.get(p.left());
						if (minHeat != null && minHeat < p.right())
							return false;
						minHeats.put(p.left(), p.right());
						return true;
					})
					.collect(Collectors.toMap(Pair::left, Pair::right, Math::min));
		}
		return minHeats.entrySet().stream()
				.filter(e -> e.getKey().position().equals(end))
				.mapToInt(Map.Entry::getValue)
				.min()
				.orElse(0);
	}
	
	record Step(Point position, Direction direction, int stepsForward) {
		Stream<Step> next(int minForwardSteps, int maxForwardSteps) {
			Stream<Direction> directions = stepsForward < minForwardSteps
					? Stream.of(direction)
					: stepsForward < maxForwardSteps
					? Stream.of(direction, direction.rotate(Direction.LEFT), direction.rotate(Direction.RIGHT))
					: Stream.of(direction.rotate(Direction.LEFT), direction.rotate(Direction.RIGHT));
			return directions.map(d -> new Step(position.shift(d), d, d == direction ? stepsForward + 1 : 1));
		}
	}
}