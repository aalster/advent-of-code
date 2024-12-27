package org.advent.year2015.day8;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day8 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day8()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 12, 19),
				new ExpectedAnswers("input.txt", 1333, 2046)
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
		int diff = lines.size() * 2;
		for (String line : lines) {
			char[] chars = line.toCharArray();
			for (int i = 1; i < chars.length - 1; i++) {
				if (chars[i] != '\\')
					continue;
				diff += chars[i + 1] == 'x' ? 3 : 1;
				if (chars[i + 1] == '\\')
					i++;
			}
		}
		return diff;
	}
	
	@Override
	public Object part2() {
		int diff = lines.size() * 2;
		for (String line : lines)
			for (char c : line.toCharArray())
				diff += c == '\"' || c == '\\' ? 1 : 0;
		return diff;
	}
}