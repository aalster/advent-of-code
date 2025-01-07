package org.advent.year2016.day12;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class Day12 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day12()).runAll();
//		new DayRunner(new Day12()).run("example.txt", 1);
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 42, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 318077, 9227731)
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
		return solve(Map.of("a", 0, "b", 0, "c", 0, "d", 0));
	}
	
	@Override
	public Object part2() {
		return solve(Map.of("a", 0, "b", 0, "c", 1, "d", 0));
	}
	
	int solve(Map<String, Integer> initialValues) {
		Map<String, Integer> values = new HashMap<>(initialValues);
		Function<String, Integer> getter = s -> Character.isLetter(s.charAt(0)) ? values.get(s) : Integer.parseInt(s);
		
		for (int index = 0; index < lines.size(); index++) {
			String[] split = lines.get(index).split(" ");
			switch (split[0]) {
				case "cpy" -> values.put(split[2], getter.apply(split[1]));
				case "inc" -> values.compute(split[1], (k, v) -> (v == null ? 0 : v) + 1);
				case "dec" -> values.compute(split[1], (k, v) -> (v == null ? 0 : v) - 1);
				case "jnz" -> {
					if (getter.apply(split[1]) != 0)
						index += getter.apply(split[2]) - 1;
				}
			}
		}
		return values.get("a");
	}
}