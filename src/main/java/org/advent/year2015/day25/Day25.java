package org.advent.year2015.day25;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day25 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day25()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 25397450, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 19980801, ExpectedAnswers.IGNORE)
		);
	}
	
	int row;
	int column;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		String[] split = input.nextLine()
				.replace(" column ", "").replace(".", "")
				.split("row ")[1].split(",");
		row = Integer.parseInt(split[0]);
		column = Integer.parseInt(split[1]);
	}
	
	@Override
	public Object part1() {
		int diagonalStartRow = row + column - 1;
		int index = diagonalStartRow * (diagonalStartRow - 1) / 2 + column - 1;
		
		long number = 20151125;
		while (index-- > 0)
			number = number * 252533 % 33554393;
		return number;
	}
	
	@Override
	public Object part2() {
		return null;
	}
}