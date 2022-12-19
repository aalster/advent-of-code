package org.advent.year2021.day3;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day3 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day3.class, "input.txt");
		List<String> numbers = new ArrayList<>();
		while (input.hasNext()) {
			numbers.add(input.nextLine());
		}
		
		System.out.println("Answer 1: " + part1(numbers));
		System.out.println("Answer 2: " + part2(numbers));
	}
	
	private static int part1(List<String> numbers) {
		int gamma = 0;
		int epsilon = 0;
		for (int i = 0; i < numbers.get(0).length(); i++) {
			int mostCommon = mostCommon(numbers, i);
			gamma = gamma * 2 + mostCommon;
			epsilon = epsilon * 2 + 1 - mostCommon;
		}
		return gamma * epsilon;
	}
	
	private static int part2(List<String> numbers) {
		return oxygen(numbers) * co2(numbers);
	}
	
	private static int oxygen(List<String> numbers) {
		int oxygen = 0;
		for (int i = 0; i < numbers.get(0).length(); i++) {
			int index = i;
			int mostCommon = mostCommon(numbers, i);
			oxygen = oxygen * 2 + mostCommon;
			numbers = numbers.stream().filter(n -> n.charAt(index) == mostCommon + '0').toList();
		}
		return oxygen;
	}
	
	private static int co2(List<String> numbers) {
		int oxygen = 0;
		for (int i = 0; i < numbers.get(0).length(); i++) {
			if (numbers.size() == 1)
				return Integer.parseInt(numbers.get(0), 2);
			
			int index = i;
			int leastCommon = 1 - mostCommon(numbers, i);
			oxygen = oxygen * 2 + leastCommon;
			numbers = numbers.stream().filter(n -> n.charAt(index) == leastCommon + '0').toList();
		}
		return oxygen;
	}
	
	private static int mostCommon(List<String> numbers, int index) {
		int count = 0;
		for (String number : numbers)
			count += number.charAt(index) == '1' ? 1 : -1;
		return count >= 0 ? 1 : 0;
	}
}