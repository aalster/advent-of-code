package org.advent.year2019.day21;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer;
import org.advent.year2019.intcode_computer.IntcodeComputerPrintingWrapper;

import java.util.List;
import java.util.Scanner;

public class Day21 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day21()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 19355227, 1143802926)
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
		IntcodeComputerPrintingWrapper wrapper = new IntcodeComputerPrintingWrapper(computer);
		wrapper.run(InputProvider.ascii("""
				NOT A J
				NOT B T
				OR T J
				NOT C T
				OR T J
				AND D J
				WALK
				"""));
		return null;
	}
	
	@Override
	public Object part2() {
		IntcodeComputerPrintingWrapper wrapper = new IntcodeComputerPrintingWrapper(computer);
		wrapper.run(InputProvider.ascii("""
				NOT B J
				NOT C T
				OR T J
				AND D J
				NOT E T
				NOT T T
				AND I T
				OR H T
				AND T J
				NOT A T
				OR T J
				RUN
				"""));
		return null;
	}
}