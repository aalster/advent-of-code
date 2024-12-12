package org.advent.year2015.day11;

import org.advent.common.Utils;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day11 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day11.class, "input.txt");
		String line = input.nextLine();
		
		String part1 = solve(line, false);
		System.out.println("Answer 1: " + part1);
		System.out.println("Answer 2: " + solve(part1, true));
	}
	
	private static String solve(String line, boolean skipCurrent) {
		Set<Integer> restricted = "iol".chars().map(c -> c - 'a').boxed().collect(Collectors.toSet());
		int[] letters = line.chars().map(c -> c - 'a').toArray();
		if (skipCurrent)
			increaseDigit(letters, letters.length - 1, restricted);
		
		for (int i = 0; i < letters.length; i++) {
			if (restricted.contains(letters[i])) {
				increaseDigit(letters, i, restricted);
				break;
			}
		}
		while (!valid(letters)) {
			int index = letters.length - 1;
			increaseDigit(letters, index, restricted);
		}
		return lettersToString(letters);
	}
	
	private static void increaseDigit(int[] letters, int index, Set<Integer> restricted) {
		boolean overflow = true;
		while (restricted.contains(letters[index]) || overflow) {
			overflow = false;
			letters[index]++;
			
			if (letters[index] >= 26) {
				index--;
				overflow = true;
			}
		}
		index++;
		while (index < letters.length) {
			letters[index] = 0;
			index++;
		}
	}
	
	static boolean valid(int[] letters) {
		boolean inc = false;
		for (int i = 0; i < letters.length - 2; i++) {
			if (letters[i] + 2 < 26 && letters[i] + 1 == letters[i + 1] && letters[i] + 2 == letters[i + 2]) {
				inc = true;
				break;
			}
		}
		if (!inc)
			return false;
		
		boolean firstPairFound = false;
		for (int i = 0; i < letters.length - 1; i++) {
			if (letters[i] == letters[i + 1]) {
				if (firstPairFound)
					return true;
				firstPairFound = true;
				i++;
			}
		}
		return false;
	}
	
	private static String lettersToString(int[] letters) {
		return Arrays.stream(letters).mapToObj(l -> "" + (char) ('a' + l)).collect(Collectors.joining());
	}
}