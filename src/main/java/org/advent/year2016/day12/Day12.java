package org.advent.year2016.day12;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day12 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day12()).runAll();
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
		return solve(lines, Map.of("a", 0, "b", 0, "c", 0, "d", 0));
	}
	
	@Override
	public Object part2() {
		return solve(lines, Map.of("a", 0, "b", 0, "c", 1, "d", 0));
	}
	
	int solve(List<String> lines, Map<String, Integer> initialValues) {
		Map<String, Integer> values = new HashMap<>(initialValues);
		Function<String, Integer> getter = s -> Character.isLetter(s.charAt(0)) ? values.get(s) : Integer.parseInt(s);
		BiConsumer<String, Function<Integer, Integer>> setter = (key, op) -> {
			if (!values.containsKey(key))
				throw new RuntimeException("Value not found: " + key);
			values.compute(key, (k, v) -> op.apply(v == null ? 0 : v));
		};
		
		String[] operations = optimize(lines.toArray(String[]::new));
		
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
				
				case "skip" -> {}
				case "add" -> setter.accept(split[2], v -> v + getter.apply(split[1]));
				case "muladd" -> setter.accept(split[3], v -> v + getter.apply(split[1]) * getter.apply(split[2]));
			}
		}
		return values.get("a");
	}
	
	// Используется в 2016-23
	public static String[] optimize(String[] operations) {
		String[] optimized = Arrays.copyOf(operations, operations.length);
		for (int index = 0; index < optimized.length; index++) {
			String operation = optimized[index];
			if (operation.startsWith("jnz")) {
				String[] split = operation.split(" ");
				String left = split[1];
				String right = split[2];
				if (!Character.isLetter(left.charAt(0)) || Character.isLetter(right.charAt(0)))
					continue;
				int target = Integer.parseInt(right) + index;
				if (target >= index)
					continue;
				
				Map<String, List<String>> ops = Arrays.stream(optimized, target, index)
						.filter(op -> !"skip".equals(op))
						.collect(Collectors.groupingBy(op -> op.split(" ")[0]));
				List<String> decOps = ops.getOrDefault("dec", List.of());
				List<String> incOps = ops.getOrDefault("inc", List.of());
				List<String> cpyOps = ops.getOrDefault("cpy", List.of());
				List<String> addOps = ops.getOrDefault("add", List.of());
				
//				cpy 14 d
//				add d a
//				cpy 0 d
//				dec c
//				jnz c -4
				
				if (ops.size() == 3 && decOps.size() == 1 && addOps.size() == 1 && cpyOps.size() == 2) {
					String[] addOp = addOps.getFirst().split(" ");
					String[] decOp = decOps.getFirst().split(" ");
					if (!addOp[1].equals(cpyOps.getFirst().split(" ")[2])
							|| !addOp[1].equals(cpyOps.getLast().split(" ")[2])
							|| !"0".equals(cpyOps.getLast().split(" ")[1]))
						continue;
					if (!decOp[1].equals(left))
						continue;
					
					optimized[target] = "muladd " + left + " " + cpyOps.getFirst().split(" ")[1] + " " + addOp[2];
					optimized[target + 1] = cpyOps.getLast();
					optimized[target + 2] = "cpy 0 " + left;
					for (int i = target + 3; i <= index; i++)
						optimized[i] = "skip";
				}
				
//				inc d
//				dec c
//				jnz c -2
				
				if (ops.size() == 2 && decOps.size() == 1 && incOps.size() == 1) {
					String[] incOp = incOps.getFirst().split(" ");
					String[] decOp = decOps.getFirst().split(" ");
					if (!decOp[1].equals(left))
						continue;
					
					optimized[target] = "add " + left + " " + incOp[1];
					optimized[target + 1] = "cpy 0 " + left;
					for (int i = target + 2; i <= index; i++)
						optimized[i] = "skip";
				}
			}
		}
		return optimized;
	}
}