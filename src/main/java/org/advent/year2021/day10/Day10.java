package org.advent.year2021.day10;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

public class Day10 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day10()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 26397, 288957),
				new ExpectedAnswers("input.txt", 392043, 1605968119)
		);
	}
	
	Map<Character, Character> openingSymbols;
	Map<Character, Character> closingSymbols;
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
		
		List<String> chunks = List.of("()", "[]", "{}", "<>");
		openingSymbols = chunks.stream().map(String::toCharArray).collect(Collectors.toMap(b -> b[1], b -> b[0]));
		closingSymbols = chunks.stream().map(String::toCharArray).collect(Collectors.toMap(b -> b[0], b -> b[1]));
	}
	
	@Override
	public Object part1() {
		Map<Character, Integer> points = Map.ofEntries(
				Map.entry(')', 3),
				Map.entry(']', 57),
				Map.entry('}', 1197),
				Map.entry('>', 25137)
		);
		return lines.stream().map(this::findIncorrectClosing).filter(Objects::nonNull).mapToLong(points::get).sum();
	}
	
	@Override
	public Object part2() {
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
		long[] scores = lines.stream().map(this::completeLine).filter(Objects::nonNull).mapToLong(scoreMapper).sorted().toArray();
		return scores[scores.length / 2];
	}
	
	Character findIncorrectClosing(String line) {
		List<Character> stack = new ArrayList<>();
		for (char c : line.toCharArray()) {
			Character opening = openingSymbols.get(c);
			if (opening == null) {
				stack.add(c);
				continue;
			}
			if (stack.isEmpty() || stack.removeLast() != opening)
				return c;
		}
		return null;
	}
	
	String completeLine(String line) {
		List<Character> stack = new ArrayList<>();
		for (char c : line.toCharArray()) {
			Character opening = openingSymbols.get(c);
			if (opening == null) {
				stack.add(c);
				continue;
			}
			if (stack.isEmpty() || stack.removeLast() != opening)
				return null;
		}
		if (stack.isEmpty())
			return null;
		
		StringBuilder completion = new StringBuilder();
		while (!stack.isEmpty())
			completion.append(closingSymbols.get(stack.removeLast()));
		return completion.toString();
	}
}