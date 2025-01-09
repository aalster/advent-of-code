package org.advent.year2017.day1;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day1 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day1()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 3, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 4, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 0, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example4.txt", 9, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example5.txt", ExpectedAnswers.IGNORE, 6),
				new ExpectedAnswers("example6.txt", ExpectedAnswers.IGNORE, 0),
				new ExpectedAnswers("example7.txt", ExpectedAnswers.IGNORE, 4),
				new ExpectedAnswers("example8.txt", ExpectedAnswers.IGNORE, 12),
				new ExpectedAnswers("example9.txt", ExpectedAnswers.IGNORE, 4),
				new ExpectedAnswers("input.txt", 1089, 1156)
		);
	}
	
	String line;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		line = input.nextLine();
	}
	
	@Override
	public Object part1() {
		char[] chars = line.toCharArray();
		int result = 0;
		for (int i = 0; i < chars.length; i++)
			if (chars[i] == chars[(i + 1) % chars.length])
				result += chars[i] - '0';
		return result;
	}
	
	@Override
	public Object part2() {
		char[] chars = line.toCharArray();
		int result = 0;
		for (int i = 0; i < chars.length; i++)
			if (chars[i] == chars[(i + chars.length / 2) % chars.length])
				result += chars[i] - '0';
		return result;
	}
}