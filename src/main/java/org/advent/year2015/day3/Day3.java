package org.advent.year2015.day3;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Day3 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day3.class, "input.txt");
		String line = input.nextLine();
		
		System.out.println("Answer 1: " + part1(line));
		System.out.println("Answer 2: " + part2(line));
	}
	
	private static long part1(String line) {
		Point current = new Point(0, 0);
		Set<Point> visited = new HashSet<>(List.of(current));
		for (char direction : line.toCharArray()) {
			current = current.shift(Direction.parseSymbol(direction));
			visited.add(current);
		}
		return visited.size();
	}
	
	private static long part2(String line) {
		Point start = new Point(0, 0);
		Point[] current = new Point[] {start, start};
		Set<Point> visited = new HashSet<>(List.of(start));
		int turn = 0;
		for (char direction : line.toCharArray()) {
			current[turn] = current[turn].shift(Direction.parseSymbol(direction));
			visited.add(current[turn]);
			turn = (turn + 1) % 2;
		}
		return visited.size();
	}
}