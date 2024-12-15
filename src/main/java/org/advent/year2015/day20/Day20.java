package org.advent.year2015.day20;

import org.advent.common.Utils;

import java.util.Scanner;

public class Day20 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day20.class, "input.txt");
		int targetPresents = Integer.parseInt(input.nextLine());
		
		long start = System.currentTimeMillis();
		System.out.println("Answer 1: " + part1(targetPresents));
		System.out.println("Answer 2: " + part2());
		System.out.println((System.currentTimeMillis() - start) + "ms");
	}
	
	private static long part1(int targetPresents) {
		targetPresents /= 10;
		for (int i = 1; i < 100000; i++) {
//			System.out.println(i + " - " + countPresents(i));
			int presents = 0;
			for (int i1 = 1; i1 <= i / 2; i1++) {
				if (i % i1 == 0)
					presents += i1;
			}
			if (presents + i >= targetPresents)
				return i;
		}
		return 0;
	}
	
	private static long part2() {
		return 0;
	}
	
}