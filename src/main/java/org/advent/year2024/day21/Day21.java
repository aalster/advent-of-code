package org.advent.year2024.day21;

import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day21 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day21.class, "example.txt");
		List<String> lines = Utils.readLines(input);
		
		long start = System.currentTimeMillis();
		System.out.println("Answer 1: " + solve(lines, 2));
//		System.out.println("Answer 2: " + solve(lines, 25));
		System.out.println((System.currentTimeMillis() - start) + "ms");
	}
	
	private static long solve(List<String> lines, int nestedLevel) {
		KeypadButtons numeric = KeypadButtons.numeric();
		KeypadButtons directional = KeypadButtons.directional();
		
		Keypad keypad = new Keypad(numeric, null);
		while (nestedLevel > 0) {
			nestedLevel--;
			keypad = new Keypad(directional, keypad);
		}
		
		int result = 0;
		for (String line : lines) {
			String firstCombination = keypad.nestedCombination(line).getFirst();
			System.out.println(line + ": " + firstCombination.length() + " " + firstCombination);
			result += firstCombination.length() * Integer.parseInt(line.replace("A", ""));
		}
		return result;
	}
	
	record Keypad(KeypadButtons buttons, Keypad nested) {
		
		List<String> nestedCombination(String key) {
			if (nested == null)
				return combinations(key);
			List<String> combinations = nested.nestedCombination(key).stream().flatMap(c -> combinations(c).stream()).toList();
			int minLength = combinations.stream().mapToInt(String::length).min().orElseThrow();
			return combinations.stream().filter(c -> c.length() == minLength).toList();
		}
		
		List<String> combinations(String key) {
			char[] chars = key.toCharArray();
			List<List<String>> variants = new ArrayList<>();
			variants.add(buttons.moves('A', chars[0]));
			for (int i = 1; i < chars.length; i++)
				variants.add(buttons.moves(chars[i - 1], chars[i]));
			return permutations(variants);
		}
	}
	
	record KeypadButtons(Map<Character, Point> buttons, Point skip, Map<Character, Map<Character, List<String>>> cache) {
		
		List<String> moves(Character currentSymbol, Character targetSymbol) {
			return cache.computeIfAbsent(currentSymbol, k -> new HashMap<>())
					.computeIfAbsent(targetSymbol, k -> calcMoves(currentSymbol, targetSymbol));
		}
		
		List<String> calcMoves(Character currentSymbol, Character targetSymbol) {
			Point position = buttons.get(currentSymbol);
			Point target = buttons.get(targetSymbol);
			if (position.equals(target))
				return List.of("A");
			
			Direction horizontal = position.x() == target.x() ? null : target.x() < position.x() ? Direction.LEFT : Direction.RIGHT;
			Direction vertical = position.y() == target.y() ? null : target.y() < position.y() ? Direction.UP : Direction.DOWN;
			
			List<Pair<String, Point>> possibleVariants = new ArrayList<>(List.of(Pair.of("", position)));
			List<String> variants = new ArrayList<>();
			
			while (!possibleVariants.isEmpty()) {
				Pair<String, Point> variant = possibleVariants.removeFirst();
				if (variant.right().equals(skip))
					continue;
				if (variant.right().equals(target)) {
					variants.add(variant.left() + "A");
					continue;
				}
				if (variant.right().x() != target.x() && horizontal != null)
					possibleVariants.add(Pair.of(variant.left() + horizontal.presentation(), variant.right().shift(horizontal)));
				if (variant.right().y() != target.y() && vertical != null)
					possibleVariants.add(Pair.of(variant.left() + vertical.presentation(), variant.right().shift(vertical)));
			}
			return variants;
		}
		
		static KeypadButtons numeric() {
			Map<Character, Point> buttons = Map.ofEntries(
					Map.entry('7', new Point(0, 0)), Map.entry('8', new Point(1, 0)), Map.entry('9', new Point(2, 0)),
					Map.entry('4', new Point(0, 1)), Map.entry('5', new Point(1, 1)), Map.entry('6', new Point(2, 1)),
					Map.entry('1', new Point(0, 2)), Map.entry('2', new Point(1, 2)), Map.entry('3', new Point(2, 2)),
					Map.entry('0', new Point(1, 3)), Map.entry('A', new Point(2, 3))
			);
			return new KeypadButtons(buttons, new Point(0, 3), new HashMap<>());
		}
		
		static KeypadButtons directional() {
			Map<Character, Point> buttons = Map.ofEntries(
					Map.entry('^', new Point(1, 0)), Map.entry('A', new Point(2, 0)),
					Map.entry('<', new Point(0, 1)), Map.entry('v', new Point(1, 1)), Map.entry('>', new Point(2, 1))
			);
			return new KeypadButtons(buttons, new Point(0, 0), new HashMap<>());
		}
	}
	
	static List<String> permutations(List<List<String>> variants) {
		variants = new ArrayList<>(variants);
		List<String> result = variants.removeFirst();
		while (!variants.isEmpty()) {
			List<String> next = variants.removeFirst();
			result = result.stream()
					.flatMap(r -> next.stream().map(n -> r + n))
					.toList();
		}
		return result;
	}
}