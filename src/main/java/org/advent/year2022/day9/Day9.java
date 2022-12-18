package org.advent.year2022.day9;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class Day9 {
	
	public static void main(String[] args) {
		System.out.println("Example 1: " + solve(Utils.scanFileNearClass(Day9.class, "example.txt"), 2));
		System.out.println("Answer 1: " + solve(Utils.scanFileNearClass(Day9.class, "input.txt"), 2));
		System.out.println("Example 1: " + solve(Utils.scanFileNearClass(Day9.class, "example.txt"), 10));
		System.out.println("Example 2: " + solve(Utils.scanFileNearClass(Day9.class, "example2.txt"), 10));
		System.out.println("Answer 2: " + solve(Utils.scanFileNearClass(Day9.class, "input.txt"), 10));
	}
	
	static int solve(Scanner input, int ropeLength) {
		Set<Point> visited = new LinkedHashSet<>();
		Rope rope = new Rope(ropeLength);
		while (input.hasNext()) {
			String[] split = input.nextLine().split(" ");
			Direction direction = Direction.valueOf(split[0]);
			int count = Integer.parseInt(split[1]);
			while (count > 0) {
				count--;
				rope.move(direction);
				visited.add(rope.tail());
			}
		}
//		printField(visited);
		return visited.size();
	}
	
	static void printField(Set<Point> visited) {
		IntSummaryStatistics xStats = visited.stream().mapToInt(Point::x).summaryStatistics();
		IntSummaryStatistics yStats = visited.stream().mapToInt(Point::y).summaryStatistics();
		for (int y = yStats.getMin(); y <= yStats.getMax(); y++) {
			for (int x = xStats.getMin(); x <= xStats.getMax(); x++) {
				System.out.print(x == 0 && y == 0 ? 's' : visited.contains(new Point(x, y)) ? '#' : '.');
			}
			System.out.println();
		}
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