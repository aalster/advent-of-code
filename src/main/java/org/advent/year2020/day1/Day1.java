package org.advent.year2020.day1;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day1 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day1.class, "input.txt");
		List<Integer> numbers = new ArrayList<>();
		while (input.hasNext()) {
			numbers.add(Integer.parseInt(input.nextLine()));
		}
		
		System.out.println("Answer 1: " + part1(numbers));
		System.out.println("Answer 2: " + part2(numbers));
	}
	
	private static int part1(List<Integer> numbers) {
		int index = 0;
		for (Integer left : numbers) {
			for (Integer right : numbers.subList(index + 1, numbers.size())) {
				if (left + right == 2020)
					return left * right;
			}
			index++;
		}
		return 0;
	}
	
	private static int part2(List<Integer> numbers) {
		int index = 0;
		for (Integer left : numbers) {
			for (Integer right : numbers.subList(index + 1, numbers.size())) {
				int index2 = index + 1;
				for (Integer mid : numbers.subList(index2 + 1, numbers.size())) {
					if (left + right + mid == 2020)
						return left * right * mid;
				}
			}
			index++;
		}
		return 0;
	}
}