package org.advent.year2018.day2;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day2 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day2()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 12, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, "fgij"),
				new ExpectedAnswers("input.txt", 4712, "lufjygedpvfbhftxiwnaorzmq")
		);
	}
	
	String[] lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input).toArray(String[]::new);
	}
	
	@Override
	public Object part1() {
		int twos = 0;
		int threes = 0;
		for (String line : lines) {
			int[] counts = new int[26];
			for (char c : line.toCharArray())
				counts[c - 'a']++;
			
			boolean foundTwo = false;
			boolean foundThree = false;
			for (int count : counts) {
				if (count == 2)
					foundTwo = true;
				if (count == 3)
					foundThree = true;
			}
			twos += foundTwo ? 1 : 0;
			threes += foundThree ? 1 : 0;
		}
		return twos * threes;
	}
	
	@Override
	public Object part2() {
		for (int l = 0; l < lines.length; l++) {
			String left = lines[l];
			for (int r = l + 1; r < lines.length; r++) {
				String right = lines[r];
				
				int diffIndex = findSingleDiffIndex(left, right);
				if (diffIndex >= 0)
					return left.substring(0, diffIndex) + left.substring(diffIndex + 1);
			}
		}
		return null;
	}
	
	int findSingleDiffIndex(String left, String right) {
		char[] leftChars = left.toCharArray();
		char[] rightChars = right.toCharArray();
		int diff = -1;
		for (int i = 0; i < leftChars.length; i++) {
			if (leftChars[i] != rightChars[i]) {
				if (diff >= 0)
					return -1;
				diff = i;
			}
		}
		return diff;
	}
}