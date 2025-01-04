package org.advent.year2021.day1;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day1 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day1()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 7, 5),
				new ExpectedAnswers("input.txt", 1502, 1538)
		);
	}
	
	List<Integer> depths;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		depths = Utils.readLines(input).stream().map(Integer::parseInt).toList();
	}
	
	@Override
	public Object part1() {
		int increases = 0;
		int previousDepth = 0;
		for (Integer depth : depths) {
			if (previousDepth < depth)
				increases++;
			previousDepth = depth;
		}
		return increases - 1;
	}
	
	@Override
	public Object part2() {
		int increases = 0;
		int previousDepth = 0;
		for (int i = 0; i < depths.size() - 2; i++) {
			int depth = depths.get(i) + depths.get(i + 1) + depths.get(i + 2);
			if (previousDepth < depth)
				increases++;
			previousDepth = depth;
		}
		return increases - 1;
	}
}