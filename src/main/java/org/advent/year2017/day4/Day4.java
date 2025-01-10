package org.advent.year2017.day4;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day4 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day4()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 2, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 3),
				new ExpectedAnswers("input.txt", 383, 265)
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
		return lines.stream()
				.map(line -> line.split(" "))
				.filter(split -> split.length == Arrays.stream(split).distinct().count())
				.count();
	}
	
	@Override
	public Object part2() {
		Function<String, Map<Integer, Long>> charsCountsMapping = s -> s.chars().boxed()
				.collect(Collectors.groupingBy(c -> c, Collectors.counting()));
		return lines.stream()
				.map(line -> line.split(" "))
				.filter(split -> split.length == Arrays.stream(split).map(charsCountsMapping).distinct().count())
				.count();
	}
}