package org.advent.year2020.day3;

import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Day3 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day3()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 7, 336),
				new ExpectedAnswers("input.txt", 286, 3638606400L)
		);
	}
	
	Set<Point> points;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		points = new HashSet<>(Point.readField(Utils.readLines(input)).get('#'));
	}
	
	@Override
	public Object part1() {
		return solveForDelta(points, new Point(3, 1));
	}
	
	@Override
	public Object part2() {
		return solveForDelta(points, new Point(1, 1))
				* solveForDelta(points, new Point(3, 1))
				* solveForDelta(points, new Point(5, 1))
				* solveForDelta(points, new Point(7, 1))
				* solveForDelta(points, new Point(1, 2));
	}
	
	long solveForDelta(Set<Point> points, Point delta) {
		int maxX = Point.maxX(points);
		int maxY = Point.maxY(points);
		
		Point position = new Point(0, 0);
		
		int count = 0;
		while (true) {
			position = position.shift(delta);
			if (position.y() > maxY)
				break;
			if (position.x() > maxX)
				position = position.shift(-maxX - 1, 0);
			if (points.contains(position))
				count++;
		}
		return count;
	}
}