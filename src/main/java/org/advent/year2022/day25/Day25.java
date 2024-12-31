package org.advent.year2022.day25;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Scanner;

public class Day25 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day25()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "2=-1=0", ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", "2=112--220-=-00=-=20", ExpectedAnswers.IGNORE)
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
		return toSnafu(lines.stream().mapToLong(this::fromSnafu).sum());
	}
	
	@Override
	public Object part2() {
		return null;
	}
	
	char[] symbols = {'=', '-', '0', '1', '2'};
	
	long fromSnafu(String s) {
		long number = 0;
		for (int i = 0; i < s.length(); i++) {
			int place = ArrayUtils.indexOf(symbols, s.charAt(i)) - 2;
			number = number * 5 + place;
		}
		return number;
	}
	
	String toSnafu(long number) {
		StringBuilder result = new StringBuilder();
		while (number > 0) {
			int place = (int) (number % 5);
			result.insert(0, symbols[(place + 2) % 5]);
			number = number / 5 + (place > 2 ? 1 : 0);
		}
		return result.toString();
	}
}