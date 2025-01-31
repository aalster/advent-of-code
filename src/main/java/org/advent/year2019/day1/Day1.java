package org.advent.year2019.day1;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day1 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day1()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 2, 2),
				new ExpectedAnswers("example2.txt", 654, 966),
				new ExpectedAnswers("example3.txt", 33583, 50346),
				new ExpectedAnswers("input.txt", 3421505, 5129386)
		);
	}
	
	int[] modules;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		modules = Utils.readLines(input).stream().mapToInt(Integer::parseInt).toArray();
	}
	
	@Override
	public Object part1() {
		return Arrays.stream(modules).map(m -> m / 3 - 2).sum();
	}
	
	@Override
	public Object part2() {
		return Arrays.stream(modules)
				.map(m -> {
					int result = 0;
					m = m / 3 - 2;
					while (m > 0) {
						result += m;
						m = m / 3 - 2;
					}
					return result;
				})
				.sum();
	}
}