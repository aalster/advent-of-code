package org.advent.year2015.day2;

import org.advent.common.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day2 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day2.class, "input.txt");
		List<int[]> boxes = Utils.readLines(input).stream()
				.map(line -> Arrays.stream(line.split("x")).mapToInt(Integer::parseInt).toArray())
				.toList();
		
		System.out.println("Answer 1: " + part1(boxes));
		System.out.println("Answer 2: " + part2(boxes));
	}
	
	private static long part1(List<int[]> boxes) {
		int area = 0;
		for (int[] edges : boxes) {
			int[] areas = new int[] {edges[0] * edges[1], edges[1] * edges[2], edges[2] * edges[0]};
			area += (areas[0] + areas[1] + areas[2]) * 2 + Math.min(areas[0], Math.min(areas[1], areas[2]));
		}
		return area;
	}
	
	private static long part2(List<int[]> boxes) {
		int length = 0;
		for (int[] edges : boxes) {
			int[] halfPerimeters = new int[] {edges[0] + edges[1], edges[1] + edges[2], edges[2] + edges[0]};
			length += Math.min(halfPerimeters[0], Math.min(halfPerimeters[1], halfPerimeters[2])) * 2 + edges[0] * edges[1] * edges[2];
		}
		return length;
	}
}