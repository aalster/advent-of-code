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
	
	int[][] dangerField;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		Map<Point, Integer> field = Point.readFieldMap(Utils.readLines(input)).entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() - '0'));
		Point end = Point.maxBound(field.keySet());
		dangerField = new int[end.x() + 1][end.y() + 1];
		field.forEach((p, d) -> dangerField[p.y()][p.x()] = d);
	}
	
	@Override
	public Object part1() {
		return solve(dangerField);
	}
	
	@Override
	public Object part2() {
		int height = dangerField.length;
		int width = dangerField[0].length;
		int[][] extendedField = new int[height * 5][width * 5];
		
		for (int chunkY = 0; chunkY < 5; chunkY++) {
			int dy = chunkY * height;
			for (int chunkX = 0; chunkX < 5; chunkX++) {
				int dx = chunkX * width;
				
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int danger = dangerField[y][x] + chunkX + chunkY;
						extendedField[y + dy][x + dx] = danger <= 9 ? danger : danger - 9;
					}
				}
			}
		}
		return solve(extendedField);
	}
	
	int solve(int[][] dangerField) {
		Point end = new Point(dangerField.length - 1, dangerField[0].length - 1);
		Map<Point, Integer> minTotalDanger = new HashMap<>(dangerField.length * dangerField[0].length * 2);
		
		Queue<Path> paths = new PriorityQueue<>(1000, Comparator.comparing(Path::danger));
		paths.add(new Path(Point.ZERO, 0));
		
		while (!paths.isEmpty()) {
			Path path = paths.poll();
			for (Direction d : Direction.VALUES) {
				Point nextPosition = d.shift(path.position);
				if (nextPosition.x() < 0 || nextPosition.y() < 0
						|| nextPosition.y() >= dangerField.length || nextPosition.x() >= dangerField[0].length)
					continue;
				int danger = dangerField[nextPosition.x()][nextPosition.y()];
				
				int nextDanger = path.danger + danger;
				Integer minDanger = minTotalDanger.get(nextPosition);
				if (minDanger != null && minDanger <= nextDanger)
					continue;
				
				minTotalDanger.put(nextPosition, nextDanger);
				paths.add(new Path(nextPosition, nextDanger));
			}
		}
		return minTotalDanger.getOrDefault(end, 0);
	}
	
	record Path(Point position, int danger) {
	}
}