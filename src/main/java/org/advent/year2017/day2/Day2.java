package org.advent.year2017.day2;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day2 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day2()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 18, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 9),
				new ExpectedAnswers("input.txt", 58975, 308)
		);
	}
	
	List<int[]> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input).stream()
				.map(line -> Arrays.stream(line.split("\\s+")).mapToInt(Integer::parseInt).toArray())
				.toList();
	}
	
	@Override
	public Object part1() {
		return lines.stream()
				.mapToInt(numbers -> Arrays.stream(numbers).max().orElseThrow() - Arrays.stream(numbers).min().orElseThrow())
				.sum();
	}
	
	@Override
	public Object part2() {
		return lines.stream().flatMapToInt(numbers ->
						Arrays.stream(numbers).flatMap(left ->
								Arrays.stream(numbers).filter(right -> left != right && left % right == 0).map(right -> left / right))
				).sum();
	}
}