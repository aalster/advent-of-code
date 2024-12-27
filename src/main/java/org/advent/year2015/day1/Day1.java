package org.advent.year2015.day1;

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
				new ExpectedAnswers("example.txt", -3, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 5),
				new ExpectedAnswers("input.txt", 280, 1797)
		);
	}
	
	String line;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		line = input.nextLine();
	}
	
	@Override
	public Object part1() {
		int floor = 0;
		for (char c : line.toCharArray())
			floor += c == '(' ? 1 : -1;
		return floor;
	}
	
	@Override
	public Object part2() {
		int floor = 0;
		int position = 0;
		for (char c : line.toCharArray()) {
			position++;
			floor += c == '(' ? 1 : -1;
			if (floor == -1)
				return position;
		}
		return -1;
	}
}