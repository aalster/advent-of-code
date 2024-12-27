package org.advent.year2015.day13;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SequencedSet;

public class Day13 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day13()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 330, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 733, 725)
		);
	}
	
	Map<String, Map<String, Integer>> values;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		values = new HashMap<>();
		while (input.hasNext()) {
			String line = input.nextLine();
			String[] split = line.replace(" would ", ",")
					.replace("gain ", "")
					.replace("lose ", "-")
					.replace(" happiness units by sitting next to ", ",")
					.replace(".", "")
					.split(",");
			values.computeIfAbsent(split[0], k -> new HashMap<>())
					.compute(split[2], (k, v) -> (v == null ? 0 : v) + Integer.parseInt(split[1]));
			values.computeIfAbsent(split[2], k -> new HashMap<>())
					.compute(split[0], (k, v) -> (v == null ? 0 : v) + Integer.parseInt(split[1]));
		}
	}
	
	@Override
	public Object part1() {
		return solve(values);
	}
	
	@Override
	public Object part2() {
		Map<String, Map<String, Integer>> withMe = new HashMap<>(values);
		for (String other : values.keySet()) {
			withMe.computeIfAbsent("Me", k -> new HashMap<>()).put(other, 0);
			withMe.get(other).put("Me", 0);
		}
		return solve(withMe);
	}
	
	int solve(Map<String, Map<String, Integer>> values) {
		return pickNext(new LinkedHashSet<>(), values.keySet().iterator().next(), values);
	}
	
	static int pickNext(SequencedSet<String> sitting, String previous, Map<String, Map<String, Integer>> values) {
		SequencedSet<String> nextSitting = new LinkedHashSet<>(sitting);
		nextSitting.add(previous);
		if (nextSitting.size() >= values.size() - 1) {
			String last = values.keySet().stream().filter(v -> !nextSitting.contains(v)).findFirst().orElseThrow();
			return values.get(last).get(previous) + values.get(last).get(nextSitting.getFirst());
		}
		return values.keySet().stream()
				.filter(next -> !nextSitting.contains(next))
				.mapToInt(next -> values.get(next).get(previous) + pickNext(nextSitting, next, values))
				.max()
				.orElseThrow();
	}
}