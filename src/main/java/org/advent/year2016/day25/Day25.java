package org.advent.year2016.day25;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2016.day12.Day12;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day25 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day25()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 196, ExpectedAnswers.IGNORE)
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
		return solve(lines);
	}
	
	@Override
	public Object part2() {
		return null;
	}
	
	int solve(List<String> lines) {
		String[] operations = optimize(lines.toArray(String[]::new));
		return IntStream.range(1, 1000)
				.mapToObj(a -> new Algorithm(operations, a))
				.filter(Algorithm::outputsClockSignal)
				.limit(1)
				.map(a -> a.initialA)
				.findAny()
				.orElse(-1);
	}
	
	static class Algorithm {
		final String[] operations;
		final Map<String, Integer> values;
		final int initialA;
		int index = 0;
		
		Algorithm(String[] operations, int initialA) {
			this.operations = operations;
			this.values = new HashMap<>(Map.of("a", initialA, "b", 0, "c", 0, "d", 0));
			this.initialA = initialA;
		}
		
		int get(String param) {
			return Character.isLetter(param.charAt(0)) ? values.get(param) : Integer.parseInt(param);
		}
		
		void set(String param, Function<Integer, Integer> remapping) {
			if (values.containsKey(param))
				values.compute(param, (k, v) -> remapping.apply(v == null ? 0 : v));
		}
		
		boolean outputsClockSignal() {
			for (int i = 0; i < 20; i++)
				if (nextOutput() != i % 2)
					return false;
			return true;
		}
		
		int nextOutput() {
			while (index < operations.length) {
				String[] split = operations[index].split(" ");
				switch (split[0]) {
					case "cpy" -> set(split[2], v -> get(split[1]));
					case "inc" -> set(split[1], v -> v + 1);
					case "dec" -> set(split[1], v -> v - 1);
					case "jnz" -> {
						if (get(split[1]) != 0)
							index += get(split[2]) - 1;
					}
					
					case "out" -> {
						index++;
						return get(split[1]);
					}
					
					case "skip" -> {}
					case "add" -> set(split[2], v -> v + get(split[1]));
					case "sub" -> set(split[2], v -> v - get(split[1]));
					case "muladd" -> set(split[3], v -> v + get(split[1]) * get(split[2]));
				}
				index++;
			}
			return -1;
		}
	}
	
	String[] optimize(String[] operations) {
		operations = Day12.optimize(operations);

		for (int index = 0; index < operations.length; index++) {
			String operation = operations[index];
			
			if (operation.startsWith("jnz")) {
				String[] split = operation.split(" ");
				String left = split[1];
				String right = split[2];
				if (Character.isLetter(left.charAt(0)) && !Character.isLetter(right.charAt(0))) {
					int target = Integer.parseInt(right) + index;
					if (target >= index)
						continue;
					
					Map<String, List<String>> ops = Arrays.stream(operations, target, index)
							.filter(op -> !"skip".equals(op))
							.collect(Collectors.groupingBy(op -> op.split(" ")[0]));
					List<String> decOps = ops.getOrDefault("dec", List.of());

//			    	dec d
//			    	dec c
//			    	jnz c -2
					
					if (ops.size() == 1 && decOps.size() == 2) {
						String[] decOp1 = decOps.getFirst().split(" ");
						String[] decOp2 = decOps.getLast().split(" ");
						if (!decOp2[1].equals(left))
							continue;
						
						operations[target] = "sub " + left + " " + decOp1[1];
						operations[target + 1] = "cpy 0 " + left;
						operations[index] = "skip";
					}
				} else {
					if ("0".equals(left)) {
						operations[index] = "skip";
						continue;
					}
					if ("1".equals(left)) {
						if (right.equals("0")) {
							operations[index] = "freeze";
						} else if (!Character.isLetter(right.charAt(0))) {
							int target = Integer.parseInt(right);
							if (target >= -1)
								continue;

//					    	jnz c 2
//					    	jnz 1 4
//					    	dec b
//					    	dec c
//					    	jnz 1 -4
							
							String[] targetOp = operations[index + target].split(" ");
							String[] targetNextOp = operations[index + target + 1].split(" ");
							if (targetOp[0].equals("jnz") && Character.isLetter(targetOp[1].charAt(0)) && "2".equals(targetOp[2])) {
								if (targetNextOp[0].equals("jnz") && targetNextOp[1].equals("1") && targetNextOp[2].equals("" + -target)) {
									operations[index] = "jnz " + targetOp[1].charAt(0) + " " + (target + 2);
									operations[index + target] = "skip";
									operations[index + target + 1] = "skip";
									// Возможна дополнительная оптимизация этой же строки
									index--;
								}
							}
						}
					}
				}
			}
		}
		return operations;
	}
}