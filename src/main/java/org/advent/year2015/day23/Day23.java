package org.advent.year2015.day23;

import org.advent.common.Utils;

import java.util.List;
import java.util.Scanner;

public class Day23 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day23.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		
		System.out.println("Answer 1: " + solve(lines, 0));
		System.out.println("Answer 2: " + solve(lines, 1));
	}
	
	private static long solve(List<String> lines, long a) {
		long b = 0;
		
		for (int i = 0; i < lines.size(); i++) {
			String[] split = lines.get(i).replace(",", "").split(" ");
			switch (split[0]) {
				case "hlf" -> {
					if (split[1].equals("a"))
						a /= 2;
					else
						b /= 2;
				}
				case "tpl" -> {
					if (split[1].equals("a"))
						a *= 3;
					else
						b *= 3;
				}
				case "inc" -> {
					if (split[1].equals("a"))
						a++;
					else
						b++;
				}
				case "jmp" -> i += Integer.parseInt(split[1]) - 1;
				case "jie" -> {
					if ((split[1].equals("a") ? a : b) % 2 == 0)
						i += Integer.parseInt(split[2]) - 1;
				}
				case "jio" -> {
					if ((split[1].equals("a") ? a : b) == 1)
						i += Integer.parseInt(split[2]) - 1;
				}
			}
		}
		return b;
	}
}