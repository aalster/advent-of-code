package org.advent.year2025.day6;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day6 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day6()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 4277556, 3263827),
				new ExpectedAnswers("input.txt", 6417439773370L, 11044319475191L)
		);
	}
	
	String[] rows;
	int[] operations;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<String> lines = new ArrayList<>(Utils.readLines(input));
		operations = lines.removeLast().chars().filter(c -> c != ' ').toArray();
		rows = lines.toArray(String[]::new);
	}
	
	@Override
	public Object part1() {
		int[][] numbers = Arrays.stream(rows)
				.map(l -> Arrays.stream(l.split(" +")).filter(StringUtils::isNotBlank).mapToInt(Integer::parseInt).toArray())
				.toArray(int[][]::new);
		return IntStream.range(0, operations.length)
				.mapToLong(i -> Arrays.stream(numbers)
						.mapToLong(number -> number[i])
						.reduce(operations[i] == '*' ? (a, b) -> a * b : (a, b) -> a + b).orElse(0))
				.sum();
	}
	
	@Override
	public Object part2() {
		int columns = Stream.of(rows).mapToInt(String::length).max().orElseThrow();
		long total = 0;
		int column = 0;
		for (int operation : operations) {
			long groupTotal = operation == '*' ? 1 : 0;
			
			while (column < columns) {
				int number = 0;
				for (String row : rows) {
					char digit = column < row.length() ? row.charAt(column) : ' ';
					if (digit != ' ')
						number = number * 10 + digit - '0';
				}
				column++;
				if (number == 0)
					break;
				groupTotal = operation == '*' ? groupTotal * number : groupTotal + number;
			}
			total += groupTotal;
		}
		return total;
	}
}