package org.example.puzzle9;

import org.apache.commons.lang3.math.NumberUtils;
import org.example.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Puzzle9 {
	
	public static void main(String[] args) {
		System.out.println("Example 1: " + solve(Utils.scanFileNearClass(Puzzle9.class, "example.txt"), 2));
		System.out.println("Answer 1: " + solve(Utils.scanFileNearClass(Puzzle9.class, "input.txt"), 2));
		System.out.println("Example 1: " + solve(Utils.scanFileNearClass(Puzzle9.class, "example.txt"), 10));
		System.out.println("Example 2: " + solve(Utils.scanFileNearClass(Puzzle9.class, "example2.txt"), 10));
		System.out.println("Answer 2: " + solve(Utils.scanFileNearClass(Puzzle9.class, "input.txt"), 10));
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
	
	enum Direction {
		R, L, U, D
	}
	
	record Point(int x, int y) {
		Point move(Direction d) {
			return switch (d) {
				case L -> new Point(x - 1, y);
				case R -> new Point(x + 1, y);
				case U -> new Point(x, y - 1);
				case D -> new Point(x, y + 1);
			};
		}
		
		Point moveTo(Point p) {
			if (near(p))
				return this;
			return new Point(x + Integer.compare(p.x, x), y + Integer.compare(p.y, y));
		}
		
		boolean near(Point p) {
			return Math.abs(p.x - x) <= 1 && Math.abs(p.y - y) <= 1;
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
				knots[i] = knots[i].moveTo(knots[i - 1]);
		}
		
		Point tail() {
			return knots[knots.length - 1];
		}
	}
}