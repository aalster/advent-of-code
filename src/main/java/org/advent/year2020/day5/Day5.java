package org.advent.year2020.day5;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day5 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day5()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 357, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 567, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 119, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example4.txt", 820, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 976, 685)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
	}
	
	@Override
	public Object part1() {
		return lines.stream()
				.map(l -> Utils.replaceEach(l, new String[] {"F", "L"}, "0"))
				.map(l -> Utils.replaceEach(l, new String[] {"B", "R"}, "1"))
				.mapToInt(l -> Integer.parseInt(l, 2))
				.max()
				.orElse(0);
	}
	
	@Override
	public Object part2() {
		int[] seats = lines.stream()
				.map(l -> Utils.replaceEach(l, new String[]{"F", "L"}, "0"))
				.map(l -> Utils.replaceEach(l, new String[]{"B", "R"}, "1"))
				.mapToInt(l -> Integer.parseInt(l, 2))
				.sorted()
				.toArray();
		int expected = seats[0] + 1;
		for (int i = 1; i < seats.length; i++) {
			int current = seats[i];
			if (expected != current)
				return expected;
			expected = current + 1;
		}
		return 0;
	}
}