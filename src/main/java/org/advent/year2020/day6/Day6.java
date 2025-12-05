package org.advent.year2020.day6;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Day6 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day6()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 11, 6),
				new ExpectedAnswers("input.txt", 6457, 3260)
		);
	}
	
	List<List<String>> groups;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		groups = Utils.splitByEmptyLine(Utils.readLines(input));
	}
	
	@Override
	public Object part1() {
		return groups.stream().mapToLong(g -> String.join("", g).chars().distinct().count()).sum();
	}
	
	@Override
	public Object part2() {
		return groups.stream().mapToLong(this::commonAnswers).sum();
	}
	
	private long commonAnswers(List<String> group) {
		int[] answers = new int['z' - 'a' + 1];
		for (String person : group)
			for (char c : person.toCharArray())
				answers[c - 'a']++;
		return IntStream.of(answers).filter(a -> a == group.size()).count();
	}
}