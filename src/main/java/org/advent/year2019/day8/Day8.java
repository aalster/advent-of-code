package org.advent.year2019.day8;

import org.advent.common.Utils;
import org.advent.common.ascii.AsciiLetters;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day8 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day8()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 2480, "ZYBLH")
		);
	}
	
	int[] pixels;
	int width = 25;
	int height = 6;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		pixels = input.nextLine().chars().map(n -> n - '0').toArray();
	}
	
	@Override
	public Object part1() {
		int[][] layers = split(pixels, width * height);
		
		int[] minZeroesLayer = null;
		int minZeroes = Integer.MAX_VALUE;
		for (int[] layer : layers) {
			int zeroes = (int) Arrays.stream(layer).filter(n -> n == 0).count();
			if (zeroes < minZeroes) {
				minZeroes = zeroes;
				minZeroesLayer = layer;
			}
		}
		if (minZeroesLayer == null)
			return 0;
		
		int ones = 0;
		int twos = 0;
		for (int n : minZeroesLayer) {
			if (n == 1)
				ones++;
			else if (n == 2)
				twos++;
		}
		return ones * twos;
	}
	
	@Override
	public Object part2() {
		int size = width * height;
		int[][] layers = split(pixels, size);
		
		int[] result = new int[size];
		Arrays.fill(result, 2);
		for (int[] layer : layers)
			for (int i = 0; i < size; i++)
				if (result[i] == 2)
					result[i] = layer[i];
		
		StringBuilder resultImage = new StringBuilder(size);
		for (int[] row : split(result, width)) {
			for (int x = 0; x < width; x++)
				resultImage.append(row[x]);
			resultImage.append("\n");
		}
		return AsciiLetters.parse(resultImage.toString(), '1');
	}
	
	int[][] split(int[] values, int size) {
		int[][] split = new int[values.length / size][];
		for (int index = 0; index < values.length / size; index++)
			split[index] = Arrays.copyOfRange(values, index * size, (index + 1) * size);
		return split;
	}
}