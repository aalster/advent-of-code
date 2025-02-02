package org.advent.year2019.day4;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Day4 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day4()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 530, 324)
		);
	}
	
	int from;
	int to;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		String[] split = input.nextLine().split("-");
		from = Integer.parseInt(split[0]);
		to = Integer.parseInt(split[1]);
	}
	
	@Override
	public Object part1() {
		return IntStream.rangeClosed(from, to).filter(this::matches1).count();
	}
	
	@Override
	public Object part2() {
		return IntStream.rangeClosed(from, to).filter(this::matches1).filter(this::matches2).count();
	}
	
	boolean matches1(int number) {
		boolean hasRepeat = false;
		int rightDigit = 10;
		while (number > 0) {
			int leftDigit = number % 10;
			if (rightDigit < leftDigit)
				return false;
			hasRepeat = hasRepeat || leftDigit == rightDigit;
			
			number /= 10;
			rightDigit = leftDigit;
		}
		return hasRepeat;
	}
	
	static int[] counts = new int[10];
	
	boolean matches2(int number) {
		Arrays.fill(counts, 0);
		while (number > 0) {
			counts[number % 10]++;
			number /= 10;
		}
		for (int count : counts)
			if (count == 2)
				return true;
		return false;
	}
}