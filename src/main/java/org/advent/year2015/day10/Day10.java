package org.advent.year2015.day10;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day10 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day10()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", ExpectedAnswers.IGNORE, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 360154, 5103798)
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
		return solve(line, 40);
	}
	
	@Override
	public Object part2() {
		return solve(line, 50);
	}
	
	long solve(String line, int count) {
		while (count > 0) {
			StringBuilder next = new StringBuilder(line.length() * 2);
			char[] chars = line.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				int repeats = 1;
				for (int k = i + 1; k < chars.length; k++) {
					if (c != chars[k])
						break;
					repeats++;
				}
				next.append(repeats).append(c);
				i += repeats - 1;
			}
			line = next.toString();
			count--;
		}
		return line.length();
	}
}