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

public class Day10 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day10()).runAll();
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
		machines = Utils.readLines(input).stream().map(Machine::parse).toList();
	}
	
	@Override
	public Object part1() {
		return machines.stream().mapToInt(Machine::part1).sum();
	}
	
	@Override
	public Object part2() {
		return machines.stream().mapToInt(Machine::part2).sum();
	}
	
	record Vector(int[] numbers) {
		
		int asBits() {
			int result = 0;
			for (int i = 0; i < numbers.length; i++)
				result |= numbers[i] << i;
			return result;
		}
		
		int leastSignificantBits() {
			int[] lsb = new int[numbers.length];
			for (int i = 0; i < numbers.length; i++)
				lsb[i] = numbers[i] & 1;
			return new Vector(lsb).asBits();
		}
		
		Vector subtractAndHalf(Vector sub) {
			int[] result = new int[numbers.length];
			for (int i = 0; i < numbers.length; i++)
				result[i] = (numbers[i] - sub.numbers[i]) / 2;
			return new Vector(result);
		}
		
		boolean isValid() {
			for (int n : numbers)
				if (n < 0)
					return false;
			return true;
		}
		
		boolean isZero() {
			for (int n : numbers)
				if (n != 0)
					return false;
			return true;
		}
		
		static Vector parse(String line) {
			return new Vector(Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray());
		}
	}
	
	record ButtonCombination(Vector joltages, int presses) {
	}
	
	record Machine(Vector lights, Vector[] buttons, Vector joltages, Map<Integer, List<ButtonCombination>> combinations) {
		
		int part1() {
			return combinations.get(lights.asBits()).stream().mapToInt(ButtonCombination::presses).min().orElseThrow();
		}
		
		int part2() {
			// https://www.reddit.com/r/adventofcode/comments/1pity70/comment/ntp30di/
			// https://www.reddit.com/r/adventofcode/comments/1pk87hl/2025_day_10_part_2_bifurcate_your_way_to_victory/
			// https://aoc.winslowjosiah.com/solutions/2025/day/10/
			return part2Recursive(joltages);
		}
		
		int part2Recursive(Vector joltages) {
			if (joltages.isZero())
				return 0;
			if (!joltages.isValid())
				return Integer.MAX_VALUE;
			
			int minResult = Integer.MAX_VALUE;
			for (ButtonCombination c : combinations.getOrDefault(joltages.leastSignificantBits(), List.of())) {
				int next = part2Recursive(joltages.subtractAndHalf(c.joltages));
				if (next < Integer.MAX_VALUE)
					minResult = Math.min(minResult, c.presses() + 2 * next);
			}
			return minResult;
		}
		
		static Machine parse(String line) {
			String[] split = line.split("\\s+");
			
			Vector lights = new Vector(unwrap(split[0]).chars().map(c -> c == '#' ? 1 : 0).toArray());
			
			Vector[] buttons = Arrays.stream(split, 1, split.length - 1)
					.map(i -> Vector.parse(unwrap(i)))
					.toArray(Vector[]::new);
			
			Vector joltages = Vector.parse(unwrap(split[split.length - 1]));
			
			return new Machine(lights, buttons, joltages, buttonCombinations(buttons, lights.numbers.length));
		}
		
		static Map<Integer, List<ButtonCombination>> buttonCombinations(Vector[] buttons, int lightsCount) {
			Map<Integer, List<ButtonCombination>> combinations = new HashMap<>();
			
			// Все возможные варианты нажатия кнопок
			int maxMask = 1 << buttons.length;
			for (int n = 0; n < maxMask; n++) {
				int lightBits = 0;
				int[] joltageDelta = new int[lightsCount];
				int presses = 0;
				
				for (int i = 0; i < buttons.length; i++) {
					if ((n & 1 << i) != 0) {
						for (int light : buttons[i].numbers) {
							lightBits ^= 1 << light;
							joltageDelta[light] += 1;
						}
						presses++;
					}
				}
				combinations.computeIfAbsent(lightBits, k -> new ArrayList<>())
						.add(new ButtonCombination(new Vector(joltageDelta), presses));
			}
			return combinations;
		}
	}
	
	static String unwrap(String text) {
		return text.substring(1, text.length() - 1);
	}
}
