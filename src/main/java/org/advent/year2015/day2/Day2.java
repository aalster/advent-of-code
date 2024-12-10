package org.advent.year2015.day2;

import org.advent.common.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day2 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day2.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2());
	}
	
	private static long part1(List<String> lines) {
		int area = 0;
		for (String line : lines) {
			int[] edges = Arrays.stream(line.split("x")).mapToInt(Integer::parseInt).toArray();
			int[] areas = new int[] {edges[0] * edges[1], edges[1] * edges[2], edges[2] * edges[0]};
			area += (areas[0] + areas[1] + areas[2]) * 2 + Math.min(areas[0], Math.min(areas[1], areas[2]));
		}
		return area;
	}
	
	private static long part2() {
		return 0;
	}
}