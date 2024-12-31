package org.advent.year2022.day1;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Day1 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day1()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 24000, 45000),
				new ExpectedAnswers("input.txt", 69912, 208180)
		);
	}
	
	List<List<Long>> allCalories;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		allCalories = new ArrayList<>();
		while (input.hasNext()) {
			List<Long> calories = new ArrayList<>();
			while (input.hasNext()) {
				String next = input.nextLine();
				if (next.isEmpty())
					break;
				calories.add(Long.parseLong(next));
			}
			allCalories.add(calories);
		}
	}
	
	@Override
	public Object part1() {
		return solve(allCalories, 1);
	}
	
	@Override
	public Object part2() {
		return solve(allCalories, 3);
	}
	
	long solve(List<List<Long>> allCalories, int topCount) {
		return allCalories.stream()
				.map(c -> c.stream().mapToLong(l -> l).sum())
				.sorted(Collections.reverseOrder())
				.limit(topCount)
				.mapToLong(l -> l)
				.sum();
	}
}