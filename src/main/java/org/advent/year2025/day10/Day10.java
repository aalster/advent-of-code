package org.advent.year2025.day10;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day10 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day10()).runAll();
//		new DayRunner(new Day10()).run("example.txt", 2);
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 7, 33),
				new ExpectedAnswers("input.txt", 484, null)
		);
	}
	
	List<Machine> machines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		machines = Utils.readLines(input).stream().map(Machine::parse).toList();
	}
	
	@Override
	public Object part1() {
		return machines.stream().mapToInt(Machine::part1).sum();
	}
	
	@Override
	public Object part2() {
//		machines.forEach(m -> System.out.println(m.lights + " " + Arrays.toString(m.buttons) + " " + Arrays.toString(m.joltage)));
		return machines.stream().mapToInt(Machine::part2).sum();
	}
	
	record Machine(int lights, int[] buttons, int[] joltage) {
		
		int part1() {
			return part1Recursive(0, 0);
		}
		
		int part1Recursive(int index, int result) {
			if (lights == result)
				return 0;
			if (index >= buttons.length)
				return 1000000;
			
			return Math.min(
					part1Recursive(index + 1, result),
					1 + part1Recursive(index + 1, result ^ buttons[index]));
		}
		
		int part2() {
			int res = part2Recursive(0, new int[joltage.length]);
			System.out.println(lights + " " + Arrays.toString(buttons) + " " + Arrays.toString(joltage) + " -> " + res);
			return res;
		}
		
		int part2Recursive(int index, int[] result) {
			boolean completed = true;
			for (int i = 0; i < joltage.length; i++) {
				if (joltage[i] < result[i])
					return 1000000;
				completed = completed && result[i] == joltage[i];
			}
			if (completed)
				return 0;
			if (index >= buttons.length)
				return 1000000;
			
			int minPresses = Integer.MAX_VALUE;
			int[] resultCopy = Arrays.copyOf(result, result.length);
			for (int n = 0; n < 120; n++) {
				int next = part2Recursive(index + 1, resultCopy);
				minPresses = Math.min(minPresses, n + next);
				for (int bit = 0; bit < joltage.length; bit++)
					resultCopy[bit] += (buttons[index] & (1 << bit)) > 0 ? 1 : 0;
			}
			return minPresses;
		}
		
		static Machine parse(String line) {
			String[] split = line.split(" ");
			String lightsStr = StringUtils.replaceChars(unwrap(split[0]), "#.", "10");
			int lights = Integer.parseInt(StringUtils.reverse(lightsStr), 2);
			
			int[] buttons = new int[split.length - 2];
			for (int i = 1; i < split.length - 1; i++) {
				int button = 0;
				for (int bit : Arrays.stream(unwrap(split[i]).split(",")).mapToInt(Integer::parseInt).toArray())
					button |= 1 << bit;
				buttons[i - 1] = button;
			}
			
			int[] joltage = Arrays.stream(unwrap(split[split.length - 1]).split(",")).mapToInt(Integer::parseInt).toArray();
			
			return new Machine(lights, buttons, joltage);
		}
		
		static String unwrap(String s) {
			return s.substring(1, s.length() - 1);
		}
	}
}