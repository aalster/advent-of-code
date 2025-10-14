package org.advent.year2019.day7;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer;
import org.advent.year2019.intcode_computer.OutputConsumer;

import java.util.List;
import java.util.Scanner;

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
	
	long[] program;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		program = IntcodeComputer.parseProgram(String.join("", Utils.readLines(input)));
	}
	
	@Override
	public Object part1() {
		long maxOutput = Long.MIN_VALUE;
		for (int[] phases : Utils.intPermutations(0, 1, 2, 3, 4)) {
			OutputConsumer.BufferingOutputConsumer outputConsumer = OutputConsumer.buffering();
			createAmplifier(program, phases, outputConsumer).accept(0);
			maxOutput = Math.max(maxOutput, outputConsumer.readNext());
		}
		return maxOutput;
	}
	
	@Override
	public Object part2() {
		long maxOutput = Long.MIN_VALUE;
		for (int[] phases : Utils.intPermutations(5, 6, 7, 8, 9)) {
			OutputConsumer.BufferingOutputConsumer outputConsumer = OutputConsumer.buffering();
			Amplifier amplifier = createAmplifier(program, phases, outputConsumer);
			
			outputConsumer.accept(0);
			long output = 0;
			while (outputConsumer.hasNext()) {
				output = outputConsumer.readNext();
				amplifier.accept(output);
			}
			maxOutput = Math.max(maxOutput, output);
		}
		return maxOutput;
	}
	
	Amplifier createAmplifier(long[] program, int[] phases, OutputConsumer lastOutputConsumer) {
		Amplifier amplifier = null;
		for (int phase : phases)
			amplifier = new Amplifier(program, phase, amplifier == null ? lastOutputConsumer : amplifier);
		return amplifier;
	}
	
	static class Amplifier implements OutputConsumer {
		final InputProvider.BufferingInputProvider inputProvider = InputProvider.buffering();
		final IntcodeComputer computer;
		
		Amplifier(long[] program, int phase, OutputConsumer outputConsumer) {
			this.computer = new IntcodeComputer(program, inputProvider, outputConsumer);
			accept(phase);
		}
		
		@Override
		public void accept(long output) {
			inputProvider.append(output);
			computer.run();
		}
	}
}