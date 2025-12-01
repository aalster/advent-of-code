package org.advent.year2025.day1;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day1 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day1()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 3, 6),
				new ExpectedAnswers("input.txt", 1081, 6689)
		);
	}
	
	int[] deltas;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		deltas = Utils.readLines(input).stream()
				.mapToInt(l -> (l.charAt(0) == 'L' ? -1 : 1) * Integer.parseInt(l.substring(1)))
				.toArray();
	}
	
	@Override
	public Object part1() {
		int number = 50;
		int zeroes = 0;
		for (int delta : deltas) {
			number = Math.floorMod(number + delta, 100);
			if (number == 0)
				zeroes++;
		}
		return zeroes;
	}
	
	@Override
	public Object part2() {
		int number = 50;
		int zeroes = 0;
		for (int delta : deltas) {
			int nextOverflowed = number + delta;
			zeroes += Math.abs(Math.floorDiv(nextOverflowed, 100));
			int next = Math.floorMod(nextOverflowed, 100);
			
			if (delta < 0) {
				if (number == 0)
					zeroes--;
				if (next == 0)
					zeroes++;
			}
			number = next;
		}
		return zeroes;
	}
}