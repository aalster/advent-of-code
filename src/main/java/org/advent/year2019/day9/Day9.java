package org.advent.year2019.day9;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day9 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day9()).runAll();
//		new DayRunner(new Day9()).run("input.txt", 1);
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
	
	IntcodeComputer computer;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		computer = IntcodeComputer.parse(input.nextLine());
	}
	
	@Override
	public Object part1() {
		List<Long> output = computer.run(InputProvider.constant(1));
		if (output.size() == 1)
			return output.getFirst();
		return output.stream().map(n -> "" + n).collect(Collectors.joining(","));
	}
	
	@Override
	public Object part2() {
		return computer.run(InputProvider.constant(2)).getFirst();
	}
}