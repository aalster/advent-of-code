package org.advent.year2017.day6;

import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day6 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day6()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 5, 4),
				new ExpectedAnswers("input.txt", 12841, 8038)
		);
	}
	
	String line;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		line = input.nextLine();
	}
	
	@Override
	public Object part1() {
		return findLoop().right();
	}
	
	@Override
	public Object part2() {
		Pair<Integer, Integer> indexes = findLoop();
		return indexes.right() - indexes.left();
	}
	
	private Pair<Integer, Integer> findLoop() {
		int[] banks = Arrays.stream(line.split("\\s+")).mapToInt(Integer::parseInt).toArray();
		Map<List<Integer>, Integer> seen = new HashMap<>();
		int step = 0;
		while (true) {
			List<Integer> list = Arrays.stream(banks).boxed().toList();
			Integer previous = seen.put(list, step);
			if (previous != null)
				return Pair.of(previous, step);
			step++;
			
			int max = Arrays.stream(banks).max().orElseThrow();
			int maxIndex = ArrayUtils.indexOf(banks, max);
			banks[maxIndex] = 0;
			maxIndex++;
			while (max > 0) {
				banks[maxIndex % banks.length]++;
				max--;
				maxIndex++;
			}
		}
	}
}