package org.advent.year2015.day10;

import org.advent.common.Utils;

import java.util.Scanner;

public class Day10 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day10.class, "input.txt");
		String line = input.nextLine();
		
		System.out.println("Answer 1: " + solve(line, 40));
		System.out.println("Answer 2: " + solve(line, 50));
	}
	
	private static long solve(String line, int count) {
		while (count > 0) {
			StringBuilder next = new StringBuilder(line.length() * 2);
			char[] chars = line.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				int repeats = 1;
				for (int k = i + 1; k < chars.length; k++) {
					if (c != chars[k])
						break;
					repeats++;
				}
				next.append(repeats).append(c);
				i += repeats - 1;
			}
			line = next.toString();
			count--;
		}
		return line.length();
	}
}