package org.advent.year2021.day20;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day20 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day20.class, "input.txt");
		Map<Integer, Integer> enhancement = new HashMap<>();
		int index = 0;
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty())
				break;
			for (char symbol : line.toCharArray()) {
				enhancement.put(index, symbol == '#' ? 1 : 0);
				index++;
			}
		}
		Map<Point, Integer> pixels = Point.readFieldMap(Utils.readLines(input)).entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() == '#' ? 1 : 0));
		
		System.out.println("Answer 1: " + part1(enhancement, pixels));
		System.out.println("Answer 2: " + part2(enhancement, pixels));
	}
	
	private static long part1(Map<Integer, Integer> enhancement, Map<Point, Integer> pixels) {
		return solve(enhancement, pixels, 2);
	}
	
	private static long part2(Map<Integer, Integer> enhancement, Map<Point, Integer> pixels) {
		return solve(enhancement, pixels, 50);
	}
	
	static long solve(Map<Integer, Integer> enhancement, Map<Point, Integer> pixels, int count) {
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
		
		private int enhancementNumber(int[] bits) {
			int enhancementNumber = 0;
			for (int bit : bits)
				enhancementNumber = enhancementNumber * 2 + bit;
			return enhancementNumber;
		}
		
		public long countLitPixels() {
			if (infinityPixel == 1)
				throw new RuntimeException("Infinity");
			return pixels.values().stream().filter(n -> n == 1).count();
		}
	}
}