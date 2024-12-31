package org.advent.year2022.day6;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class Day6 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day6()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 5, 23),
				new ExpectedAnswers("example2.txt", 6, 23),
				new ExpectedAnswers("example3.txt", 10, 29),
				new ExpectedAnswers("example4.txt", 11, 26),
				new ExpectedAnswers("example5.txt", ExpectedAnswers.IGNORE, 19),
				new ExpectedAnswers("input.txt", 1100, 2421)
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
		return solve(line, 4);
	}
	
	@Override
	public Object part2() {
		return solve(line, 14);
	}
	
	int solve(String line, int size) {
		if (line.length() < size)
			return 0;
		
		Function<List<Integer>, Boolean> different = list -> list.size() == new HashSet<>(list).size();
		
		List<Integer> chars = new ArrayList<>(line.substring(0, size).chars().boxed().toList());
		if (different.apply(chars))
			return size + 1;
		for (int i = size; i < line.length(); i++) {
			int c = line.charAt(i);
			chars.add(c);
			chars.removeFirst();
			if (different.apply(chars))
				return i + 1;
		}
		return 0;
	}
}