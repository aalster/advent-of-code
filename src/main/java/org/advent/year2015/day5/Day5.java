package org.advent.year2015.day5;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Day5 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day5()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 2, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 2),
				new ExpectedAnswers("input.txt", 238, 69)
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
		List<String> bads = List.of("ab", "cd", "pq", "xy");
		Set<Character> vowels = Set.of('a', 'e', 'i', 'o', 'u');
		long result = 0;
		for (String line : lines) {
			if (bads.stream().anyMatch(line::contains))
				continue;
			
			int vowelsCount = 0;
			boolean repeats = false;
			char previous = ' ';
			for (char current : line.toCharArray()) {
				if (vowels.contains(current))
					vowelsCount++;
				if (previous == current)
					repeats = true;
				previous = current;
			}
			if (repeats && vowelsCount > 2)
				result++;
		}
		return result;
	}
	
	@Override
	public Object part2() {
		long result = 0;
		for (String line : lines) {
			Map<String, List<Integer>> pairs = new HashMap<>();
			boolean repeats = false;
			char prePrevious = ' ';
			char previous = ' ';
			int index = 0;
			for (char current : line.toCharArray()) {
				if (prePrevious == current)
					repeats = true;
				pairs.computeIfAbsent("" + previous + current, k -> new ArrayList<>()).add(index);
				
				prePrevious = previous;
				previous = current;
				index++;
			}
			boolean hasPairs = pairs.values().stream()
					.filter(l -> l.size() > 1)
					.mapToInt(l -> l.getLast() - l.getFirst())
					.anyMatch(n -> n > 1);
			if (repeats && hasPairs)
				result++;
		}
		return result;
	}
}