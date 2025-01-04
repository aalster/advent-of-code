package org.advent.year2020.day1;

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
				new ExpectedAnswers("example.txt", 514579, 241861950),
				new ExpectedAnswers("input.txt", 211899, 275765682)
		);
	}
	
	List<Integer> numbers;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		numbers = Utils.readLines(input).stream().map(Integer::parseInt).toList();
	}
	
	@Override
	public Object part1() {
		int index = 0;
		for (Integer left : numbers) {
			for (Integer right : numbers.subList(index + 1, numbers.size())) {
				if (left + right == 2020)
					return left * right;
			}
			index++;
		}
		return 0;
	}
	
	@Override
	public Object part2() {
		int index = 0;
		for (Integer left : numbers) {
			for (Integer right : numbers.subList(index + 1, numbers.size())) {
				int index2 = index + 1;
				for (Integer mid : numbers.subList(index2 + 1, numbers.size())) {
					if (left + right + mid == 2020)
						return left * right * mid;
				}
			}
			index++;
		}
		return 0;
	}
}