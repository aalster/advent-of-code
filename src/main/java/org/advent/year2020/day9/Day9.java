package org.advent.year2020.day9;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Scanner;

public class Day9 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day9()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 127, 62),
				new ExpectedAnswers("input.txt", 88311122, 13549369)
		);
	}
	
	int preamble;
	long[] numbers;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		numbers = Utils.readLines(input).stream().mapToLong(Long::parseLong).toArray();
		preamble = switch (file) {
			case "example.txt" -> 5;
			case "input.txt" -> 25;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Long part1() {
		main: for (int i = preamble; i < numbers.length; i++) {
			long number = numbers[i];
			for (int j = i - preamble; j < i; j++) {
				long left = numbers[j];
				for (int k = j + 1; k < i; k++)
					if (left + numbers[k] == number)
						continue main;
			}
			return number;
		}
		throw new RuntimeException("No number found");
	}
	
	@Override
	public Object part2() {
		long number = part1();
		int start = 0;
		int end = 1;
		long sum = numbers[start];
		while (sum != number && end < numbers.length) {
			if (sum > number)
				sum -= numbers[start++];
			else
				sum += numbers[end++];
		}
		LongSummaryStatistics stats = Arrays.stream(numbers, start, end).summaryStatistics();
		return stats.getMin() + stats.getMax();
	}
}