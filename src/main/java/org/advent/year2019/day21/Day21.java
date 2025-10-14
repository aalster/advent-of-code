package org.advent.year2019.day21;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer2;
import org.advent.year2019.intcode_computer.OutputConsumer;

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
	
	final boolean silent = true;
	long[] program;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		program = IntcodeComputer2.parseProgram(input.nextLine());
	}
	
	@Override
	public Object part1() {
		return solve("""
				NOT A J
				NOT B T
				OR T J
				NOT C T
				OR T J
				AND D J
				WALK
				""");
	}
	
	@Override
	public Object part2() {
		return solve("""
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
				""");
	}
	
	long solve(String input) {
		HullDamageReader hullDamageReader = new HullDamageReader();
		OutputConsumer output = OutputConsumer.combine(hullDamageReader, OutputConsumer.asciiPrinter(silent));
		new IntcodeComputer2(program, InputProvider.ascii(input), output).run();
		return hullDamageReader.damage;
	}
	
	static class HullDamageReader implements OutputConsumer {
		long damage = 0;
		
		@Override
		public void accept(long value) {
			if (value > 255)
				damage = value;
		}
	}
}