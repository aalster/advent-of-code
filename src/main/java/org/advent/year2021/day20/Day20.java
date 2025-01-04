package org.advent.year2021.day20;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day20 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day20()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 35, 3351),
				new ExpectedAnswers("input.txt", 4917, 16389)
		);
	}
	
	Map<Integer, Integer> enhancement;
	Map<Point, Integer> pixels;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<List<String>> lines = Utils.splitByEmptyLine(Utils.readLines(input));
		enhancement = new HashMap<>();
		int index = 0;
		for (char c : String.join("", lines.getFirst()).toCharArray()) {
			enhancement.put(index, c == '#' ? 1 : 0);
			index++;
		}
		pixels = Point.readFieldMap(lines.getLast()).entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() == '#' ? 1 : 0));
	}
	
	@Override
	public Object part1() {
		return solve(enhancement, pixels, 2);
	}
	
	@Override
	public Object part2() {
		return solve(enhancement, pixels, 50);
	}
	
	long solve(Map<Integer, Integer> enhancement, Map<Point, Integer> pixels, int count) {
		Image image = new Image(pixels, 0);
		while (count > 0) {
			count--;
			image = image.enhance(enhancement);
		}
		return image.countLitPixels();
	}
	
	record Image(Map<Point, Integer> pixels, int infinityPixel) {
		
		Image enhance(Map<Integer, Integer> enhancement) {
			Point min = Point.minBound(pixels.keySet()).shift(-1, -1);
			Point max = Point.maxBound(pixels.keySet()).shift(1, 1);
			
			Map<Point, Integer> nextPixels = new HashMap<>();
			for (int x = min.x(); x <= max.x(); x++) {
				for (int y = min.y(); y <= max.y(); y++) {
					Point current = new Point(x, y);
					int[] bits = Stream.of(current.shift(Direction.UP), current, current.shift(Direction.DOWN))
							.flatMap(p -> Stream.of(p.shift(Direction.LEFT), p, p.shift(Direction.RIGHT)))
							.mapToInt(p -> pixels.getOrDefault(p, infinityPixel))
							.toArray();
					nextPixels.put(current, enhancement.get(enhancementNumber(bits)));
				}
			}
			int[] infinityBits = new int[9];
			Arrays.fill(infinityBits, infinityPixel);
			return new Image(nextPixels, enhancement.get(enhancementNumber(infinityBits)));
		}
		
		int enhancementNumber(int[] bits) {
			int enhancementNumber = 0;
			for (int bit : bits)
				enhancementNumber = enhancementNumber * 2 + bit;
			return enhancementNumber;
		}
		
		long countLitPixels() {
			if (infinityPixel == 1)
				throw new RuntimeException("Infinity");
			return pixels.values().stream().filter(n -> n == 1).count();
		}
	}
}