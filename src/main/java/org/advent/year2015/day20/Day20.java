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
		// У числа будет большое кол-во делителей, поэтому идем только по четным
		for (int house = 2; house < 1000000; house += 2)
			if (countPresents1(house) >= targetPresents)
				return house;
		return 0;
	}
	
	@Override
	public Object part2() {
		for (int house = 2; house < 1000000; house += 2)
			if (countPresents2(house) >= targetPresents)
				return house;
		return 0;
	}
	
	int countPresents1(int house) {
		int presents = 0;
		int sqrt = (int) Math.sqrt(house);
		for (int i = 1; i <= sqrt; i++) {
			if (house % i == 0) {
				int opposite = house / i;
				presents += i;
				if (i != opposite)
					presents += opposite;
			}
		}
		return presents * 10;
	}
	
	int countPresents2(int house) {
		int presents = 0;
		int sqrt = (int) Math.sqrt(house);
		for (int i = 1; i <= sqrt; i++) {
			if (house % i == 0) {
				int opposite = house / i;
				if (opposite < 50)
					presents += i;
				if (i < 50 && i != opposite)
					presents += opposite;
			}
		}
		return presents * 11;
	}
}