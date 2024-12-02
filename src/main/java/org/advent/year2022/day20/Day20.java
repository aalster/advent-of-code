package org.advent.year2022.day20;

import org.advent.common.Utils;

import java.util.Arrays;
import java.util.Scanner;

public class Day20 {
	static final boolean debug = false;
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day20.class, "input.txt");
		long[] numbers = Utils.readLines(input).stream().mapToLong(Long::valueOf).toArray();
		
		System.out.println("Answer 1: " + part1(numbers));
		System.out.println("Answer 2: " + part2(numbers));
	}
	
	private static long part1(long[] numbers) {
		return mix(numbers, 1);
	}
	
	private static long part2(long[] numbers) {
		for (int i = 0; i < numbers.length; i++)
			numbers[i] *= 811589153;
		return mix(numbers, 10);
	}
	
	private static long mix(long[] numbers, int times) {
		int length = numbers.length;
		int lengthMinusOne = length - 1;
		int[] newIndexes = new int[length];
		for (int i = 0; i < length; i++)
			newIndexes[i] = i;
		
		if (debug) {
			System.out.println("Initial arrangement:");
			System.out.println(Arrays.toString(newIndexes));
			System.out.println(Arrays.toString(numbers));
		}
		
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
				
				if (debug) {
					System.out.println();
					System.out.println(shift + " moves from " + from + " to " + to + ":");
				}
				
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
				if (debug) {
					System.out.println(Arrays.toString(newIndexes));
					System.out.println(Arrays.toString(getNewArray(numbers, newIndexes)));
				}
			}
		}
		
		long[] newArray = getNewArray(numbers, newIndexes);
		for (int i = 0; i < newArray.length; i++) {
			long n = newArray[i];
			if (n == 0) {
				int a = (i + 1000) % length;
				int b = (i + 2000) % length;
				int c = (i + 3000) % length;
				System.out.println(newArray[a] + ", " + newArray[b] + ", " + newArray[c]);
				return newArray[a] + newArray[b] + newArray[c];
			}
		}
		throw new RuntimeException("Zero not found");
	}
	
	private static long[] getNewArray(long[] numbers, int[] newIndexes) {
		long[] result = new long[numbers.length];
		for (int i = 0; i < newIndexes.length; i++)
			result[newIndexes[i]] = numbers[i];
		return result;
	}
}