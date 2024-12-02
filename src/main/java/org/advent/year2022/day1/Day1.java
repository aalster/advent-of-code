package org.advent.year2022.day1;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Day1 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day1.class, "input.txt");
		List<List<Long>> allCalories = new ArrayList<>();
		while (input.hasNext()) {
			List<Long> calories = new ArrayList<>();
			while (input.hasNext()) {
				String next = input.nextLine();
				if (next.isEmpty())
					break;
				calories.add(Long.parseLong(next));
			}
			allCalories.add(calories);
		}
		
		System.out.println("Answer 1: " + solve(allCalories, 1));
		System.out.println("Answer 2: " + solve(allCalories, 3));
	}
	
	private static long solve(List<List<Long>> allCalories, int topCount) {
		return allCalories.stream()
				.map(c -> c.stream().mapToLong(l -> l).sum())
				.sorted(Collections.reverseOrder())
				.limit(topCount)
				.mapToLong(l -> l)
				.sum();
	}
}