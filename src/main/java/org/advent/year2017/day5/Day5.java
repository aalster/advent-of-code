package org.advent.year2017.day5;

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
				new ExpectedAnswers("example.txt", 5, 10),
				new ExpectedAnswers("input.txt", 359348, 27688760)
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
		int[] numbers = lines.stream().mapToInt(Integer::parseInt).toArray();
		int index = 0;
		int steps = 0;
		while (0 <= index && index < numbers.length) {
			index += numbers[index]++;
			steps++;
		}
		return steps;
	}
	
	@Override
	public Object part2() {
		int[] numbers = lines.stream().mapToInt(Integer::parseInt).toArray();
		int index = 0;
		int steps = 0;
		while (0 <= index && index < numbers.length) {
			int jump = numbers[index];
			numbers[index] += jump < 3 ? 1 : -1;
			index += jump;
			steps++;
		}
		return steps;
	}
}