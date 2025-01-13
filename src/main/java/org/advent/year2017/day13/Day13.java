package org.advent.year2017.day13;

import org.advent.common.NumbersAdventUtils;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day13 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day13()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 24, 10),
				new ExpectedAnswers("input.txt", 1876, 3964778)
		);
	}
	
	Map<Integer, Integer> depths;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		depths = Utils.readLines(input).stream().map(line -> line.split(": "))
				.collect(Collectors.toMap(s -> Integer.parseInt(s[0]), s -> Integer.parseInt(s[1])));
	}
	
	@Override
	public Object part1() {
		return depths.entrySet().stream()
				.filter(e -> e.getKey() % ((e.getValue() - 1) * 2) == 0)
				.mapToInt(e -> e.getKey() * e.getValue())
				.sum();
	}
	
	@Override
	public Object part2() {
		Map<Integer, Set<Integer>> forbiddenStarts = new HashMap<>();
		for (Map.Entry<Integer, Integer> entry : depths.entrySet()) {
			int period = (entry.getValue() - 1) * 2;
			forbiddenStarts.computeIfAbsent(period, k -> new HashSet<>()).add(Math.floorMod(-entry.getKey(), period));
		}
		List<Period> periods = new ArrayList<>(forbiddenStarts.entrySet().stream()
				.map(e -> Period.fromForbiddenStarts(e.getKey(), e.getValue()))
				.sorted(Comparator.comparing(p -> p.possibleStarts.size()))
				.toList());
		
		Period combined = periods.removeFirst();
		while (!periods.isEmpty())
			combined = combined.combine(periods.removeFirst());
		return combined.possibleStarts.stream().mapToInt(i -> i).min().orElseThrow();
	}
	
	record Period(int period, Set<Integer> possibleStarts) {
		
		Period combine(Period other) {
			int lcm = (int) NumbersAdventUtils.lcm(new int[] {period, other.period});
			int multiplier = lcm / period;
			Set<Integer> nextPossibleStarts = possibleStarts.stream()
					.flatMap(s -> IntStream.range(0, multiplier).mapToObj(m -> s + m * period))
					.filter(s -> other.possibleStarts.contains(s % other.period))
					.collect(Collectors.toSet());
			return new Period(lcm, nextPossibleStarts);
		}
		
		static Period fromForbiddenStarts(int period, Set<Integer> forbiddenStarts) {
			return new Period(period, IntStream.range(0, period).boxed().filter(i -> !forbiddenStarts.contains(i)).collect(Collectors.toSet()));
		}
	}
}