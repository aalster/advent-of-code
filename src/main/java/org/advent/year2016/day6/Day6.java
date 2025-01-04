package org.advent.year2016.day6;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
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
				new ExpectedAnswers("example.txt", "easter", "advent"),
				new ExpectedAnswers("input.txt", "ursvoerv", "vomaypnn")
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
		return solve(lines, chars -> Arrays.stream(chars).max().orElseThrow());
	}
	
	@Override
	public Object part2() {
		return solve(lines, chars -> Arrays.stream(chars).filter(c -> c > 0).min().orElseThrow());
	}
	
	String solve(List<String> lines, Function<int[], Integer> frequencySelector) {
		int[][] frequency = new int[lines.getFirst().length()][26];
		for (String line : lines) {
			char[] chars = line.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				frequency[i][c - 'a']++;
			}
		}
		int[] selectedFrequencies = Arrays.stream(frequency).map(frequencySelector).mapToInt(i -> i).toArray();
		
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < selectedFrequencies.length; i++)
			result.append((char) ('a' + ArrayUtils.indexOf(frequency[i], selectedFrequencies[i])));
		return result.toString();
	}
}