package org.advent.year2015.day2;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day2 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day2()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 58 + 43, 34 + 14),
				new ExpectedAnswers("input.txt", 1598415, 3812909)
		);
	}
	
	List<int[]> boxes;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		boxes = Utils.readLines(input).stream()
				.map(line -> Arrays.stream(line.split("x")).mapToInt(Integer::parseInt).toArray())
				.toList();
	}
	
	@Override
	public Object part1() {
		int area = 0;
		for (int[] edges : boxes) {
			int[] areas = new int[] {edges[0] * edges[1], edges[1] * edges[2], edges[2] * edges[0]};
			area += (areas[0] + areas[1] + areas[2]) * 2 + Math.min(areas[0], Math.min(areas[1], areas[2]));
		}
		return area;
	}
	
	@Override
	public Object part2() {
		int length = 0;
		for (int[] edges : boxes) {
			int[] halfPerimeters = new int[] {edges[0] + edges[1], edges[1] + edges[2], edges[2] + edges[0]};
			length += Math.min(halfPerimeters[0], Math.min(halfPerimeters[1], halfPerimeters[2])) * 2 + edges[0] * edges[1] * edges[2];
		}
		return length;
	}
}