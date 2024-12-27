package org.advent.year2015.day17;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day17 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day17()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 4, 3),
				new ExpectedAnswers("input.txt", 4372, 4)
		);
	}
	
	int[] containers;
	int liters;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		containers = Utils.readLines(input).stream().mapToInt(Integer::parseInt).toArray();
		liters = switch (file) {
			case "example.txt" -> 25;
			case "input.txt" -> 150;
			default -> throw new IllegalArgumentException("Invalid file: " + file);
		};
	}
	
	@Override
	public Object part1() {
		return countNext(containers, 0, 0, liters).values().stream().mapToInt(i -> i).sum();
	}
	
	@Override
	public Object part2() {
		Map<Integer, Integer> combinations = countNext(containers, 0, 0, liters);
		return combinations.get(combinations.keySet().stream().mapToInt(i -> i).min().orElseThrow());
	}
	
	Map<Integer, Integer> countNext(int[] containers, int index, int containersUsed, int litersLeft) {
		if (litersLeft == 0)
			return Map.of(containersUsed, 1);
		if (litersLeft < 0 || index >= containers.length)
			return Map.of();
		
		int container = containers[index];
		Map<Integer, Integer> currentUsed = countNext(containers, index + 1, containersUsed + 1, litersLeft - container);
		Map<Integer, Integer> currentNotUsed = countNext(containers, index + 1, containersUsed, litersLeft);
		
		HashMap<Integer, Integer> result = new HashMap<>(currentUsed.size() + currentNotUsed.size());
		result.putAll(currentUsed);
		for (Map.Entry<Integer, Integer> entry : currentNotUsed.entrySet())
			result.compute(entry.getKey(), (k, v) -> (v == null ? 0 : v) + entry.getValue());
		
		return result;
	}
}