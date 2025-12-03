package org.advent.year2025.day3;

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
				new ExpectedAnswers("example.txt", 357, 3121910778619L),
				new ExpectedAnswers("input.txt", 17166, 169077317650774L)
		);
	}
	
	List<int[]> banks;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		banks = Utils.readLines(input).stream().map(l -> l.chars().map(c -> c - '0').toArray()).toList();
	}
	
	@Override
	public Object part1() {
		return banks.stream().mapToLong(bank -> maxJoltage(bank, 2)).sum();
	}
	
	@Override
	public Object part2() {
		return banks.stream().mapToLong(bank -> maxJoltage(bank, 12)).sum();
	}
	
	private long maxJoltage(int[] bank, int batteries) {
		long totalJoltage = 0;
		int batteryIndex = -1;
		while (batteries-- > 0) {
			batteryIndex = maxIndex(bank, batteryIndex + 1, bank.length - batteries);
			totalJoltage = totalJoltage * 10 + bank[batteryIndex];
		}
		return totalJoltage;
	}
	
	int maxIndex(int[] digits, int indexFrom, int indexTo) {
		int max = 0;
		int maxIndex = -1;
		for (int i = indexFrom; i < indexTo; i++) {
			int digit = digits[i];
			if (digit > max) {
				max = digit;
				maxIndex = i;
			}
		}
		return maxIndex;
	}
}