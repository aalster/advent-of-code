package org.advent.year2023.day1;

import org.advent.common.Pair;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day1 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day1.class, "input.txt");
		List<String> lines = new ArrayList<>();
		while (input.hasNext()) {
			lines.add(input.nextLine());
		}
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static long part1(List<String> data) {
		Map<String, Integer> digits = IntStream.range(0, 10).boxed().collect(Collectors.toMap(i -> "" + i, i -> i));
		return calculate(data, digits);
	}
	
	private static long part2(List<String> data) {
		Map<String, Integer> digits = IntStream.range(0, 10).boxed().collect(Collectors.toMap(i -> "" + i, i -> i));
		digits = new HashMap<>(digits);
		digits.putAll(Map.ofEntries(
				Map.entry("zero", 0),
				Map.entry("one", 1),
				Map.entry("two", 2),
				Map.entry("three", 3),
				Map.entry("four", 4),
				Map.entry("five", 5),
				Map.entry("six", 6),
				Map.entry("seven", 7),
				Map.entry("eight", 8),
				Map.entry("nine", 9)
		));
		return calculate(data, digits);
	}
	
	private static long calculate(List<String> data, Map<String, Integer> digits) {
		long result = 0;
		for (String line : data) {
			result += first(line, digits) * 10L + last(line, digits);
		}
		return result;
	}
	
	private static int first(String src, Map<String, Integer> digits) {
		return digits.entrySet().stream()
				.map(entry -> Pair.of(entry.getValue(), indexOf(src, entry.getKey())))
				.filter(pair -> pair.right() >= 0)
				.min(Comparator.comparing(Pair::right))
				.map(Pair::left)
				.orElse(0);
	}
	
	private static int last(String src, Map<String, Integer> digits) {
		return digits.entrySet().stream()
				.map(entry -> Pair.of(entry.getValue(), lastIndexOf(src, entry.getKey())))
				.filter(pair -> pair.right() >= 0)
				.max(Comparator.comparing(Pair::right))
				.map(Pair::left)
				.orElse(0);
	}
	
	private static int indexOf(String src, String search) {
		if (src.length() < search.length())
			return -1;
		char[] srcChars = src.toCharArray();
		char[] searchChars = search.toCharArray();
		
		for (int index = 0; index <= src.length() - search.length(); index++) {
			for (int i = 0; i < searchChars.length; i++) {
				if (searchChars[i] != srcChars[i + index])
					break;
				if (i == searchChars.length - 1)
					return index;
			}
		}
		return -1;
	}
	
	private static int lastIndexOf(String src, String search) {
		if (src.length() < search.length())
			return -1;
		char[] srcChars = src.toCharArray();
		char[] searchChars = search.toCharArray();
		
		for (int index = src.length() - search.length(); index >= 0; index--) {
			for (int i = 0; i < searchChars.length; i++) {
				if (searchChars[i] != srcChars[i + index])
					break;
				if (i == searchChars.length - 1)
					return index;
			}
		}
		return -1;
	}
}