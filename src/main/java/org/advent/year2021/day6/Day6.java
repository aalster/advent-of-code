package org.advent.year2021.day6;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day6 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day6()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 5934, 26984457539L),
				new ExpectedAnswers("input.txt", 393019, 1757714216975L)
		);
	}
	
	static final int period = 7 - 1;
	static final int initialPeriod = period + 2;
	List<Integer> counters;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		counters = Utils.readLines(input).stream()
				.flatMap(s -> Arrays.stream(s.split(",")))
				.map(Integer::parseInt)
				.toList();
	}
	
	@Override
	public Object part1() {
		return solve(80);
	}
	
	@Override
	public Object part2() {
		return solve(256);
	}
	
	long solve(int steps) {
		List<Fish> fishes = merge(counters.stream().map(c -> new Fish(c, 1)));
		while (steps > 0) {
			fishes = merge(fishes.stream().flatMap(Fish::step));
			steps--;
		}
		return fishes.stream().mapToLong(Fish::duplicates).sum();
	}
	
	List<Fish> merge(Stream<Fish> fishes) {
		return fishes.collect(Collectors.groupingBy(Fish::counter))
				.entrySet().stream()
				.map(e -> new Fish(e.getKey(), e.getValue().stream().mapToLong(Fish::duplicates).sum()))
				.toList();
	}
	
	record Fish(int counter, long duplicates) {
		
		Stream<Fish> step() {
			if (counter > 0)
				return Stream.of(new Fish(counter - 1, duplicates));
			return Stream.of(new Fish(period, duplicates), new Fish(initialPeriod, duplicates));
		}
	}
}