package org.advent.year2023.day4;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day4 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day4()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 13, 30),
				new ExpectedAnswers("input.txt", 20407, 23806951)
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
		int result = 0;
		for (String line : lines)
			result += 1 << (count(line) - 1);
		return result;
	}
	
	@Override
	public Object part2() {
		int[] copies = new int[lines.size()];
		Arrays.fill(copies, 1);
		
		int id = 0;
		for (String line : lines) {
			int count = count(line);
			int instances = copies[id];
			for (int i = id + 1; i <= id + count; i++)
				copies[i] += instances;
			id++;
		}
		return Arrays.stream(copies).sum();
	}
	
	int count(String line) {
		String[] split = line.split(":")[1].split("\\|");
		Set<Integer> winningsNumbers = Arrays.stream(split[0].split(" "))
				.filter(StringUtils::isNotBlank)
				.map(Integer::parseInt)
				.collect(Collectors.toSet());
		return (int) Arrays.stream(split[1].split(" "))
				.filter(StringUtils::isNotBlank)
				.map(Integer::parseInt)
				.filter(winningsNumbers::contains)
				.count();
	}
}