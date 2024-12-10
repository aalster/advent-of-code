package org.advent.year2015.day1;

import org.advent.common.Utils;

import java.util.Scanner;

public class Day1 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day1.class, "input.txt");
		String line = input.nextLine();
		
		System.out.println("Answer 1: " + part1(line));
		System.out.println("Answer 2: " + part2(line));
	}
	
	private static long part1(String line) {
		int floor = 0;
		for (char c : line.toCharArray())
			floor += c == '(' ? 1 : -1;
		return floor;
	}
	
	private static long part2(String line) {
		int floor = 0;
		int position = 0;
		for (char c : line.toCharArray()) {
			position++;
			floor += c == '(' ? 1 : -1;
			if (floor == -1)
				return position;
		}
		return -1;
	}
}