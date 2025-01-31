package org.advent.year2019.day2;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day2 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day2()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 3500, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 2, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 30, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 2890696, 8226)
		);
	}
	
	int[] program;
	Map<Integer, Integer> replacements;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		program = Arrays.stream(input.nextLine().split(",")).mapToInt(Integer::parseInt).toArray();
		replacements = "input.txt".equals(file) ? Map.of(1, 12, 2, 2) : Map.of();
	}
	
	@Override
	public Object part1() {
		return run(program, replacements);
	}
	
	@Override
	public Object part2() {
		for (int noun = 0; noun < 100; noun++)
			for (int verb = 0; verb < 100; verb++)
				if (run(Arrays.copyOf(program, program.length), Map.of(1, noun, 2, verb)) == 19690720)
					return 100 * noun + verb;
		return null;
	}
	
	int run(int[] program, Map<Integer, Integer> replacements) {
		replacements.forEach((k, v) -> program[k] = v);
		loop: for (int index = 0; index < program.length - 4; index += 4) {
			int left = program[index + 1];
			int right = program[index + 2];
			int target = program[index + 3];
			switch (program[index]) {
				case 1 -> program[target] = program[left] + program[right];
				case 2 -> program[target] = program[left] * program[right];
				case 99 -> {break loop;}
			}
		}
		return program[0];
	}
}