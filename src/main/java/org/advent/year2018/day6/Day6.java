package org.advent.year2018.day6;

import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Day6 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day6()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 17, 16),
				new ExpectedAnswers("input.txt", 4166, 42250)
		);
	}
	
	Point[] points;
	int totalDistanceLimit;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		points = Utils.readLines(input).stream().map(line -> Point.parse(line, ", ")).toArray(Point[]::new);
		totalDistanceLimit = switch (file) {
			case "example.txt" -> 32;
			case "input.txt" -> 10000;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		int[] counts = new int[points.length];
		Set<Integer> excludeIndexes = new HashSet<>();
		Rect bounds = Point.bounds(List.of(points));
		for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
			for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
				Point point = new Point(x, y);
				int[] distances = new int[points.length];
				for (int i = 0; i < points.length; i++)
					distances[i] = point.distanceTo(points[i]);
				
				int min = Arrays.stream(distances).min().orElseThrow();
				int index = ArrayUtils.indexOf(distances, min);
				
				if (ArrayUtils.indexOf(distances, min, index + 1) < 0)
					counts[index]++;
				if (x == bounds.minX() || x == bounds.maxX() || y == bounds.minY() || y == bounds.maxY())
					excludeIndexes.add(index);
			}
		}
		int max = 0;
		for (int i = 0; i < counts.length; i++)
			if (!excludeIndexes.contains(i) && counts[i] > max)
				max = counts[i];
		return max;
	}
	
	@Override
	public Object part2() {
		int count = 0;
		Rect bounds = Point.bounds(List.of(points));
		for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
			for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
				Point point = new Point(x, y);
				int totalDistance = 0;
				for (int i = 0; i < points.length && totalDistance < totalDistanceLimit; i++)
					totalDistance += point.distanceTo(points[i]);
				
				if (totalDistance < totalDistanceLimit)
					count++;
			}
		}
		return count;
	}
}