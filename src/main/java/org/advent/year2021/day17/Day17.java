package org.advent.year2021.day17;

import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.Scanner;

public class Day17 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day17.class, "example.txt");
		Area area = Area.parse(input.nextLine().split(": ")[1]);
		
		System.out.println("Answer 1: " + part1(area));
		System.out.println("Answer 2: " + part2());
	}
	
	private static long part1(Area area) {
		int totalMaxY = Integer.MIN_VALUE;
		for (int initialXV = 1; initialXV < area.x2() + 1; initialXV++) {
			int steps = stepsForX(initialXV, area.x1(), area.x2());
			if (steps <= 0)
				continue;
			
			int maxY = maxY(steps, area.y1(), area.y2());
			if (maxY == Integer.MIN_VALUE)
				continue;
			
			if (totalMaxY < maxY)
				totalMaxY = maxY;
		}
		return totalMaxY;
	}
	
	private static long part2() {
		return 0;
	}
	
	private static int stepsForX(int initialXVelocity, int destFrom, int destTo) {
		int v = initialXVelocity;
		int x = 0;
		int step = 1;
		while (v > 0) {
			x += v;
			if (destFrom <= x && x <= destTo)
				return step;
			step++;
			v--;
		}
		return -1;
	}
	
	private static int maxY(int steps, int destFrom, int destTo) {
		int maxY = Integer.MIN_VALUE;
		for (int initialYVelocity = -1000; initialYVelocity < 1000; initialYVelocity++) {
			int v = initialYVelocity;
			int y = 0;
			int step = 1;
			while (step <= steps) {
				y += v;
				if (destFrom <= y && y <= destTo) {
				
				}
				v++;
				step++;
			}
		}
		return maxY;
	}
	
	private record Area(int x1, int x2, int y1, int y2) {
		boolean contains(Point point) {
			return x1 <= point.x() && point.x() <= x2 && y1 <= point.y() && point.y() <= y2;
		}
		
		static Area parse(String value) {
			String[] split = value.split(", ");
			String[] x = split[0].split("=")[1].split("\\.\\.");
			String[] y = split[1].split("=")[1].split("\\.\\.");
			return new Area(Integer.parseInt(x[0]), Integer.parseInt(x[1]), Integer.parseInt(y[0]), Integer.parseInt(y[1]));
		}
	}
}