package org.advent.year2017.day15;

import lombok.AllArgsConstructor;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day15 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day15()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 588, 309),
				new ExpectedAnswers("input.txt", 650, 336)
		);
	}
	
	int initialA;
	int initialB;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		initialA = Integer.parseInt(input.nextLine().split("with ")[1]);
		initialB = Integer.parseInt(input.nextLine().split("with ")[1]);
	}
	
	@Override
	public Object part1() {
		Generator a = new Generator(16807, 3, initialA);
		Generator b = new Generator(48271, 7, initialB);
		
		int count = 0;
		for (int i = 0; i < 40_000_000; i++)
			if (a.next() == b.next())
				count++;
		return count;
	}
	
	@Override
	public Object part2() {
		Generator a = new Generator(16807, 3, initialA);
		Generator b = new Generator(48271, 7, initialB);
		
		int count = 0;
		for (int i = 0; i < 5_000_000; i++)
			if (a.next2() == b.next2())
				count++;
		return count;
	}
	
	@AllArgsConstructor
	static class Generator {
		final long factor;
		final long mask;
		long value;
		
		long next() {
			value = (value * factor) % 2147483647;
			return value & 0xFFFF;
		}
		
		long next2() {
			do {
				next();
			} while ((value & mask) != 0);
			return value & 0xFFFF;
		}
	}
}