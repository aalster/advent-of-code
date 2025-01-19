package org.advent.year2018.day1;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Day1 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day1()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 3, 2),
				new ExpectedAnswers("example2.txt", 3, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 0, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example4.txt", -6, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example5.txt", ExpectedAnswers.IGNORE, 0),
				new ExpectedAnswers("example6.txt", ExpectedAnswers.IGNORE, 10),
				new ExpectedAnswers("example7.txt", ExpectedAnswers.IGNORE, 5),
				new ExpectedAnswers("example8.txt", ExpectedAnswers.IGNORE, 14),
				new ExpectedAnswers("input.txt", 477, 390)
		);
	}
	
	int[] numbers;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		numbers = Utils.readLines(input).stream()
				.flatMap(line -> Arrays.stream(line.split(", ")))
				.mapToInt(Integer::parseInt)
				.toArray();
	}
	
	@Override
	public Object part1() {
		return Arrays.stream(numbers).sum();
	}
	
	@Override
	public Object part2() {
		int current = 0;
		Set<Integer> seen = new HashSet<>();
		seen.add(current);
		int index = 0;
		while (true) {
			current += numbers[index % numbers.length];
			if (!seen.add(current))
				return current;
			index++;
		}
	}
}