package org.advent.year2015.day7;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class Day7 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day7()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 65412, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 46065, 14134)
		);
	}
	
	List<String> lines;
	String outputWire;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
		outputWire = switch (file) {
			case "example.txt" -> "h";
			case "input.txt" -> "a";
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		return solve(lines);
	}
	
	@Override
	public Object part2() {
		int answer1 = solve(lines);
		return solve(lines.stream().map(l -> l.endsWith(" -> b") ? answer1 + " -> b" : l).toList());
	}
	
	int solve(List<String> lines) {
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
		return values.get(outputWire);
	}
}