package org.advent.year2019.day7;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer;

import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Day7 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day7()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 43210, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 54321, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 65210, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example4.txt", ExpectedAnswers.IGNORE, 139629729),
				new ExpectedAnswers("example5.txt", ExpectedAnswers.IGNORE, 18216),
				new ExpectedAnswers("input.txt", 567045, 39016654)
		);
	}
	
	IntcodeComputer computer;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		computer = IntcodeComputer.parse(String.join("", Utils.readLines(input)));
	}
	
	@Override
	public Object part1() {
		long maxOutput = Long.MIN_VALUE;
		for (int[] phases : Utils.intPermutations(0, 1, 2, 3, 4)) {
			long input = 0;
			for (int phase : phases)
				input = computer.copy().runUntilOutput(InputProvider.constant(phase, input));
			maxOutput = Math.max(maxOutput, input);
		}
		return maxOutput;
	}
	
	@Override
	public Object part2() {
		long maxOutput = Long.MIN_VALUE;
		for (int[] phases : Utils.intPermutations(5, 6, 7, 8, 9)) {
			long input = 0;
			IntcodeComputer[] computers = IntStream.range(0, 5).mapToObj(i -> computer.copy()).toArray(IntcodeComputer[]::new);
			for (int i = 0; i < 5; i++)
				input = computers[i].runUntilOutput(InputProvider.constant(phases[i], input));
			int index = 0;
			while (true) {
				IntcodeComputer current = computers[index % computers.length];
				Long result = current.runUntilOutput(InputProvider.constant(input));
				if (current.getState() == IntcodeComputer.State.HALTED)
					break;
				input = result;
				index++;
			}
			maxOutput = Math.max(maxOutput, input);
		}
		return maxOutput;
	}
}