package org.advent.year2015.day18;

import org.advent.common.DirectionExt;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day18 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day18()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 4, 17),
				new ExpectedAnswers("input.txt", 821, 886)
		);
	}
	
	Map<Point, Character> field;
	int part1Steps;
	int part2Steps;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		field = Point.readFieldMap(Utils.readLines(input));
		part1Steps = switch (file) {
			case "example.txt" -> 4;
			case "input.txt" -> 100;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
		part2Steps = switch (file) {
			case "example.txt" -> 5;
			case "input.txt" -> 100;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		return solve(field, Map.of(), part1Steps);
	}
	
	@Override
	public Object part2() {
		Point bound = Point.maxBound(field.keySet());
		Map<Point, Character> fixed = Stream.of(new Point(0, 0), new Point(0, bound.y()), new Point(bound.x(), 0), bound)
				.collect(Collectors.toMap(p -> p, p -> '#'));
		
		Map<Point, Character> fieldWithFixed = new HashMap<>(field);
		fieldWithFixed.putAll(fixed);
		return solve(fieldWithFixed, fixed, part2Steps);
	}
	
	long solve(Map<Point, Character> field, Map<Point, Character> fixed, int steps) {
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