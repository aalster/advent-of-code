package org.advent.year2022.day20;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day20 {
	static final boolean debug = false;
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day20.class, "input.txt");
		List<Integer> lines = new ArrayList<>(5000);
		while (input.hasNext()) {
			lines.add(Integer.valueOf(input.nextLine()));
		}
		int[] numbers = lines.stream().mapToInt(i -> i).toArray();
		
//		numbers = new int[] {0, 12, 0, 0, 0};
		
		System.out.println("Answer 1: " + part1(numbers));
		System.out.println("Answer 2: " + part2());
	}
	
	private static int part1(int[] numbers) {
		int length = numbers.length;
		int[] newIndexes = new int[length];
		for (int i = 0; i < length; i++)
			newIndexes[i] = i;
		
		if (debug) {
			System.out.println("Initial arrangement:");
			System.out.println(Arrays.toString(newIndexes));
			System.out.println(Arrays.toString(numbers));
		}
		
		for (int i = 0; i < length; i++) {
			int shift = numbers[i];
			if (shift == 0)
				continue;
			
			int from = newIndexes[i];
			int to = from + shift;
			while (to <= 0)
				to = to + length - 1;
			while (to >= length)
				to = to - length + 1;
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
		
		int[] newArray = getNewArray(numbers, newIndexes);
		for (int i = 0; i < newArray.length; i++) {
			int n = newArray[i];
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
	
	private static int[] getNewArray(int[] numbers, int[] newIndexes) {
		int[] result = new int[numbers.length];
		for (int i = 0; i < newIndexes.length; i++)
			result[newIndexes[i]] = numbers[i];
		return result;
	}
	
	private static int part2() {
		return 0;
	}
}