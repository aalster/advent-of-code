package org.advent.year2015.day18;

import org.advent.common.DirectionExt;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day18 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day18.class, "input.txt");
		Map<Point, Character> field = Point.readFieldMap(Utils.readLines(input));
		
		System.out.println("Answer 1: " + part1(field));
		System.out.println("Answer 2: " + part2(field));
	}
	
	private static long part1(Map<Point, Character> field) {
		return solve(field, Map.of());
	}
	
	private static long part2(Map<Point, Character> field) {
		Point bound = Point.maxBound(field.keySet());
		Map<Point, Character> fixed = Stream.of(new Point(0, 0), new Point(0, bound.y()), new Point(bound.x(), 0), bound)
				.collect(Collectors.toMap(p -> p, p -> '#'));
		
		field = new HashMap<>(field);
		field.putAll(fixed);
		return solve(field, fixed);
	}
	
	private static long solve(Map<Point, Character> field, Map<Point, Character> fixed) {
		int steps = 100;
		while (steps > 0) {
			Map<Point, Character> _field = field;
			Map<Point, Character> nextField = new HashMap<>();
			for (Map.Entry<Point, Character> entry : field.entrySet()) {
				long lightedNeighbors = DirectionExt.stream()
						.map(d -> d.shift(entry.getKey()))
						.filter(p -> _field.getOrDefault(p, '.') == '#')
						.count();
				boolean nextLight = lightedNeighbors == 3 || (entry.getValue() == '#' && lightedNeighbors == 2);
				nextField.put(entry.getKey(), nextLight ? '#' : '.');
			}
			nextField.putAll(fixed);
			field = nextField;
			steps--;
		}
		return field.values().stream().filter(c -> c == '#').count();
	}
}