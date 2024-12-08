package org.advent.year2024.day8;

import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Day8 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day8.class, "input.txt");
		Map<Character, List<Point>> field = Point.readField(Utils.readLines(input));
		
		System.out.println("Answer 1: " + part1(field));
		System.out.println("Answer 2: " + part2(field));
	}
	
	private static long part1(Map<Character, List<Point>> field) {
		Rect bounds = Point.bounds(field.get('.'));
		Set<Point> antinodes = new HashSet<>();
		for (Map.Entry<Character, List<Point>> entry : field.entrySet()) {
			if (entry.getKey() == '.')
				continue;
			for (Point left : entry.getValue()) {
				for (Point right : entry.getValue()) {
					if (left == right)
						continue;
					Point diff = right.subtract(left);
					antinodes.add(right.shift(diff));
					antinodes.add(left.subtract(diff));
				}
			}
		}
		return antinodes.stream().filter(bounds::containsInclusive).count();
	}
	
	private static long part2(Map<Character, List<Point>> field) {
		Rect bounds = Point.bounds(field.get('.'));
		Set<Point> antinodes = new HashSet<>();
		for (Map.Entry<Character, List<Point>> entry : field.entrySet()) {
			if (entry.getKey() == '.')
				continue;
			for (Point left : entry.getValue()) {
				for (Point right : entry.getValue()) {
					if (left == right)
						continue;
					Point diff = right.subtract(left);
					antinodes.add(left);
					Point next = left.shift(diff);
					while (bounds.containsInclusive(next)) {
						antinodes.add(next);
						next = next.shift(diff);
					}
					next = left.subtract(diff);
					while (bounds.containsInclusive(next)) {
						antinodes.add(next);
						next = next.subtract(diff);
					}
				}
			}
		}
		return antinodes.size();
	}
}