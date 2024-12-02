package org.advent.year2024.day2;

import org.advent.common.Utils;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Day2 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day2.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static long part1(List<String> lines) {
		long count = 0;
		for (String line : lines) {
			int[] levels = Stream.of(line.split(" ")).mapToInt(Integer::parseInt).toArray();
			RangeChecker range = levels[0] < levels[1] ? new RangeChecker(1, 3) : new RangeChecker(-3, -1);
			boolean safe = true;
			for (int i = 0; i < levels.length - 1; i++) {
				int diff = levels[i + 1] - levels[i];
				if (!range.contains(diff)) {
					safe = false;
					break;
				}
			}
			if (safe)
				count++;
		}
		return count;
	}
	
	private static long part2(List<String> lines) {
		long count = 0;
		for (String line : lines) {
			int[] levels = Stream.of(line.split(" ")).mapToInt(Integer::parseInt).toArray();
			int[] diffs = new int[levels.length - 1];
			int increasingCount = 0;
			for (int i = 0; i < levels.length - 1; i++) {
				int diff = levels[i + 1] - levels[i];
				diffs[i] = diff;
				increasingCount += diff < 0 ? -1 : 1;
			}
			RangeChecker range = 0 < increasingCount ? new RangeChecker(1, 3) : new RangeChecker(-3, -1);
			
			boolean safe = true;
			int removedIndex = -1;
			for (int i = 0; i < diffs.length; i++) {
				int diff = diffs[i];
				if (!range.contains(diff)) {
					if (removedIndex < 0) {
						if (i == 0 || i == diffs.length - 1
								|| range.contains(diffs[i - 1] + diffs[i])
								|| range.contains(diffs[i] + diffs[i + 1])) {
							removedIndex = i;
							continue;
						}
					}
					if (removedIndex >= 0 && removedIndex == i - 1 && range.contains(diffs[removedIndex] + diffs[i]))
						continue;
					safe = false;
					break;
				}
			}
			
			if (safe)
				count++;
		}
		return count;
	}
	
	record RangeChecker(int min, int max) {
		boolean contains(int value) {
			return min <= value && value <= max;
		}
	}
}