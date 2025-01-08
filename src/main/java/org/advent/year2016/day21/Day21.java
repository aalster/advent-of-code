package org.advent.year2016.day21;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day21 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day21()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "decab", "abcde"),
				new ExpectedAnswers("input.txt", "agcebfdh", "afhdbegc")
		);
	}
	
	List<Operation> operations;
	String password;
	String scrambledPassword;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		operations = Utils.readLines(input).stream().map(Operation::parse).toList();
		password = switch (file) {
			case "example.txt" -> "abcde";
			case "input.txt" -> "abcdefgh";
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
		scrambledPassword = switch (file) {
			case "example.txt" -> "decab";
			case "input.txt" -> "fbgdceah";
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		char[] chars = password.toCharArray();
		for (Operation operation : operations)
			operation.apply(chars);
		return new String(chars);
	}
	
	@Override
	public Object part2() {
		char[] chars = scrambledPassword.toCharArray();
		for (Operation operation : operations.reversed())
			operation.reverse(chars);
		return new String(chars);
	}
	
	interface Operation {
		interface Symmetric extends Operation {
			@Override
			default void reverse(char[] chars) {
				apply(chars);
			}
		}
		
		void apply(char[] chars);
		void reverse(char[] chars);
		
		static Operation parse(String line) {
			if (line.startsWith("swap position")) {
				String[] split = line.replace("swap position ", "").split(" with position ");
				return new SwapPosition(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
			}
			if (line.startsWith("swap letter")) {
				String[] split = line.replace("swap letter ", "").split(" with letter ");
				return new SwapLetter(split[0].charAt(0), split[1].charAt(0));
			}
			if (line.startsWith("rotate based on position")) {
				return new RotateBasedOnPosition(line.charAt(line.length() - 1));
			}
			if (line.startsWith("rotate")) {
				return new Rotate((line.contains("left") ? -1 : 1) * Integer.parseInt(line.split(" ")[2]));
			}
			if (line.startsWith("reverse positions")) {
				String[] split = line.replace("reverse positions ", "").split(" through ");
				return new ReversePositions(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
			}
			if (line.startsWith("move position")) {
				String[] split = line.replace("move position ", "").split(" to position ");
				return new MovePosition(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
			}
			throw new IllegalStateException("Unexpected value: " + line);
		}
	}
	
	record SwapPosition(int left, int right) implements Operation.Symmetric {
		
		@Override
		public void apply(char[] chars) {
			char leftChar = chars[left];
			chars[left] = chars[right];
			chars[right] = leftChar;
		}
	}
	
	record SwapLetter(char leftChar, char rightChar) implements Operation.Symmetric {
		
		@Override
		public void apply(char[] chars) {
			List<Integer> leftIndexes = allIndexes(chars, leftChar);
			List<Integer> rightIndexes = allIndexes(chars, rightChar);
			for (int index : leftIndexes)
				chars[index] = rightChar;
			for (int index : rightIndexes)
				chars[index] = leftChar;
		}
		
		static List<Integer> allIndexes(char[] chars, char target) {
			List<Integer> indexes = new ArrayList<>();
			int index = -1;
			while ((index = ArrayUtils.indexOf(chars, target, index + 1)) != -1)
				indexes.add(index);
			return indexes;
		}
	}
	
	record RotateBasedOnPosition(char targetChar) implements Operation {
		
		@Override
		public void apply(char[] chars) {
			int index = ArrayUtils.indexOf(chars, targetChar);
			if (index == -1)
				throw new IllegalStateException("Target char not found: " + targetChar);
			int delta = 1 + index + (index >= 4 ? 1 : 0);
			Rotate.rotate(chars, delta);
		}
		
		@Override
		public void reverse(char[] chars) {
			for (int delta = 0; delta < chars.length; delta++) {
				char[] copy = Arrays.copyOf(chars, chars.length);
				Rotate.rotate(copy, -delta);
				apply(copy);
				if (Arrays.equals(chars, copy)) {
					Rotate.rotate(chars, -delta);
					return;
				}
			}
			throw new RuntimeException("Cannot find reverse delta for " + new String(chars) + " char " + targetChar);
		}
	}
	
	record Rotate(int delta) implements Operation {
		
		@Override
		public void apply(char[] chars) {
			rotate(chars, delta);
		}
		
		@Override
		public void reverse(char[] chars) {
			rotate(chars, -delta);
		}
		
		static void rotate(char[] chars, int delta) {
			while (delta < 0)
				delta += chars.length;
			if (delta == 0)
				return;
			
			char[] prev = Arrays.copyOf(chars, chars.length);
			for (int i = 0; i < chars.length; i++)
				chars[(i + delta) % chars.length] = prev[i];
		}
	}
	
	record ReversePositions(int start, int end) implements Operation.Symmetric {
		
		@Override
		public void apply(char[] chars) {
			int from = start;
			int to = end;
			while (from < to) {
				char startChar = chars[from];
				chars[from] = chars[to];
				chars[to] = startChar;
				from++;
				to--;
			}
		}
	}
	
	record MovePosition(int from, int to) implements Operation {
		
		@Override
		public void apply(char[] chars) {
			movePosition(chars, from, to);
		}
		
		@Override
		public void reverse(char[] chars) {
			movePosition(chars, to, from);
		}
		
		static void movePosition(char[] chars, int from, int to) {
			char targetChar = chars[from];
			if (to < from)
				System.arraycopy(chars, to, chars, to + 1, from - to);
			else
				System.arraycopy(chars, from + 1, chars, from, to - from);
			chars[to] = targetChar;
		}
	}
}