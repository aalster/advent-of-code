package org.advent.year2020.day3;

import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Day3 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day3.class, "input.txt");
		Set<Point> points = new HashSet<>();
		int y = 0;
		while (input.hasNext()) {
			char[] charArray = input.nextLine().toCharArray();
			for (int x = 0; x < charArray.length; x++) {
				if (charArray[x] == '#')
					points.add(new Point(x, y));
			}
			y++;
		}
		
		System.out.println("Answer 1: " + part1(points));
		System.out.println("Answer 2: " + part2(points));
	}
	
	private static long part1(Set<Point> points) {
		return solveForDelta(points, new Point(3, 1));
	}
	
	private static long part2(Set<Point> points) {
		return solveForDelta(points, new Point(1, 1))
				* solveForDelta(points, new Point(3, 1))
				* solveForDelta(points, new Point(5, 1))
				* solveForDelta(points, new Point(7, 1))
				* solveForDelta(points, new Point(1, 2));
	}
	
	private static long solveForDelta(Set<Point> points, Point delta) {
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