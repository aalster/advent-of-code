package org.advent.year2021.day3;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day3 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day3()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 198, 230),
				new ExpectedAnswers("input.txt", 4118544, 3832770)
		);
	}
	
	List<String> numbers;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		numbers = Utils.readLines(input);
	}
	
	@Override
	public Object part1() {
		int gamma = 0;
		int epsilon = 0;
		for (int i = 0; i < numbers.getFirst().length(); i++) {
			int mostCommon = mostCommon(numbers, i);
			gamma = gamma * 2 + mostCommon;
			epsilon = epsilon * 2 + 1 - mostCommon;
		}
		return gamma * epsilon;
	}
	
	@Override
	public Object part2() {
		return oxygen(numbers) * co2(numbers);
	}
	
	int oxygen(List<String> numbers) {
		int oxygen = 0;
		for (int i = 0; i < numbers.getFirst().length(); i++) {
			int index = i;
			int mostCommon = mostCommon(numbers, i);
			oxygen = oxygen * 2 + mostCommon;
			numbers = numbers.stream().filter(n -> n.charAt(index) == mostCommon + '0').toList();
		}
		return oxygen;
	}
	
	int co2(List<String> numbers) {
		int oxygen = 0;
		for (int i = 0; i < numbers.getFirst().length(); i++) {
			if (numbers.size() == 1)
				return Integer.parseInt(numbers.getFirst(), 2);
			
			int index = i;
			int leastCommon = 1 - mostCommon(numbers, i);
			oxygen = oxygen * 2 + leastCommon;
			numbers = numbers.stream().filter(n -> n.charAt(index) == leastCommon + '0').toList();
		}
		return oxygen;
	}
	
	int mostCommon(List<String> numbers, int index) {
		int count = 0;
		for (String number : numbers)
			count += number.charAt(index) == '1' ? 1 : -1;
		return count >= 0 ? 1 : 0;
	}
}