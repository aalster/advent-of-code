package org.advent.year2016.day3;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Day3 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day3()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 0, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 6),
				new ExpectedAnswers("input.txt", 869, 1544)
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
				.map(line -> Arrays.stream(line.split(" "))
						.filter(StringUtils::isNotBlank)
						.mapToInt(Integer::parseInt)
						.toArray())
				.filter(sides -> validTriangle(sides[0], sides[1], sides[2]))
				.count();
	}
	
	@Override
	public Object part2() {
		List<int[]> rows = lines.stream()
				.map(line -> Arrays.stream(line.split(" "))
						.filter(StringUtils::isNotBlank)
						.mapToInt(Integer::parseInt)
						.toArray())
				.toList();
		
		int valid = 0;
		Iterator<int[]> iterator = rows.iterator();
		while (iterator.hasNext()) {
			int[] row1 = iterator.next();
			int[] row2 = iterator.next();
			int[] row3 = iterator.next();
			for (int i = 0; i < 3; i++)
				valid += validTriangle(row1[i], row2[i], row3[i]) ? 1 : 0;
		}
		return valid;
	}
	
	boolean validTriangle(int a, int b, int c) {
		return a + b > c && b + c > a && c + a > b;
	}
}