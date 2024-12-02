package org.advent.year2022.day9;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Day9 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day9.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		System.out.println("Answer 1: " + solve(lines, 2));
		System.out.println("Answer 2: " + solve(lines, 10));
	}
	
	static int solve(List<String> lines, int ropeLength) {
		Set<Point> visited = new LinkedHashSet<>();
		Rope rope = new Rope(ropeLength);
		for (String line : lines) {
			String[] split = line.split(" ");
			Direction direction = Direction.parseLetter(split[0].charAt(0));
			int count = Integer.parseInt(split[1]);
			while (count > 0) {
				count--;
				rope.move(direction);
				visited.add(rope.tail());
			}
		}
		return visited.size();
	}
	
	static class Rope {
		private final Point[] knots;
		
		Rope(int length) {
			knots = new Point[length];
			Arrays.fill(knots, new Point(0, 0));
		}
		
		void move(Direction d) {
			knots[0] = knots[0].move(d);
			for (int i = 1; i < knots.length; i++)
				knots[i] = moveTo(knots[i], knots[i - 1]);
		}
		
		Point tail() {
			return knots[knots.length - 1];
		}
	}
	
	static Point moveTo(Point p, Point target) {
		if (Math.abs(target.x() - p.x()) <= 1 && Math.abs(target.y() - p.y()) <= 1)
			return p;
		return new Point(p.x() + Integer.compare(target.x(), p.x()), p.y() + Integer.compare(target.y(), p.y()));
	}
}