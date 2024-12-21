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
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2());
	}
	
	private static long part1(List<String> lines) {
		KeypadButtons numeric = KeypadButtons.numeric();
		KeypadButtons directional = KeypadButtons.directional();
		
//		Keypad test = new Keypad(directional, null);
//		test.combination('<').forEach(System.out::println);
//		if (true)
//			return 0;
		
		int result = 0;
		for (String line : lines) {
			Keypad keypad = new Keypad(directional, new Keypad(directional, new Keypad(numeric, null)));
			System.out.print(line + ": ");
			int combinationSize = 0;
			for (char c : line.toCharArray()) {
				List<Character> combination = keypad.nestedCombination(c);
				combinationSize += combination.size();
				combination.forEach(System.out::print);
				System.out.print(" ");
			}
			System.out.println("\n" + combinationSize + " * " + numericPart(line) + "\n");
			result += combinationSize * numericPart(line);
		}
		return result;
	}
	
	private static long part2() {
		return 0;
	}
	
	static int numericPart(String line) {
		while (line.startsWith("0"))
			line = line.substring(1);
		return Integer.parseInt(line.replace("A", ""));
	}
	
	static class Keypad {
		final KeypadButtons buttons;
		final Keypad nested;
		Point position;
		
		Keypad(KeypadButtons buttons, Keypad nested) {
			this.buttons = buttons;
			this.nested = nested;
			this.position = buttons.buttons.get('A');
		}
		
		List<Character> nestedCombination(Character key) {
			if (nested == null)
				return combination(key);
			return nested.nestedCombination(key).stream().flatMap(c -> combination(c).stream()).toList();
		}
		
		List<Character> combination(Character key) {
			List<Character> combination = new ArrayList<>();
			Pair<List<Direction>, Point> directions = buttons.findDirections(position, key);
			position = directions.right();
			directions.left().stream().map(Direction::presentation).map(s -> s.charAt(0)).forEach(combination::add);
			combination.add('A');
			return combination;
		}
	}
	
	record KeypadButtons(Map<Character, Point> buttons, Point skip, Map<Point, Map<Point, List<Direction>>> cache) {
		
		Pair<List<Direction>, Point> findDirections(Point position, Character targetSymbol) {
			Point target = buttons.get(targetSymbol);
			if (position.equals(target))
				return Pair.of(List.of(), target);
			
			List<Direction> directions = new ArrayList<>();
			Direction horizontal = position.x() == target.x() ? null : target.x() < position.x() ? Direction.LEFT : Direction.RIGHT;
			Direction vertical = position.y() == target.y() ? null : target.y() < position.y() ? Direction.UP : Direction.DOWN;
			if (position.y() == skip.y() && vertical != null) {
				while (position.y() != target.y()) {
					directions.add(vertical);
					position = position.shift(vertical);
				}
			}
			if (position.x() == skip.x() && horizontal != null) {
				while (position.x() != target.x()) {
					directions.add(horizontal);
					position = position.shift(horizontal);
				}
			}
			if (vertical != null) {
				while (position.y() != target.y()) {
					directions.add(vertical);
					position = position.shift(vertical);
				}
			}
			if (horizontal != null) {
				while (position.x() != target.x()) {
					directions.add(horizontal);
					position = position.shift(horizontal);
				}
			}
			return Pair.of(directions, position);
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
}