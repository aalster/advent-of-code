package org.advent.year2017.day8;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day8 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day8()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 1, 10),
				new ExpectedAnswers("input.txt", 6343, 7184)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
	}
	
	@Override
	public Object part1() {
		return run(new ArrayList<>()).values().stream().mapToInt(v -> v).max().orElseThrow();
	}
	
	@Override
	public Object part2() {
		List<Integer> allValues = new ArrayList<>();
		run(allValues);
		return allValues.stream().mapToInt(v -> v).max().orElseThrow();
	}
	
	Map<String, Integer> run(List<Integer> allValues) {
		Map<String, Integer> values = new HashMap<>();
		for (String line : lines) {
			String[] split = line.split(" ");
			if (!test(split[5], values.getOrDefault(split[4], 0), Integer.parseInt(split[6])))
				continue;
			int nextValue = values.getOrDefault(split[0], 0) + Integer.parseInt(split[2]) * ("inc".equals(split[1]) ? 1 : -1);
			values.put(split[0], nextValue);
			allValues.add(nextValue);
		}
		return values;
	}
	
	boolean test(String op, int left, int right) {
		return switch (op) {
			case "<" -> left < right;
			case ">" -> left > right;
			case "<=" -> left <= right;
			case ">=" -> left >= right;
			case "==" -> left == right;
			case "!=" -> left != right;
			default -> throw new IllegalStateException("Unexpected value: " + op);
		};
	}
}