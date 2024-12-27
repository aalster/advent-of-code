package org.advent.year2024.day10;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Day10 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day10()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 36, 81),
				new ExpectedAnswers("input.txt", 430, 928)
		);
	}
	
	Map<Point, Integer> field;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		field = Point.readFieldMap(Utils.readLines(input)).entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() - '0'));
	}
	
	@Override
	public Object part1() {
		return solve(false);
	}
	
	@Override
	public Object part2() {
		return solve(true);
	}
	
	long solve(boolean uniquePaths) {
		Collector<Point, ?, Collection<Point>> collector = Collectors.toCollection(
				uniquePaths ? ArrayList::new : HashSet::new);
		
		int result = 0;
		for (Map.Entry<Point, Integer> entry : field.entrySet()) {
			if (entry.getValue() != 0)
				continue;
			
			Collection<Point> current = List.of(entry.getKey());
			int steps = 0;
			while (steps < 9) {
				steps++;
				int _steps = steps;
				current = current.stream()
						.flatMap(c -> Direction.stream().map(c::shift))
						.filter(p -> field.containsKey(p) && field.get(p) == _steps)
						.collect(collector);
			}
			result += current.size();
		}
		return result;
	}
}