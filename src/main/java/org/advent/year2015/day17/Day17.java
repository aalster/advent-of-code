package org.advent.year2015.day17;

import org.advent.common.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Day17 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day17.class, "input.txt");
		int[] containers = Utils.readLines(input).stream().mapToInt(Integer::parseInt).toArray();
		
		System.out.println("Answer 1: " + part1(containers));
		System.out.println("Answer 2: " + part2(containers));
	}
	
	private static long part1(int[] containers) {
		return countNext(containers, 0, 0, 150).values().stream().mapToInt(i -> i).sum();
	}
	
	private static long part2(int[] containers) {
		Map<Integer, Integer> combinations = countNext(containers, 0, 0, 150);
		return combinations.get(combinations.keySet().stream().mapToInt(i -> i).min().orElseThrow());
	}
	
	static Map<Integer, Integer> countNext(int[] containers, int currentIndex, int containersUsed, int litersLeft) {
		if (litersLeft == 0)
			return Map.of(containersUsed, 1);
		if (litersLeft < 0 || currentIndex >= containers.length)
			return Map.of();
		
		int container = containers[currentIndex];
		Map<Integer, Integer> currentUsed = countNext(containers, currentIndex + 1, containersUsed + 1, litersLeft - container);
		Map<Integer, Integer> currentNotUsed = countNext(containers, currentIndex + 1, containersUsed, litersLeft);
		
		HashMap<Integer, Integer> result = new HashMap<>(currentUsed.size() + currentNotUsed.size());
		result.putAll(currentUsed);
		for (Map.Entry<Integer, Integer> entry : currentNotUsed.entrySet())
			result.compute(entry.getKey(), (k, v) -> (v == null ? 0 : v) + entry.getValue());
		
		return result;
	}
}