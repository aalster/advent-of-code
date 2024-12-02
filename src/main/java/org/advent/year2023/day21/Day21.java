package org.advent.year2023.day21;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day21 {
	record TestCase(String file, int steps1, int steps2) {}
	private static final TestCase example = new TestCase("example.txt", 6, 5000);
	private static final TestCase input = new TestCase("input.txt", 64, 26501365);
	private static final TestCase test = example;
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day21.class, test.file());
		Map<Character, List<Point>> allPoints = Point.readField(Utils.readLines(input));
		List<Point> field = allPoints.get('#');
		Point start = allPoints.get('S').getFirst();
		
		System.out.println("Answer 1: " + part1(field, start, test.steps1()));
		System.out.println("Answer 2: " + part2());
	}
	
	private static long part1(List<Point> rocks, Point start, int steps) {
		Rect bounds = new Rect(Point.minBound(rocks).shift(-1, -1), Point.maxBound(rocks).shift(1, 1));
		List<Point> visited = new ArrayList<>();
		Set<Point> current = Set.of(start);
		for (int i = 0; i < steps; i++) {
			current = current.stream()
					.flatMap(p -> Direction.stream().map(p::move))
					.filter(bounds::containsInclusive)
					.filter(p -> !rocks.contains(p))
					.filter(p -> !visited.contains(p))
					.collect(Collectors.toSet());
			if (current.isEmpty())
				break;
			visited.addAll(current);
		}
		int remainder = (steps + start.x() + start.y()) % 2;
		return visited.stream().filter(p -> (p.x() + p.y()) % 2 == remainder).count();
	}
	
	private static long part2() {
		return 0;
	}
}