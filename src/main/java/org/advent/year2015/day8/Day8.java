package org.advent.year2015.day8;

import org.advent.common.Utils;

import java.util.List;
import java.util.Scanner;

public class Day8 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day8.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static long part1(List<String> lines) {
		int diff = lines.size() * 2;
		for (String line : lines) {
			char[] chars = line.toCharArray();
			for (int i = 1; i < chars.length - 1; i++) {
				if (chars[i] != '\\')
					continue;
				diff += chars[i + 1] == 'x' ? 3 : 1;
				if (chars[i + 1] == '\\')
					i++;
			}
		}
		return diff;
	}
	
	private static long part2(List<String> lines) {
		int diff = lines.size() * 2;
		for (String line : lines)
			for (char c : line.toCharArray())
				diff += c == '\"' || c == '\\' ? 1 : 0;
		return diff;
	}
}