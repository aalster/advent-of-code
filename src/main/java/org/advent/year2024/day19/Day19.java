package org.advent.year2024.day19;

import org.advent.common.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day19 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day19.class, "input.txt");
		List<String> towels = Arrays.stream(input.nextLine().split(", ")).toList();
		input.nextLine();
		List<String> designs = Utils.readLines(input);
		
		System.out.println("Answer 1: " + part1(towels, designs));
		System.out.println("Answer 2: " + part2(towels, designs));
	}
	
	private static long part1(List<String> towels, List<String> designs) {
		return designs.stream().filter(d -> possible(d, towels)).count();
	}
	
	private static long part2(List<String> towels, List<String> designs) {
		Map<String, Long> cache = new HashMap<>();
		return designs.stream().mapToLong(design -> variants(design, towels, cache)).sum();
	}
	
	static boolean possible(String design, List<String> towels) {
		if (design.isEmpty())
			return true;
		return towels.stream()
				.filter(towel -> towel.length() <= design.length() && design.startsWith(towel))
				.anyMatch(towel -> possible(design.substring(towel.length()), towels));
	}
	
	static long variants(String design, List<String> towels, Map<String, Long> cache) {
		if (design.isEmpty())
			return 1;
		Long result = cache.get(design);
		if (result == null) {
			result = towels.stream()
					.filter(towel -> towel.length() <= design.length() && design.startsWith(towel))
					.mapToLong(towel -> variants(design.substring(towel.length()), towels, cache))
					.sum();
			cache.put(design, result);
		}
		return result;
	}
}