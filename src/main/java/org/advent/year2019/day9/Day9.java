package org.advent.year2019.day9;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer2;
import org.advent.year2019.intcode_computer.OutputConsumer;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Day9 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day9()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99", ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 1219070632396864L, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 1125899906842624L, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 2789104029L, 32869)
		);
	}
	
	long[] program;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		program = IntcodeComputer2.parseProgram(input.nextLine());
	}
	
	@Override
	public Object part1() {
		OutputConsumer.BufferingOutputConsumer outputConsumer = OutputConsumer.buffering();
		new IntcodeComputer2(program, InputProvider.constant(1), outputConsumer).run();
		if (outputConsumer.size() == 1)
			return outputConsumer.readNext();
		return LongStream.of(outputConsumer.readAll()).mapToObj(n -> "" + n).collect(Collectors.joining(","));
	}
	
	@Override
	public Object part2() {
		OutputConsumer.BufferingOutputConsumer outputConsumer = OutputConsumer.buffering();
		new IntcodeComputer2(program, InputProvider.constant(2), outputConsumer).run();
		return outputConsumer.readNext();
	}
}