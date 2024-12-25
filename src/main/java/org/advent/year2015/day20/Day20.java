package org.advent.year2015.day20;

import org.advent.common.Utils;

import java.util.Scanner;

public class Day20 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day20.class, "input.txt");
		int targetPresents = Integer.parseInt(input.nextLine());
		
		System.out.println("Answer 1: " + part1(targetPresents));
		System.out.println("Answer 2: " + part2(targetPresents));
	}
	
	private static long part1(int targetPresents) {
		for (int i = 1; i < 1000000; i++)
			if (countPresents1(i) >= targetPresents)
				return i;
		return 0;
	}
	
	private static long part2(int targetPresents) {
		for (int i = 1; i < 1000000; i++)
			if (countPresents2(i) >= targetPresents)
				return i;
		return 0;
	}
	
	private static int countPresents1(int house) {
		int presents = 0;
		double sqrt = Math.sqrt(house);
		for (int i = 1; i <= sqrt; i++)
			if (house % i == 0)
				presents += i + house / i;
		return presents * 10;
	}
	
	private static int countPresents2(int house) {
		int presents = 0;
		double sqrt = Math.sqrt(house);
		for (int i = 1; i <= sqrt; i++) {
			if (house % i == 0) {
				int divided = house / i;
				if (divided < 50)
					presents += i;
				if (i < 50)
					presents += divided;
			}
		}
		return presents * 11;
	}
}