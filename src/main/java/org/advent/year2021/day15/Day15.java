package org.advent.year2021.day15;

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
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day15 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day15()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 40, 315),
				new ExpectedAnswers("input.txt", 714, 2948)
		);
	}
	
	Map<Point, Integer> field;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		field = Point.readFieldMap(Utils.readLines(input)).entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() - '0'));
	}
	
	@Override
	public Object part1() {
		return solve(field);
	}
	
	@Override
	public Object part2() {
		int width = field.keySet().stream().mapToInt(Point::x).max().orElse(0) + 1;
		int height = field.keySet().stream().mapToInt(Point::y).max().orElse(0) + 1;
		
		Map<Point, Integer> extendedField = new HashMap<>();
		for (int y = 0; y < 5; y++) {
			int _y = y;
			int dy = y * height;
			for (int x = 0; x < 5; x++) {
				int _x = x;
				int dx = x * width;
				extendedField.putAll(field.entrySet().stream().collect(Collectors.toMap(
						e -> e.getKey().shift(dx, dy),
						e -> wrapDangerLevel(e.getValue() + _x + _y)
				)));
			}
		}
		
		return solve(extendedField);
	}
	
	int wrapDangerLevel(int sum) {
		return sum <= 9 ? sum : sum - 9;
	}
	
	long solve(Map<Point, Integer> dangerField) {
		Map<Point, Long> minTotalDanger = new HashMap<>();
		
		Queue<Path> paths = new PriorityQueue<>(Comparator.comparing(Path::danger));
		paths.add(new Path(Point.ZERO, 0));
		
		while (!paths.isEmpty()) {
			Path path = paths.poll();
			for (Direction d : Direction.values()) {
				Point nextPosition = d.shift(path.position);
				Integer danger = dangerField.get(nextPosition);
				if (danger == null)
					continue;
				
				long nextDanger = path.danger + danger;
				Long minDanger = minTotalDanger.get(nextPosition);
				if (minDanger != null && minDanger <= nextDanger)
					continue;
				
				minTotalDanger.put(nextPosition, nextDanger);
				paths.add(new Path(nextPosition, nextDanger));
			}
		}
		
		Point end = Point.maxBound(dangerField.keySet());
		return minTotalDanger.getOrDefault(end, -1L);
	}
	
	record Path(Point position, long danger) {
	}
}