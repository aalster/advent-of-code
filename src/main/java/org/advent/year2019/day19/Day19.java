package org.advent.year2019.day19;

import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer2;
import org.advent.year2019.intcode_computer.OutputConsumer;

import java.util.List;
import java.util.Scanner;

public class Day19 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day19()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 154, 9791328)
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
		int width = 50;
		long affectedPoints = 0;
		for (int y = 0; y < width; y++)
			for (int x = 0; x < width; x++)
				affectedPoints += check(x, y);
		return affectedPoints;
	}
	
	@Override
	public Object part2() {
		int width = 100;
		Point topLeft = Point.ZERO;
		while (check(topLeft.x() + width - 1, topLeft.y()) <= 0) {
			while (check(topLeft.x() + width - 1, topLeft.y()) <= 0)
				topLeft = topLeft.shift(0, 1);
			while (check(topLeft.x(), topLeft.y() + width - 1) <= 0)
				topLeft = topLeft.shift(1, 0);
		}
		return topLeft.x() * 10000 + topLeft.y();
	}
	
	long check(int x, int y) {
		return new IntcodeComputer2(program, InputProvider.constant(x, y), OutputConsumer.empty()).runUntilOutput();
	}
}