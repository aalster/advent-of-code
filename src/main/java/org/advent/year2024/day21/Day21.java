package org.advent.year2024.day21;

import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day21 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day21.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		
		System.out.println("Answer 1: " + solve(lines, 2));
		System.out.println("Answer 2: " + solve(lines, 25));
	}
	
	private static long solve(List<String> lines, int nestedLevel) {
		Keypad keypad = null;
		while (nestedLevel-- > 0)
			keypad = new Keypad(KeypadButtons.directional, keypad);
		keypad = new Keypad(KeypadButtons.numeric, keypad);
		
		long result = 0;
		for (String line : lines)
			result += keypad.minCombinationLength(line) * Integer.parseInt(line.replace("A", ""));
		return result;
	}
	
	record Keypad(KeypadButtons buttons, Keypad nested, Map<String, Long> cache) {
		
		public Keypad(KeypadButtons buttons, Keypad nested) {
			this(buttons, nested, new HashMap<>());
		}
		
		long minCombinationLength(String key) {
			return cache.computeIfAbsent(key, this::calcMinCombinationLength);
		}
		
		long calcMinCombinationLength(String key) {
			char[] chars = key.toCharArray();
			Stream<List<String>> charPairs = Stream.concat(
							Stream.of(Pair.of('A', chars[0])),
							IntStream.range(1, chars.length).mapToObj(i -> Pair.of(chars[i - 1], chars[i])))
					.map(p -> buttons.moves(p.left(), p.right()));
			
			if (nested == null)
				return charPairs
						.mapToLong(keys -> keys.stream().mapToLong(String::length).min().orElseThrow())
						.sum();
			return charPairs
					.mapToLong(keys -> keys.stream().mapToLong(nested::minCombinationLength).min().orElseThrow())
					.sum();
		}
	}
	
	record KeypadButtons(Map<Character, Point> buttons, Set<Point> allowedPositions) {
		static KeypadButtons numeric = KeypadButtons.parse(List.of(
				"789",
				"456",
				"123",
				" 0A"));
		static KeypadButtons directional = KeypadButtons.parse(List.of(
				" ^A",
				"<v>"));
		
		List<String> moves(Character currentSymbol, Character targetSymbol) {
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
				if (!allowedPositions.contains(variant.right()))
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
		
		static KeypadButtons parse(List<String> lines) {
			Map<Character, Point> buttons = Point.readField(lines).entrySet().stream()
					.filter(e -> e.getKey() != ' ')
					.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFirst()));
			return new KeypadButtons(buttons, new HashSet<>(buttons.values()));
		}
	}
}