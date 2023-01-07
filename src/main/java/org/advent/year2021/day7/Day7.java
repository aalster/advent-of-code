package org.advent.year2021.day7;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Day7 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day7.class, "input.txt");
		List<Integer> crabsPositions = new ArrayList<>();
		while (input.hasNext()) {
			crabsPositions.addAll(Arrays.stream(input.nextLine().split(",")).map(Integer::parseInt).toList());
		}
		
		int[] crabs = new int[crabsPositions.stream().mapToInt(p -> p).max().orElseThrow() + 1];
		for (Integer crabsPosition : crabsPositions)
			crabs[crabsPosition]++;
		
		System.out.println("Answer 1: " + part1(crabs));
		System.out.println("Answer 2: " + part2(crabs));
	}
	
	private static long part1(int[] crabs) {
		int crabsCount = IntStream.of(crabs).sum();
		int bestPosition = 0;
		long bestFuel = 0;
		for (int position = 0; position < crabs.length; position++) {
			bestFuel += (long) position * crabs[position];
		}
		
		long fuel = bestFuel;
		int crabsOnLeft = crabs[0];
		for (int position = 1; position < crabs.length; position++) {
			fuel += 2L * crabsOnLeft - crabsCount;
			crabsOnLeft += crabs[position];
			if (fuel < bestFuel) {
				bestFuel = fuel;
				bestPosition = position;
			}
		}
		
		System.out.println(bestPosition + " " + bestFuel);
		return bestFuel;
	}
	
	private static long part2(int[] crabs) {
		int bestPosition = 0;
		long bestFuel = 0;
		
		int crabsOnRight = IntStream.of(crabs).sum();
		for (int position = 0; position < crabs.length; position++) {
			bestFuel += (long) position * crabsOnRight;
			crabsOnRight -= crabs[position];
		}
		
		long fuel = bestFuel;
		for (int position = 1; position < crabs.length; position++) {
			for (int i = 0; i < crabs.length; i++) {
				fuel += (long) crabs[i] * (position - i + (position <= i ? -1 : 0));
			}
			
			if (fuel < bestFuel) {
				bestFuel = fuel;
				bestPosition = position;
			}
		}
		
		System.out.println(bestPosition + " " + bestFuel);
		return bestFuel;
	}
}