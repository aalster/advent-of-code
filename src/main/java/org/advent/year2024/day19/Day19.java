package org.advent.year2024.day19;

import org.advent.common.Utils;
import org.advent.runner.AbstractDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day19 extends AbstractDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day19()).run("input.txt");
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", null, null),
				new ExpectedAnswers("input.txt", null, null)
		);
	}
	
	List<String> towels;
	List<String> designs;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		towels = Arrays.stream(input.nextLine().split(", ")).toList();
		input.nextLine();
		designs = Utils.readLines(input);
	}
	
	@Override
	public Object part1() {
		return designs.stream().filter(d -> possible(d, towels)).count();
	}
	
	@Override
	public Object part2() {
		Map<String, Long> cache = new HashMap<>();
		return designs.stream().mapToLong(design -> variants(design, towels, cache)).sum();
	}
	
	boolean possible(String design, List<String> towels) {
		if (design.isEmpty())
			return true;
		return towels.stream()
				.filter(towel -> towel.length() <= design.length() && design.startsWith(towel))
				.anyMatch(towel -> possible(design.substring(towel.length()), towels));
	}
	
	long variants(String design, List<String> towels, Map<String, Long> cache) {
		if (design.isEmpty())
			return 1;
		Long result = cache.get(design);
		if (result == null) {
			result = towels.stream()
					.filter(towel -> towel.length() <= design.length() && design.startsWith(towel))
					.mapToLong(towel -> variants(design.substring(towel.length()), towels, cache))
					.sum();
			cache.put(design, result);
		}
		return result;
	}
}