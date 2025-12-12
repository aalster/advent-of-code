package org.advent.year2025.day10;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Day10 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day10()).runAll();
//		new DayRunner(new Day10()).run("example.txt", 2);
//		new DayRunner(new Day10()).run("input.txt", 2);
//		new DayRunner(new Day10()).run("input2.txt", 2);
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
		
		Vector add(Vector other) {
			int[] resultNumbers = new int[numbers.length];
			for (int i = 0; i < numbers.length; i++)
				resultNumbers[i] = numbers[i] + other.numbers[i];
			return new Vector(resultNumbers);
		}
		
		Vector sub(Vector other) {
			int[] resultNumbers = new int[numbers.length];
			for (int i = 0; i < numbers.length; i++)
				resultNumbers[i] = numbers[i] - other.numbers[i];
			return new Vector(resultNumbers);
		}
		
		Vector mul(int multiplier) {
			int[] resultNumbers = new int[numbers.length];
			for (int i = 0; i < numbers.length; i++)
				resultNumbers[i] = numbers[i] * multiplier;
			return new Vector(resultNumbers);
		}
		
		int maxDivisor(Vector divider) {
			int result = Integer.MAX_VALUE;
			for (int i = 0; i < numbers.length; i++)
				if (divider.numbers[i] > 0)
					result = Math.min(result, numbers[i] / divider.numbers[i]);
			return result;
		}
		
		@Override
		public String toString() {
			return Arrays.toString(numbers);
		}
	}
	
	record ButtonVariants(int index, Set<Vector> buttons) {
		
		static Vector removeBestButton(List<ButtonVariants> variants) {
			Vector button = variants.stream()
					.min(Comparator.comparing((ButtonVariants v1) -> v1.buttons.size()))
					.orElseThrow()
					.buttons.iterator().next();
			for (Iterator<ButtonVariants> iterator = variants.iterator(); iterator.hasNext(); ) {
				ButtonVariants variant = iterator.next();
				variant.buttons.remove(button);
				if (variant.buttons.isEmpty())
					iterator.remove();
			}
			return button;
		}
	}
	
	record Machine(int lightsBits, Vector[] buttons, Vector joltage) {
		
		int part1() {
			return part1Recursive(0, lightsBits);
		}
		
		int part1Recursive(int index, int currentLightsBits) {
			if (currentLightsBits == 0)
				return 0;
			if (index >= buttons.length)
				return 1000000;
			
			return Math.min(
					part1Recursive(index + 1, currentLightsBits),
					1 + part1Recursive(index + 1, currentLightsBits ^ buttons[index].asBits()));
		}
		
		int part22() {
			return part22Recursive(joltage);
		}
		
		int part22Recursive(Vector target) {
			int part1Target = 0;
			for (int number : target.numbers)
				part1Target = part1Target * 2 + (number % 2);
			return 0;
		}
		
		
//		int part2() {
//			// Составляем систему уравнений в виде
//			// b1[n] * a + b2[n] * b + b3[n] * c = joltage[n]
//			// где b1, b2, b3 - кнопки, b1[n] - коэффициент для n-ого индикатора на кнопке, a, b, c - кол-во нажатий на кнопку
//			// Например для (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7} получим:
//			// 0a + 0b + 0c + 0d + 1e + 1f = 3
//			// 0a + 1b + 0c + 0d + 0e + 1f = 5
//			// 0a + 0b + 1c + 1d + 1e + 0f = 4
//			// 1a + 1b + 0c + 1d + 0e + 0f = 7
//			// Дальше решаем систему алгоритмом Gaussian Elimination и находим x, y, z.
//
//			Vector[] system = new Vector[joltage.numbers.length + 1];
//			int[] numbers = joltage.numbers;
//			for (int i = 0; i < numbers.length; i++) {
//				int res = numbers[i];
//
//			}
//		}
		
		
		int part2() {
			if (true)
				return part22();
			
			Vector[][] buttonsByFirstIndicator = buttonsByFirstIndicator();
//			Arrays.stream(buttonsByFirstIndicator).forEach(b -> System.out.println(Arrays.toString(b)));
			
			
//			List<ButtonVariants> variants = new ArrayList<>();
//			IntStream.range(0, joltage.numbers.length).forEach(i -> variants.add(new ButtonVariants(i, new HashSet<>())));
//			for (Vector button : buttons) {
//				int[] numbers = button.numbers;
//				for (int index = 0; index < numbers.length; index++) {
//					int number = numbers[index];
//					if (number > 0)
//						variants.get(index).buttons.add(button);
//				}
//			}
//			variants.sort(Comparator.comparingInt((ButtonVariants v) -> v.buttons.size()));
			
//			Vector stats = Arrays.stream(buttons).reduce(Vector::add).orElseThrow();
//			int min = Arrays.stream(stats.numbers).min().orElse(0);
//			System.out.println(min + " -> " + Arrays.toString(stats.numbers) + " " + joltage);
//			variants.forEach(System.out::println);
//			Arrays.stream(buttons).forEach(b -> System.out.println(b + ": " + joltage.maxDivisor(b)));
			
			// [..##.#] (0,2) (1,3,4) (2,3,5) (1,2,4) (0,1,4,5) (1,2,3,4,5) (0,5) (1,2) {38,167,178,138,154,148}
			// 1 0 1 0 0 0
			// 0 1 0 1 1 0
			// 0 0 1 1 0 1
			// 0 1 1 0 1 0
			// 1 1 0 0 1 1
			// 0 1 1 1 1 1
			// 1 0 0 0 0 1
			// 3 4 4 3 4 4 - stats
			
			long started = System.currentTimeMillis();
			int res = part2Recursive(buttonsByFirstIndicator, 0, 0, joltage);
//			System.out.println(lightsBits + " " + Arrays.toString(buttons) + " " + Arrays.toString(joltage) + " -> " + res);
			System.out.println(Arrays.toString(joltage.numbers) + " - Time: " + (System.currentTimeMillis() - started));
			System.out.println();
			return res;
		}
		
		int part2Recursive(Vector[][] buttonsByFirstIndicator, int index, int buttonIndex, Vector target) {
			if (Arrays.stream(target.numbers).allMatch(n -> n == 0))
				return 0;
			if (index >= buttonsByFirstIndicator.length)
				return Integer.MAX_VALUE;
			
			Vector[] buttons = buttonsByFirstIndicator[index];
			if (buttons.length == 0)
				return part2Recursive(buttonsByFirstIndicator, index + 1, 0, target);
			
			Vector currentButton = buttons[buttonIndex];
			int maxCurrentPresses = target.maxDivisor(currentButton);
			int targetPresses = target.numbers[index];
			
			if (buttonIndex >= buttons.length - 1) {
				if (maxCurrentPresses == targetPresses) {
					int next = part2Recursive(buttonsByFirstIndicator, index + 1, 0, target.sub(currentButton.mul(targetPresses)));
					if (next < Integer.MAX_VALUE)
						return targetPresses + next;
				}
				return Integer.MAX_VALUE;
			}
			
			Vector nextTarget = target;
			
			int minPresses = Integer.MAX_VALUE;
			for (int currentPresses = 0; currentPresses <= maxCurrentPresses; currentPresses++) {
				int next = part2Recursive(buttonsByFirstIndicator, index, buttonIndex + 1, nextTarget);
				if (next < Integer.MAX_VALUE)
					minPresses = Math.min(minPresses, currentPresses + next);
				nextTarget = nextTarget.sub(currentButton);
			}
			return minPresses;
		}
		
		private Vector[][] buttonsByFirstIndicator() {
			List<Vector> unusedButtons = new ArrayList<>(Arrays.asList(buttons));
			Vector[][] result = new Vector[joltage.numbers.length][];
			for (int i = 0; i < joltage.numbers.length; i++) {
				List<Vector> current = new ArrayList<>();
				for (Iterator<Vector> iterator = unusedButtons.iterator(); iterator.hasNext(); ) {
					Vector button = iterator.next();
					if (button.numbers[i] > 0) {
						current.add(button);
						iterator.remove();
					}
				}
				result[i] = current.toArray(Vector[]::new);
			}
			while (result[result.length - 1].length == 0)
				result = Arrays.copyOf(result, result.length - 1);
			return result;
		}
		
		static Machine parse(String line) {
			String[] split = line.split(" ");
			String lightsStr = StringUtils.replaceChars(unwrap(split[0]), "#.", "10");
			int lights = Integer.parseInt(StringUtils.reverse(lightsStr), 2);
			
			// TODO отсортировать кнопки по кол-ву чисел в них?
			Vector[] buttons = new Vector[split.length - 2];
			for (int i = 1; i < split.length - 1; i++) {
				int[] array = new int[lightsStr.length()];
				Arrays.stream(unwrap(split[i]).split(",")).mapToInt(Integer::parseInt).forEach(index -> array[index] = 1);
				buttons[i - 1] = new Vector(array);
			}
			
			int[] joltage = Arrays.stream(unwrap(split[split.length - 1]).split(",")).mapToInt(Integer::parseInt).toArray();
			
			return new Machine(lights, buttons, new Vector(joltage));
		}
		
		static String unwrap(String s) {
			return s.substring(1, s.length() - 1);
		}
	}
}