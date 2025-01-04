package org.advent.year2021.day7;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Day7 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day7()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 37, 168),
				new ExpectedAnswers("input.txt", 342534, 94004208)
		);
	}
	
	int[] crabs;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<Integer> crabsPositions = Utils.readLines(input).stream()
				.flatMap(line -> Arrays.stream(line.split(",")))
				.map(Integer::parseInt)
				.toList();
		
		crabs = new int[crabsPositions.stream().mapToInt(p -> p).max().orElseThrow() + 1];
		for (int crabsPosition : crabsPositions)
			crabs[crabsPosition]++;
	}
	
	@Override
	public Object part1() {
		int crabsCount = IntStream.of(crabs).sum();
		long bestFuel = 0;
		for (int position = 0; position < crabs.length; position++)
			bestFuel += (long) position * crabs[position];
		
		long fuel = bestFuel;
		int crabsOnLeft = crabs[0];
		for (int position = 1; position < crabs.length; position++) {
			fuel += 2L * crabsOnLeft - crabsCount;
			crabsOnLeft += crabs[position];
			if (fuel < bestFuel)
				bestFuel = fuel;
		}
		return bestFuel;
	}
	
	@Override
	public Object part2() {
		long bestFuel = 0;
		
		int crabsOnRight = IntStream.of(crabs).sum();
		for (int position = 0; position < crabs.length; position++) {
			bestFuel += (long) position * crabsOnRight;
			crabsOnRight -= crabs[position];
		}
		
		long fuel = bestFuel;
		for (int position = 1; position < crabs.length; position++) {
			for (int i = 0; i < crabs.length; i++)
				fuel += (long) crabs[i] * (position - i + (position <= i ? -1 : 0));
			
			if (fuel < bestFuel)
				bestFuel = fuel;
		}
		return bestFuel;
	}
}