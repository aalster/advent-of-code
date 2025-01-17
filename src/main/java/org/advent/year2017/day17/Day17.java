package org.advent.year2017.day17;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day17 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day17()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 638, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 1244, 11162912)
		);
	}
	
	int step;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		step = input.nextInt();
	}
	
	@Override
	public Object part1() {
		List<Integer> numbers = new ArrayList<>(2018);
		numbers.add(0);
		int index = 0;
		for (int n = 1; n <= 2017; n++) {
			index = (index + step) % numbers.size() + 1;
			numbers.add(index, n);
		}
		return numbers.get((index + 1) % numbers.size());
	}
	
	@Override
	public Object part2() {
		int result = 0;
		int index = 0;
		for (int n = 1; n <= 50000000; n++) {
			index = (index + step) % n + 1;
			if (index == 1)
				result = n;
		}
		return result;
	}
}