package org.advent.year2023.day8;

import lombok.Data;
import org.advent.common.Pair;
import org.advent.common.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day8 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day8.class, "input.txt");
		int[] instructions = input.nextLine().chars().map(c -> c == 'L' ? 0 : 1).toArray();
		input.nextLine();
		
		Pattern pattern = Pattern.compile("(.{3}) = \\((.{3}), (.{3})\\)");
		Map<String, Pair<String, String>> directions = new HashMap<>();
		while (input.hasNext()) {
			Matcher matcher = pattern.matcher(input.nextLine());
			if (!matcher.find())
				continue;
			directions.put(matcher.group(1), Pair.of(matcher.group(2), matcher.group(3)));
		}
		
		System.out.println("Answer 1: " + part1(instructions, directions));
		System.out.println("Answer 2: " + part2(instructions, directions));
	}
	
	private static long part1(int[] instructions, Map<String, Pair<String, String>> directions) {
		String start = "AAA";
		if (!directions.containsKey(start)) {
			System.out.println("Test input invalid");
			return 0;
		}
		return countSteps(instructions, directions, start, "ZZZ"::equals);
	}
	
	private static long part2(int[] instructions, Map<String, Pair<String, String>> directions) {
		int[] counts = directions.keySet().stream()
				.filter(s -> s.endsWith("A"))
				.mapToInt(s -> countSteps(instructions, directions, s, str -> str.endsWith("Z")))
				.toArray();
		return lcm(counts);
	}
	
	private static int countSteps(int[] instructions, Map<String, Pair<String, String>> directions, String start, Predicate<String> endChecker) {
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
	
	private static long lcm(int[] element_array) {
		long lcm_of_array_elements = 1;
		int divisor = 2;
		
		while (true) {
			int counter = 0;
			boolean divisible = false;
			
			for (int i = 0; i < element_array.length; i++) {
				if (element_array[i] == 0)
					return 0;
				else if (element_array[i] < 0)
					element_array[i] = element_array[i] * (-1);
				
				if (element_array[i] == 1)
					counter++;
				
				if (element_array[i] % divisor == 0) {
					divisible = true;
					element_array[i] = element_array[i] / divisor;
				}
			}
			
			if (divisible)
				lcm_of_array_elements = lcm_of_array_elements * divisor;
			else
				divisor++;
			
			if (counter == element_array.length)
				return lcm_of_array_elements;
		}
	}
	
	@Data
	private static class CircularIterator {
		private final int[] data;
		private int prevIndex = -1;
		
		public int next() {
			prevIndex = (prevIndex + 1) % data.length;
			return data[prevIndex];
		}
	}
}