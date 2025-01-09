package org.advent.year2016.day23;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2016.day12.Day12;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Day23 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day23()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 3, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 11120, 479007680)
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
		return solve(lines, Map.of("a", 7, "b", 0, "c", 0, "d", 0));
	}
	
	@Override
	public Object part2() {
		return solve(lines, Map.of("a", 12, "b", 0, "c", 0, "d", 0));
	}
	
	int solve(List<String> lines, Map<String, Integer> initialValues) {
		Map<String, Integer> values = new HashMap<>(initialValues);
		Function<String, Integer> getter = s -> Character.isLetter(s.charAt(0)) ? values.get(s) : Integer.parseInt(s);
		BiConsumer<String, Function<Integer, Integer>> setter = (key, op) -> {
			if (values.containsKey(key))
				values.compute(key, (k, v) -> op.apply(v == null ? 0 : v));
		};
		
		String[] operations = Day12.optimize(lines.toArray(String[]::new));
		
		for (int index = 0; index < operations.length; index++) {
			String[] split = operations[index].split(" ");
			switch (split[0]) {
				case "cpy" -> setter.accept(split[2], v -> getter.apply(split[1]));
				case "inc" -> setter.accept(split[1], v -> v + 1);
				case "dec" -> setter.accept(split[1], v -> v - 1);
				case "jnz" -> {
					if (getter.apply(split[1]) != 0)
						index += getter.apply(split[2]) - 1;
				}
				
				case "tgl" -> {
					int target = getter.apply(split[1]) + index;
					if (0 <= target && target < operations.length) {
						String targetOp = operations[target];
						operations[target] = switch (targetOp.split(" ")[0]) {
							case "inc" -> targetOp.replace("inc", "dec");
							case "dec" -> targetOp.replace("dec", "inc");
							case "tgl" -> targetOp.replace("tgl", "inc");
							case "jnz" -> targetOp.replace("jnz", "cpy");
							case "cpy" -> targetOp.replace("cpy", "jnz");
							// При попадании на оптимизированные операции лучше тоглить исходные операции и оптимизировать заново.
							// Но такое не происходит в заданном алгоритме
							default -> throw new IllegalStateException("Unknown toggle target: " + targetOp);
						};
					}
				}
				
				case "skip" -> {}
				case "add" -> setter.accept(split[2], v -> v + getter.apply(split[1]));
				case "muladd" -> setter.accept(split[3], v -> v + getter.apply(split[1]) * getter.apply(split[2]));
			}
		}
		return values.get("a");
	}
}