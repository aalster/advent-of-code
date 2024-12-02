package org.advent.year2021.day10;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

public class Day10 {
	
	static final List<String> chunks = List.of("()", "[]", "{}", "<>");
	static final Map<Character, Character> openingSymbols = chunks.stream()
			.map(String::toCharArray)
			.collect(Collectors.toMap(b -> b[1], b -> b[0]));
	static final Map<Character, Character> closingSymbols = chunks.stream()
			.map(String::toCharArray)
			.collect(Collectors.toMap(b -> b[0], b -> b[1]));
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day10.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static long part1(List<String> data) {
		Map<Character, Integer> points = Map.ofEntries(
				Map.entry(')', 3),
				Map.entry(']', 57),
				Map.entry('}', 1197),
				Map.entry('>', 25137)
		);
		return data.stream().map(Day10::findIncorrectClosing).filter(Objects::nonNull).mapToLong(points::get).sum();
	}
	
	private static long part2(List<String> data) {
		Map<Character, Integer> points = Map.ofEntries(
				Map.entry(')', 1),
				Map.entry(']', 2),
				Map.entry('}', 3),
				Map.entry('>', 4)
		);
		ToLongFunction<String> scoreMapper = completion -> {
			long result = 0;
			for (char c : completion.toCharArray())
				result = result * 5 + points.get(c);
			return result;
		};
		long[] scores = data.stream().map(Day10::completeLine).filter(Objects::nonNull).mapToLong(scoreMapper).sorted().toArray();
		return scores[scores.length / 2];
	}
	
	static Character findIncorrectClosing(String line) {
		Stack<Character> stack = new Stack<>();
		for (char c : line.toCharArray()) {
			Character opening = openingSymbols.get(c);
			if (opening == null) {
				stack.add(c);
				continue;
			}
			if (stack.isEmpty() || stack.remove() != opening)
				return c;
		}
		return null;
	}
	
	static String completeLine(String line) {
		Stack<Character> stack = new Stack<>();
		for (char c : line.toCharArray()) {
			Character opening = openingSymbols.get(c);
			if (opening == null) {
				stack.add(c);
				continue;
			}
			if (stack.isEmpty() || stack.remove() != opening)
				return null;
		}
		if (stack.isEmpty())
			return null;
		
		StringBuilder completion = new StringBuilder();
		while (!stack.isEmpty())
			completion.append(closingSymbols.get(stack.remove()));
		return completion.toString();
	}
	
	static class Stack<T> {
		private final List<T> items = new ArrayList<>();
		
		int size() {
			return items.size();
		}
		
		boolean isEmpty() {
			return items.isEmpty();
		}
		
		void add(T item) {
			items.add(item);
		}
		
		T remove() {
			return items.remove(size() - 1);
		}
	}
}