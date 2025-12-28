package org.advent.year2020.day15;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day15 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day15()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 436, 175594),
				new ExpectedAnswers("example2.txt", 1, 2578),
				new ExpectedAnswers("example3.txt", 10, 3544142),
				new ExpectedAnswers("example4.txt", 27, 261214),
				new ExpectedAnswers("example5.txt", 78, 6895259),
				new ExpectedAnswers("example6.txt", 438, 18),
				new ExpectedAnswers("example7.txt", 1836, 362),
				new ExpectedAnswers("input.txt", 1111, 48568)
		);
	}
	
	int[] numbers;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		numbers = Arrays.stream(input.nextLine().split(",")).mapToInt(Integer::parseInt).toArray();
	}
	
	@Override
	public Object part1() {
		return solve(2020);
	}
	
	@Override
	public Object part2() {
		return solve(30000000);
	}
	
	private int solve(int turns) {
		int[] indexes = new int[turns];
		Arrays.fill(indexes, -1);
		
		int index = 0;
		for (int number : numbers)
			indexes[number] = index++;
		
		int lastNumber = numbers[numbers.length - 1];
		int lastIndex = -1;
		while (index < turns) {
			lastNumber = lastIndex >= 0 ? index - lastIndex - 1 : 0;
			
			lastIndex = indexes[lastNumber];
			indexes[lastNumber] = index;
			index++;
		}
		return lastNumber;
	}
}