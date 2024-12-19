package org.advent.year2024.day18;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day18 {
	static final Data example = new Data("example.txt", 6, 12);
	static final Data input = new Data("input.txt", 70, 1024);
	static final Data data = input;
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day18.class, data.file);
		List<Point> bytes = Utils.readLines(input).stream().map(Point::parse).toList();
		
		System.out.println("Answer 1: " + part1(bytes));
		System.out.println("Answer 2: " + part2(bytes));
	}
	
	private static long part1(List<Point> bytes) {
		return exitDistance(bytes, data.bytesFallen);
	}
	
	private static String part2(List<Point> bytes) {
		int min = 0;
		int max = bytes.size();
		while (min < max) {
			int mid = (min + max) / 2;
			int steps = exitDistance(bytes, mid);
			if (steps == 0)
				max = mid;
			else
				min = mid + 1;
		}
		return bytes.stream().skip(min - 1).findFirst().map(p -> p.x() + "," + p.y()).orElse("");
	}
	
	private static int exitDistance(List<Point> bytes, int bytesFallen) {
		Set<Point> corrupted = bytes.stream().limit(bytesFallen).collect(Collectors.toSet());
		Point start = new Point(0, 0);
		Point finish = new Point(data.fieldSize, data.fieldSize);
		Rect bounds = new Rect(start, finish);
		
		Set<Point> visited = new HashSet<>();
		Set<Point> current = Set.of(start);
		int steps = 0;
		while (!current.isEmpty()) {
			if (current.contains(finish))
				return steps;
			steps++;
			current = current.stream()
					.flatMap(p -> Direction.stream().map(p::shift))
					.filter(p -> !corrupted.contains(p))
					.filter(p -> !visited.contains(p))
					.filter(bounds::containsInclusive)
					.collect(Collectors.toSet());
			visited.addAll(current);
		}
		return 0;
	}
	
	record Data(String file, int fieldSize, int bytesFallen) {
	}
}