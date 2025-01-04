package org.advent.year2021.day9;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day9 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day9()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 15, 1134),
				new ExpectedAnswers("input.txt", 478, 1327014)
		);
	}
	
	Map<Point, Integer> heights;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		heights = new HashMap<>();
		int y = 0;
		while (input.hasNext()) {
			int x = 0;
			for (char c : input.nextLine().toCharArray()) {
				heights.put(new Point(x, y), c - '0');
				x++;
			}
			y++;
		}
	}
	
	@Override
	public Object part1() {
		return findLowPoints(heights).stream().mapToInt(heights::get).map(h -> h + 1).sum();
	}
	
	@Override
	public Object part2() {
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
				.limit(3)
				.reduce(1, (l, r) -> l * r);
	}
	
	Set<Point> findLowPoints(Map<Point, Integer> heights) {
		return heights.keySet().stream()
				.filter(point -> Direction.stream()
						.map(point::move)
						.mapToInt(p -> heights.getOrDefault(p, 9))
						.noneMatch(h -> h <= heights.get(point)))
				.collect(Collectors.toSet());
	}
}