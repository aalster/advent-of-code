package org.advent.year2017.day10;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Day10 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day10()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 12, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, "a2582a3a0e66e6e86e3812dcb672a272"),
				new ExpectedAnswers("example3.txt", ExpectedAnswers.IGNORE, "33efeb34ea91902bb2f59c9920caa6cd"),
				new ExpectedAnswers("example4.txt", ExpectedAnswers.IGNORE, "3efbe78a8d82f29979031a4aa0b16a9d"),
				new ExpectedAnswers("example5.txt", ExpectedAnswers.IGNORE, "63960835bcdc130f0b66d7ff4f6a5a8e"),
				new ExpectedAnswers("input.txt", 4114, "2f8c3d2100fdd57cec130d928b0fd2dd")
		);
	}
	
	String line;
	int part1Size;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		line = input.hasNext() ? input.nextLine() : "";
		part1Size = "example.txt".equals(file) ? 5 : 256;
	}
	
	@Override
	public Object part1() {
		int[] lengths = Arrays.stream(line.split(",\\s?")).mapToInt(Integer::parseInt).toArray();
		int[] numbers = IntStream.range(0, part1Size).toArray();
		knotHash(numbers, lengths, 1);
		return numbers[0] * numbers[1];
	}
	
	@Override
	public Object part2() {
		int[] lengths = IntStream.concat(line.chars(), IntStream.of(17, 31, 73, 47, 23)).toArray();
		int[] numbers = IntStream.range(0, 256).toArray();
		knotHash(numbers, lengths, 64);
		return denseHash(numbers);
	}
	
	void knotHash(int[] numbers, int[] lengths, int rounds) {
		int index = 0;
		int skip = 0;
		while (rounds-- > 0) {
			for (int length : lengths) {
				reverse(numbers, index, index + length - 1);
				index = (index + length + skip) % numbers.length;
				skip++;
			}
		}
	}
	
	void reverse(int[] numbers, int start, int end) {
		while (start < end) {
			int temp = numbers[start % numbers.length];
			numbers[start % numbers.length] = numbers[end % numbers.length];
			numbers[end % numbers.length] = temp;
			start++;
			end--;
		}
	}
	
	String denseHash(int[] numbers) {
		byte[] result = new byte[numbers.length / 16];
		for (int group = 0; group < result.length; group++)
			for (int index = 0; index < 16 && index + group < numbers.length; index++)
				result[group] ^= (byte) numbers[group * 16 + index];
		return HexFormat.of().formatHex(result);
	}
}