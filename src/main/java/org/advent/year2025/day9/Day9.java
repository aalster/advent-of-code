package org.advent.year2025.day9;

import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Day9 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day9()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 50, 24),
				new ExpectedAnswers("input.txt", 4786902990L, 1571016172)
		);
	}
	
	Point[] points;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		points = Utils.readLines(input).stream().map(Point::parse).toArray(Point[]::new);
	}
	
	@Override
	public Object part1() {
		long maxArea = 0;
		for (int l = 0; l < points.length; l++) {
			Point left = points[l];
			for (int r = l + 1; r < points.length; r++) {
				Point right = points[r];
				maxArea = Math.max(maxArea, area(left, right));
			}
		}
		return maxArea;
	}
	
	@Override
	public Object part2() {
		List<SizedRect> edges = new ArrayList<>();
		Point prev = points[points.length - 1];
		for (Point current : points) {
			edges.add(SizedRect.of(prev, current));
			prev = current;
		}
		edges.sort(Comparator.comparing(SizedRect::size).reversed());
		
		List<SizedRect> sizes = new ArrayList<>();
		for (int l = 0; l < points.length; l++) {
			Point left = points[l];
			for (int r = l + 1; r < points.length; r++)
				sizes.add(SizedRect.of(left, points[r]));
		}
		
		return sizes.stream()
				.sorted(Comparator.comparing(SizedRect::size).reversed())
				.filter(r -> edges.stream().noneMatch(r::intersects))
				.findFirst()
				.map(SizedRect::size)
				.orElse(null);
	}
	
	static long area(Point left, Point right) {
		return (Math.abs(left.x() - right.x()) + 1L) * (Math.abs(left.y() - right.y()) + 1);
	}
	
	record SizedRect(Rect rect, long size) {
		
		boolean intersects(SizedRect other) {
			return rect.intersectsExclusive(other.rect);
		}
		
		static SizedRect of(Point a, Point b) {
			return new SizedRect(Rect.ofCorners(a, b), area(a, b));
		}
	}
}