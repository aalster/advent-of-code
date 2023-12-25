package org.advent.year2023.day13;

import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day13 {
	private static final boolean print = false;
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day13.class, "input.txt");
		List<Set<Point>> fields = new ArrayList<>();
		Set<Point> field = new HashSet<>();
		int y = 0;
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty()) {
				fields.add(field);
				field = new HashSet<>();
				y = 0;
				continue;
			}
			for (int x = 0; x < line.length(); x++)
				if (line.charAt(x) == '#')
					field.add(new Point(x, y));
			y++;
		}
		if (!field.isEmpty())
			fields.add(field);
		
		System.out.println("Answer 1: " + part1(fields));
		System.out.println("Answer 2: " + part2(fields));
	}
	
	private static long part1(List<Set<Point>> fields) {
		return solve(fields, 0);
	}
	
	private static long part2(List<Set<Point>> fields) {
		return solve(fields, 1);
	}
	
	private static long solve(List<Set<Point>> fields, int differences) {
		long result = 0;
		for (Set<Point> field : fields) {
			result += verticalColumnsSum(field, differences) + 100 * horizontalRowsSum(field, differences);
		}
		return result;
	}
	
	private static long verticalColumnsSum(Set<Point> field, int differences) {
		int maxX = Point.maxX(field);
		int result = 0;
		
		printField(field, "Original:");
		
		for (int x = 1; x <= maxX; x++) {
			int _x = x;
			int filterTo = x * 2;
			int filterFrom = filterTo - maxX - 1;
			
			Set<Point> subField = field.stream()
					.filter(p -> filterFrom <= p.x() && p.x() < filterTo)
					.collect(Collectors.toSet());
			
			Set<Point> mirrored = subField.stream()
					.map(p -> p.x() >= _x ? new Point(_x * 2 - p.x() - 1, p.y()) : p)
					.collect(Collectors.toSet());
			
			printField(subField, "Subfield at x = " + x + ":");
			printField(mirrored, "Reflection at x = " + x + ":");
			
			if (Math.abs(subField.size() - mirrored.size() * 2) == differences)
				result += x;
		}
		return result;
	}
	
	private static long horizontalRowsSum(Set<Point> field, int differences) {
		return verticalColumnsSum(field.stream().map(p -> new Point(p.y(), p.x())).collect(Collectors.toSet()), differences);
	}
	
	private static void printField(Set<Point> field, String caption) {
		if (!print)
			return;
		System.out.println(caption);
		Point.printField(field, '#', '.');
		System.out.println();
	}
}