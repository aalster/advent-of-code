package org.advent.year2015.day16;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiPredicate;

public class Day16 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day16()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 213, 323)
		);
	}
	
	List<String> lines;
	Map<String, Integer> clues = Map.of(
			"children", 3,
			"cats", 7,
			"samoyeds", 2,
			"pomeranians", 3,
			"akitas", 0,
			"vizslas", 0,
			"goldfish", 5,
			"trees", 3,
			"cars", 2,
			"perfumes", 1);
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
	}
	
	@Override
	public Object part1() {
		nextAunt: for (String line : lines) {
			String[] split = line.split(": ", 2);
			for (String entry : split[1].split(", ")) {
				String[] entrySplit = entry.split(": ");
				if (clues.get(entrySplit[0]) != Integer.parseInt(entrySplit[1]))
					continue nextAunt;
			}
			return Integer.parseInt(split[0].replace("Sue ", ""));
		}
		return 0;
	}
	
	@Override
	public Object part2() {
		BiPredicate<Integer, Integer> eq = Integer::equals;
		Map<String, BiPredicate<Integer, Integer>> comparisons = Map.of(
				"cats", (clue, aunt) -> clue < aunt,
				"trees", (clue, aunt) -> clue < aunt,
				"pomeranians", (clue, aunt) -> clue > aunt,
				"goldfish", (clue, aunt) -> clue > aunt
		);
		
		nextAunt: for (String line : lines) {
			String[] split = line.split(": ", 2);
			for (String entry : split[1].split(", ")) {
				String[] entrySplit = entry.split(": ");
				BiPredicate<Integer, Integer> comparison = comparisons.getOrDefault(entrySplit[0], eq);
				if (!comparison.test(clues.get(entrySplit[0]), Integer.parseInt(entrySplit[1])))
					continue nextAunt;
			}
			return Integer.parseInt(split[0].replace("Sue ", ""));
		}
		return 0;
	}
}