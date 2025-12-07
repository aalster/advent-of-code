package org.advent.year2020.day10;

import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day10 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day10()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 35, 8),
				new ExpectedAnswers("example2.txt", 220, 19208),
				new ExpectedAnswers("input.txt", 2414, 21156911906816L)
		);
	}
	
	int[] adapters;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		adapters = Utils.readLines(input).stream().mapToInt(Integer::parseInt).sorted().toArray();
	}
	
	@Override
	public Object part1() {
		int oneJoltDiffs = 0;
		int threeJoltDiffs = 1;
		int prevJolts = 0;
		for (int adapter : adapters) {
			int diff = adapter - prevJolts;
			if (diff > 3)
				break;
			if (diff == 1)
				oneJoltDiffs++;
			else if (diff == 3)
				threeJoltDiffs++;
			prevJolts = adapter;
		}
		return oneJoltDiffs * threeJoltDiffs;
	}
	
	@Override
	public Object part2() {
		return countArrangements(0, 0, new HashMap<>());
	}
	
	long countArrangements(int jolts, int index, Map<Pair<Integer, Integer>, Long> cache) {
		if (index >= adapters.length)
			return 1;
		
		Pair<Integer, Integer> key = Pair.of(jolts, index);
		Long result = cache.get(key);
		if (result == null) {
			int current = adapters[index];
			int diff = current - jolts;
			if (diff > 3)
				return 0;
			
			index++;
			result = countArrangements(current, index, cache);
			if (index < adapters.length)
				result += countArrangements(jolts, index, cache);
			
			cache.put(key, result);
		}
		return result;
	}
}