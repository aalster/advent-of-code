package org.advent.year2015.day20;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day20 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day20()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 6, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 776160, 786240)
		);
	}
	
	int targetPresents;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		targetPresents = Integer.parseInt(input.nextLine());
	}
	
	@Override
	public Object part1() {
		for (int i = 1; i < 1000000; i++)
			if (countPresents1(i) >= targetPresents)
				return i;
		return 0;
	}
	
	@Override
	public Object part2() {
		for (int i = 1; i < 1000000; i++)
			if (countPresents2(i) >= targetPresents)
				return i;
		return 0;
	}
	
	int countPresents1(int house) {
		int presents = 0;
		double sqrt = Math.sqrt(house);
		for (int i = 1; i <= sqrt; i++)
			if (house % i == 0)
				presents += i + house / i;
		return presents * 10;
	}
	
	int countPresents2(int house) {
		int presents = 0;
		double sqrt = Math.sqrt(house);
		for (int i = 1; i <= sqrt; i++) {
			if (house % i == 0) {
				int divided = house / i;
				if (divided < 50)
					presents += i;
				if (i < 50)
					presents += divided;
			}
		}
		return presents * 11;
	}
}