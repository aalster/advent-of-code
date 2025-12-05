package org.advent.year2020.day8;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Day8 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day8()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 5, 8),
				new ExpectedAnswers("input.txt", 1489, 1539)
		);
	}
	
	String[] instructions;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		instructions = Utils.readLines(input).toArray(String[]::new);
	}
	
	@Override
	public Object part1() {
		return run(instructions, 0, 0, false).acc;
	}
	
	@Override
	public Object part2() {
		return run(instructions, 0, 0, true).acc;
	}
	
	static ExecutionResult run(String[] instructions, int index, int acc, boolean changeAllowed) {
		Set<Integer> visited = new HashSet<>();
		while (visited.add(index)) {
			if (index == instructions.length)
				return new ExecutionResult(true, acc);
			if (index < 0 || instructions.length < index)
				break;
			
			String[] split = instructions[index].split(" ");
			int value = Integer.parseInt(split[1]);
			
			switch (split[0]) {
				case "nop" -> {
					if (changeAllowed) {
						ExecutionResult result = runCopy(instructions, index, "jmp " + value, acc);
						if (result.finished())
							return result;
					}
					index++;
				}
				case "jmp" -> {
					if (changeAllowed) {
						ExecutionResult result = runCopy(instructions, index, "nop " + value, acc);
						if (result.finished())
							return result;
					}
					index += value;
				}
				case "acc" -> {
					acc += value;
					index++;
				}
			}
		}
		return new ExecutionResult(false, acc);
	}
	
	static ExecutionResult runCopy(String[] instructions, int index, String replacement, int acc) {
		String[] copy = Arrays.copyOf(instructions, instructions.length);
		copy[index] = replacement;
		return run(copy, index, acc, false);
	}
	
	record ExecutionResult(boolean finished, int acc) {
	}
}