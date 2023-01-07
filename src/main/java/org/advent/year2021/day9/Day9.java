package org.advent.year2021.day9;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day9 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day9.class, "input.txt");
		Map<Point, Integer> heights = new HashMap<>();
		int y = 0;
		while (input.hasNext()) {
			int x = 0;
			for (char c : input.nextLine().toCharArray()) {
				heights.put(new Point(x, y), c - '0');
				x++;
			}
			y++;
		}
		
		System.out.println("Answer 1: " + part1(heights));
		System.out.println("Answer 2: " + part2(heights, 3));
	}
	
	private static int part1(Map<Point, Integer> heights) {
		return findLowPoints(heights).stream().mapToInt(heights::get).map(h -> h + 1).sum();
	}
	
	private static int part2(Map<Point, Integer> heights, int count) {
		Map<Point, Integer> notProcessed = new HashMap<>(heights);
		return findLowPoints(heights).stream()
				.map(lowPoint -> {
					int basinSize = 0;
					Set<Point> chunk = Set.of(lowPoint);
					while (!chunk.isEmpty()) {
						basinSize += chunk.size();
						chunk = chunk.stream()
								.peek(notProcessed::remove)
								.flatMap(p -> Direction.stream().map(p::move))
								.filter(p -> notProcessed.getOrDefault(p, 9) < 9)
								.collect(Collectors.toSet());
					}
					return basinSize;
				})
				.sorted(Comparator.<Integer>naturalOrder().reversed())
				.limit(count)
				.reduce(1, (l, r) -> l * r);
	}
	
	private static Set<Point> findLowPoints(Map<Point, Integer> heights) {
		return heights.keySet().stream()
				.filter(point -> Direction.stream()
						.map(point::move)
						.mapToInt(p -> heights.getOrDefault(p, 9))
						.noneMatch(h -> h <= heights.get(point)))
				.collect(Collectors.toSet());
	}
}