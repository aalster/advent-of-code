package org.advent.year2019.day16;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day16 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day16()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 24176176, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 73745418, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 52432133, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example4.txt", ExpectedAnswers.IGNORE, 84462026),
				new ExpectedAnswers("example5.txt", ExpectedAnswers.IGNORE, 78725270),
				new ExpectedAnswers("example6.txt", ExpectedAnswers.IGNORE, 53553731),
				new ExpectedAnswers("input.txt", 42945143, 99974970)
		);
	}
	
	int[] numbers;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		numbers = input.nextLine().chars().map(n -> n - '0').toArray();
	}
	
	@Override
	public Object part1() {
		for (int steps = 0; steps < 100; steps++)
			numbers = next(numbers);
		return extractDigits(numbers, 0, 8);
	}
	
	@Override
	public Object part2() {
		int repeats = 10000;
		int offset = extractDigits(numbers, 0, 7);
		
		repeats -= offset / numbers.length;
		offset = offset % numbers.length;
		
		numbers = expand(numbers, repeats);
		
		for (int steps = 0; steps < 100; steps++)
			numbers = nextFast(numbers, offset);
		return extractDigits(numbers, offset, 8);
	}
	
	int[] expand(int[] numbers, int repeats) {
		int[] result = new int[numbers.length * repeats];
		for (int i = 0; i < repeats; i++)
			System.arraycopy(numbers, 0, result, i * numbers.length, numbers.length);
		return result;
	}
	
	int[] next(int[] numbers) {
		int[] pattern = new int[] {0, 1, 0, -1};
		int[] result = new int[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			int next = 0;
			for (int j = 0; j < numbers.length; j++)
				next += numbers[j] * pattern[Math.floorMod((j + 1) / (i + 1), 4)];
			result[i] = Math.abs(next) % 10;
		}
		return result;
	}
	
	// Работает только для правой половины исходного массива.
	// Для оптимизации работает в том же массиве.
	// https://work.njae.me.uk/2019/12/20/advent-of-code-2019-day-16/
	int[] nextFast(int[] numbers, int minIndex) {
		int right = 0;
		for (int i = numbers.length - 1; i >= minIndex; i--)
			numbers[i] = right = (numbers[i] + right) % 10;
		return numbers;
	}
	
	int extractDigits(int[] numbers, int start, int count) {
		int result = 0;
		for (int i = start; i < start + count; i++)
			result = result * 10 + numbers[i];
		return result;
	}
}