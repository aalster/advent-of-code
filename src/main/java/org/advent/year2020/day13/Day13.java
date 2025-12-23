package org.advent.year2020.day13;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day13 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day13()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 295, 1068781),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 3417),
				new ExpectedAnswers("example3.txt", ExpectedAnswers.IGNORE, 754018),
				new ExpectedAnswers("example4.txt", ExpectedAnswers.IGNORE, 779210),
				new ExpectedAnswers("example5.txt", ExpectedAnswers.IGNORE, 1261476),
				new ExpectedAnswers("example6.txt", ExpectedAnswers.IGNORE, 1202161486),
				new ExpectedAnswers("input.txt", 4315, 556100168221141L)
		);
	}
	
	int earliestDeparture;
	int[] buses;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		earliestDeparture = Integer.parseInt(input.nextLine());
		buses = Arrays.stream(input.nextLine().split(",")).mapToInt(b -> "x".equals(b) ? 0 : Integer.parseInt(b)).toArray();
	}
	
	@Override
	public Object part1() {
		int minWait = Integer.MAX_VALUE;
		int result = 0;
		for (int bus : buses) {
			if (bus > 0) {
				int lateBy = earliestDeparture % bus;
				int waitTime = bus - lateBy;
				if (waitTime < minWait) {
					minWait = waitTime;
					result = bus * minWait;
				}
			}
		}
		return result;
	}
	
	@Override
	public Object part2() {
		// Chinese Remainder Theorem
		// https://www.youtube.com/watch?v=zIFehsBHB8o
		
		long product = Arrays.stream(buses).filter(bus -> bus > 0).mapToLong(b -> b).reduce(1, (a, b) -> a * b);
		
		int index = 0;
		long result = 0;
		for (int bus : buses) {
			if (bus > 0) {
				long n = product / bus;
				result += Math.floorMod(-index, bus) * n * inverse(n, bus);
			}
			index++;
		}
		return result % product;
	}
	
	long inverse(long multiplier, long mod) {
		multiplier %= mod;
		long result = 1;
		while (result * multiplier % mod != 1)
			result++;
		return result;
	}
}