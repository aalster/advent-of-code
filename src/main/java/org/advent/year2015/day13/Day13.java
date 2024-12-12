package org.advent.year2015.day13;

import org.advent.common.Utils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.SequencedSet;

public class Day13 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day13.class, "input.txt");
		Map<String, Map<String, Integer>> values = new HashMap<>();
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
		
		System.out.println("Answer 1: " + part1(values));
		System.out.println("Answer 2: " + part2(values));
	}
	
	private static long part1(Map<String, Map<String, Integer>> values) {
		return pickNext(new LinkedHashSet<>(), values.keySet().iterator().next(), values);
	}
	
	private static long part2(Map<String, Map<String, Integer>> values) {
		Map<String, Map<String, Integer>> withMe = new HashMap<>(values);
		for (String other : values.keySet()) {
			withMe.computeIfAbsent("Me", k -> new HashMap<>()).put(other, 0);
			withMe.get(other).put("Me", 0);
		}
		return part1(withMe);
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