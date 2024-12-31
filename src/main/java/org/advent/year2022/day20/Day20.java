package org.advent.year2022.day20;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day20 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day20()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 3, 1623178306),
				new ExpectedAnswers("input.txt", 2203, 6641234038999L)
		);
	}
	
	long[] numbers;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		numbers = Utils.readLines(input).stream().mapToLong(Long::valueOf).toArray();
	}
	
	@Override
	public Object part1() {
		return mix(numbers, 1);
	}
	
	@Override
	public Object part2() {
		return mix(Arrays.stream(numbers).map(n -> n * 811589153).toArray(), 10);
	}
	
	long mix(long[] numbers, int times) {
		int length = numbers.length;
		int lengthMinusOne = length - 1;
		int[] newIndexes = new int[length];
		for (int i = 0; i < length; i++)
			newIndexes[i] = i;
		
		while (times > 0) {
			times--;
			
			for (int i = 0; i < length; i++) {
				long shift = numbers[i];
				if (shift == 0)
					continue;
				
				int from = newIndexes[i];
				long toLong = from + shift;
				if (toLong <= 0)
					toLong = toLong % lengthMinusOne + lengthMinusOne;
				else if (toLong >= length)
					toLong = toLong % lengthMinusOne;
				int to = (int) toLong;
				
				if (from < to) {
					for (int newIndex = 0; newIndex < length; newIndex++)
						if (from < newIndexes[newIndex] && newIndexes[newIndex] <= to)
							newIndexes[newIndex]--;
				} else {
					for (int newIndex = 0; newIndex < length; newIndex++)
						if (to <= newIndexes[newIndex] && newIndexes[newIndex] < from)
							newIndexes[newIndex]++;
				}
				newIndexes[i] = to;
			}
		}
		
		long[] newArray = getNewArray(numbers, newIndexes);
		for (int i = 0; i < newArray.length; i++) {
			long n = newArray[i];
			if (n == 0) {
				int a = (i + 1000) % length;
				int b = (i + 2000) % length;
				int c = (i + 3000) % length;
				return newArray[a] + newArray[b] + newArray[c];
			}
		}
		throw new RuntimeException("Zero not found");
	}
	
	long[] getNewArray(long[] numbers, int[] newIndexes) {
		long[] result = new long[numbers.length];
		for (int i = 0; i < newIndexes.length; i++)
			result[newIndexes[i]] = numbers[i];
		return result;
	}
}