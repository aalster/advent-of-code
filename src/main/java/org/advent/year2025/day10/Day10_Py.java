package org.advent.year2025.day10;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day10_Py extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day10_Py()).runAll();
//		new DayRunner(new Day10_Py()).run("example.txt", 1);
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 7, 33),
				new ExpectedAnswers("input.txt", 484, 19210)
		);
	}
	
	List<Machine> machines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		machines = parse(String.join("\n", Utils.readLines(input)));
//		machines.forEach(System.out::println);
	}
	
	@Override
	public Object part1() {
		return machines.stream().mapToInt(Machine::part1).sum();
	}
	
	@Override
	public Object part2() {
		return machines.stream().mapToInt(Machine::part2).sum();
	}
	
	static int asBits(int[] numbers) {
		int result = 0;
		for (int i = 0; i < numbers.length; i++)
			result |= numbers[i] << i;
		return result;
	}
	
	record ButtonCombination(int[] joltages, int presses) {}
	
	record Machine(
			int[] lights,
			List<int[]> buttons,
			int[] joltages,
			Map<Integer, List<ButtonCombination>> patterns
	) {
		
		int part1() {
			return patterns.get(asBits(lights)).stream()
					.mapToInt(ButtonCombination::presses)
					.min()
					.orElseThrow();
		}
		
		int part2() {
			return solve(joltages);
		}
		
		int solve(int[] joltages) {
			if (Arrays.stream(joltages).allMatch(j -> j == 0))
				return 0;
			
			// least significant bits
			int[] lsb = new int[joltages.length];
			for (int i = 0; i < joltages.length; i++) {
				lsb[i] = joltages[i] & 1;
			}
			
			int key = asBits(lsb);
			List<ButtonCombination> buttonCombinations = patterns.getOrDefault(key, List.of());
			
			int minResult = Integer.MAX_VALUE;
			
			for (ButtonCombination c : buttonCombinations) {
				int[] nextJoltages = subtractAndHalf(joltages, c.joltages());
				
				if (isValid(nextJoltages)) {
					int rest = solve(nextJoltages);
					if (rest < Integer.MAX_VALUE) {
						int result = c.presses() + 2 * rest;
						minResult = Math.min(minResult, result);
					}
				}
			}
			return minResult;
		}
	}
	
	public static List<Machine> parse(String input) {
		List<Machine> machines = new ArrayList<>();
		
		for (String line : input.split("\\R")) {
			if (line.isBlank()) continue;
			
			String[] parts = line.split("\\s+");
			String lightSpec = parts[0];
			String joltageSpec = parts[parts.length - 1];
			
			List<int[]> buttons = new ArrayList<>();
			
			// Lights
			String ls = lightSpec.substring(1, lightSpec.length() - 1);
			int[] lights = new int[ls.length()];
			for (int i = 0; i < ls.length(); i++) {
				lights[i] = (ls.charAt(i) == '#') ? 1 : 0;
			}
			
			// Buttons
			for (int i = 1; i < parts.length - 1; i++) {
				String b = parts[i].substring(1, parts[i].length() - 1);
				String[] nums = b.split(",");
				int[] btn = new int[nums.length];
				for (int j = 0; j < nums.length; j++) {
					btn[j] = Integer.parseInt(nums[j]);
				}
				buttons.add(btn);
			}
			
			// Patterns map
			Map<Integer, List<ButtonCombination>> patterns = new HashMap<>();
			
			int buttonCount = buttons.size();
			
			// iterate all bitmasks
			for (int n = 0; n < (1 << buttonCount); n++) {
				// mask bits
				int[] mask = new int[buttonCount];
				for (int i = 0; i < buttonCount; i++) {
					mask[i] = (n & (1 << (buttonCount - 1 - i))) != 0 ? 1 : 0;
				}
				
				int[] lightResult = new int[lights.length];
				int[] joltageDelta = new int[lights.length];
				int presses = 0;
				
				for (int i = 0; i < buttonCount; i++) {
					if (mask[i] == 1) {
						int[] btn = buttons.get(i);
						for (int light : btn) {
							lightResult[light] ^= 1;
							joltageDelta[light] += 1;
						}
						presses++;
					}
				}
				
				int key = asBits(lightResult);
				patterns.computeIfAbsent(key, k -> new ArrayList<>())
						.add(new ButtonCombination(joltageDelta, presses));
			}
			
			// joltage target
			String js = joltageSpec.substring(1, joltageSpec.length() - 1);
			String[] jsplit = js.split(",");
			int[] joltages = new int[jsplit.length];
			for (int i = 0; i < jsplit.length; i++) {
				joltages[i] = Integer.parseInt(jsplit[i]);
			}
			
			machines.add(new Machine(lights, buttons, joltages, patterns));
		}
		
		return machines;
	}
	
	// Utility: (j1 - j2) // 2
	public static int[] subtractAndHalf(int[] j1, int[] j2) {
		int[] out = new int[j1.length];
		for (int i = 0; i < j1.length; i++) {
			out[i] = (j1[i] - j2[i]) / 2;
		}
		return out;
	}
	
	public static boolean isValid(int[] j) {
		for (int x : j)
			if (x < 0)
				return false;
		return true;
	}
}
