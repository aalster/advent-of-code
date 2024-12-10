package org.advent.year2015.day7;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class Day7 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day7.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static long part1(List<String> lines) {
		return solve(lines);
	}
	
	private static long part2(List<String> lines) {
		long answer1 = part1(lines);
		lines = lines.stream().map(l -> l.endsWith(" -> b") ? answer1 + " -> b" : l).toList();
		return solve(lines);
	}
	
	private static Integer solve(List<String> lines) {
		lines = new ArrayList<>(lines);
		
		Map<String, Integer> values = new HashMap<>();
		values.put("dummy", 0);
		
		Function<String, Integer> valueGetter = s -> Character.isDigit(s.charAt(0)) ? Integer.valueOf(s) : values.get(s);
		
		while (!lines.isEmpty()) {
			String line = lines.removeFirst();
			String[] split = line.split(" -> ");
			String target = split[1];
			
			if (!split[0].contains(" ")) {
				Integer value = valueGetter.apply(split[0]);
				if (value == null)
					lines.add(line);
				else
					values.put(target, value);
				continue;
			}
			
			String[] leftSplit = split[0].split(" ");
			Integer left = valueGetter.apply(leftSplit.length > 2 ? leftSplit[0] : "dummy");
			Integer right = valueGetter.apply(leftSplit[leftSplit.length - 1]);
			if (left == null || right == null) {
				lines.add(line);
				continue;
			}
			values.put(target, switch (leftSplit[leftSplit.length - 2]) {
				case "AND" -> left & right;
				case "OR" -> left | right;
				case "NOT" -> 0xFFFF ^ right;
				case "LSHIFT" -> 0xFFFF & (left << right);
				case "RSHIFT" -> left >> right;
				default -> throw new IllegalStateException("Unexpected operation: " + line);
			});
		}
		return values.get("a");
	}
}