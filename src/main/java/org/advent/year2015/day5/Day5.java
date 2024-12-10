package org.advent.year2015.day5;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Day5 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day5.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static long part1(List<String> lines) {
		List<String> bads = List.of("ab", "cd", "pq", "xy");
		Set<Character> vowels = Set.of('a', 'e', 'i', 'o', 'u');
		long result = 0;
		for (String line : lines) {
			if (bads.stream().anyMatch(line::contains))
				continue;
			
			int vowelsCount = 0;
			boolean repeats = false;
			char previous = ' ';
			for (char current : line.toCharArray()) {
				if (vowels.contains(current))
					vowelsCount++;
				if (previous == current)
					repeats = true;
				previous = current;
			}
			if (repeats && vowelsCount > 2)
				result++;
		}
		return result;
	}
	
	private static long part2(List<String> lines) {
		long result = 0;
		for (String line : lines) {
			Map<String, List<Integer>> pairs = new HashMap<>();
			boolean repeats = false;
			char prePrevious = ' ';
			char previous = ' ';
			int index = 0;
			for (char current : line.toCharArray()) {
				if (prePrevious == current)
					repeats = true;
				pairs.computeIfAbsent("" + previous + current, k -> new ArrayList<>()).add(index);
				
				prePrevious = previous;
				previous = current;
				index++;
			}
			boolean hasPairs = pairs.values().stream()
					.filter(l -> l.size() > 1)
					.mapToInt(l -> l.getLast() - l.getFirst())
					.anyMatch(n -> n > 1);
			if (repeats && hasPairs)
				result++;
		}
		return result;
	}
}