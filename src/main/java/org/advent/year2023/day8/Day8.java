package org.advent.year2023.day8;

import lombok.Data;
import org.advent.common.NumbersAdventUtils;
import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day8 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day8()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 2, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 6, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", ExpectedAnswers.IGNORE, 6),
				new ExpectedAnswers("input.txt", 12737, 9064949303801L)
		);
	}
	
	int[] instructions;
	Map<String, Pair<String, String>> directions;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		instructions = input.nextLine().chars().map(c -> c == 'L' ? 0 : 1).toArray();
		input.nextLine();
		
		Pattern pattern = Pattern.compile("(.{3}) = \\((.{3}), (.{3})\\)");
		directions = new HashMap<>();
		while (input.hasNext()) {
			Matcher matcher = pattern.matcher(input.nextLine());
			if (!matcher.find())
				continue;
			directions.put(matcher.group(1), Pair.of(matcher.group(2), matcher.group(3)));
		}
	}
	
	@Override
	public Object part1() {
		String start = "AAA";
		if (!directions.containsKey(start)) {
			System.out.println("Test input invalid");
			return 0;
		}
		return countSteps(instructions, directions, start, "ZZZ"::equals);
	}
	
	@Override
	public Object part2() {
		int[] counts = directions.keySet().stream()
				.filter(s -> s.endsWith("A"))
				.mapToInt(s -> countSteps(instructions, directions, s, str -> str.endsWith("Z")))
				.toArray();
		return NumbersAdventUtils.lcm(counts);
	}
	
	int countSteps(int[] instructions, Map<String, Pair<String, String>> directions, String start, Predicate<String> endChecker) {
		CircularIterator iterator = new CircularIterator(instructions);
		String current = start;
		int steps = 0;
		while (!endChecker.test(current)) {
			steps++;
			Pair<String, String> pair = directions.get(current);
			current = iterator.next() == 0 ? pair.left() : pair.right();
		}
		return steps;
	}
	
	@Data
	static class CircularIterator {
		private final int[] data;
		private int prevIndex = -1;
		
		public int next() {
			prevIndex = (prevIndex + 1) % data.length;
			return data[prevIndex];
		}
	}
}