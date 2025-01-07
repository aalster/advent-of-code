package org.advent.year2016.day16;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day16 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day16()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "01100", ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", "00000100100001100", "00011010100010010")
		);
	}
	
	String line;
	int size;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		line = input.nextLine();
		size = switch (file) {
			case "example.txt" -> 20;
			case "input.txt" -> 272;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		return convert(checksum(fill(convert(line), size)));
	}
	
	@Override
	public Object part2() {
		return convert(checksum(fill(convert(line), 35651584)));
	}
	
	int[] convert(String input) {
		return input.chars().map(c -> c == '1' ? 1 : 0).toArray();
	}
	
	String convert(int[] input) {
		StringBuilder result = new StringBuilder();
		for (int i : input)
			result.append(i);
		return result.toString();
	}
	
	int[] fill(int[] data, int size) {
		int currentSize = Math.min(data.length, size);
		int[] result = new int[size];
		System.arraycopy(data, 0, result, 0, currentSize);
		
		while (currentSize < size) {
			result[currentSize] = 0;
			currentSize++;
			if (currentSize >= size)
				return result;
			
			for (int i = 0; i < currentSize - 1 && currentSize + i < size; i++)
				result[currentSize + i] = result[currentSize - i - 2] == 1 ? 0 : 1;
			currentSize += currentSize - 1;
		}
		return result;
	}
	
	int[] checksum(int[] input) {
		while (input.length % 2 == 0) {
			int[] next = new int[input.length / 2];
			for (int i = 0; i < next.length; i++)
				next[i] = input[i * 2] == input[i * 2 + 1] ? 1 : 0;
			input = next;
		}
		return input;
	}
}