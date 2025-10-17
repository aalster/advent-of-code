package org.advent.year2021.day15;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

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
		dangerField = Utils.readLines(input).stream()
				.map(line -> line.chars().map(c -> c - '0').toArray())
				.toArray(int[][]::new);
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
		Rect bounds = new Rect(Point.ZERO, end);
		int[][] minTotalDanger = new int[dangerField.length][dangerField[0].length];
		
		Queue<Path> paths = new PriorityQueue<>(1000, Comparator.comparing(Path::danger));
		paths.add(new Path(Point.ZERO, 0));
		
		while (!paths.isEmpty()) {
			Path path = paths.poll();
			for (Direction d : Direction.VALUES) {
				Point nextPosition = d.shift(path.position);
				if (!bounds.containsInclusive(nextPosition))
					continue;
				
				int nextDanger = path.danger + dangerField[nextPosition.x()][nextPosition.y()];
				int minDanger = minTotalDanger[nextPosition.y()][nextPosition.x()];
				if (minDanger > 0 && minDanger <= nextDanger)
					continue;
				
				minTotalDanger[nextPosition.y()][nextPosition.x()] = nextDanger;
				paths.add(new Path(nextPosition, nextDanger));
			}
		}
		return minTotalDanger[end.y()][end.x()];
	}
	
	record Path(Point position, int danger) {
	}
}