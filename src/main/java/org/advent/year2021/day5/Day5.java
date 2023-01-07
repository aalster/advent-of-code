package org.advent.year2021.day5;

import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Day5 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day5.class, "input.txt");
		List<Pair<Point, Point>> points = new ArrayList<>();
		while (input.hasNext()) {
			String[] split = input.nextLine().split(" -> ");
			points.add(Pair.of(Point.parse(split[0]), Point.parse(split[1])));
		}
		
		System.out.println("Answer 1: " + part1(points));
		System.out.println("Answer 2: " + part2(points));
	}
	
	private static long part1(List<Pair<Point, Point>> points) {
		List<Set<Point>> lines = points.stream()
				.filter(pair -> pair.left().x() == pair.right().x() || pair.left().y() == pair.right().y())
				.map(pair -> line(pair.left(), pair.right()))
				.toList();
		return countOverlappedPoints(lines, 2);
	}
	
	private static long part2(List<Pair<Point, Point>> points) {
		List<Set<Point>> lines = points.stream()
				.map(pair -> line(pair.left(), pair.right()))
				.toList();
		return countOverlappedPoints(lines, 2);
	}
	
	private static long countOverlappedPoints(List<Set<Point>> lines, int overlaps) {
		return lines.stream()
				.flatMap(Collection::stream)
				.distinct()
				.filter(p -> lines.stream().filter(line -> line.contains(p)).count() >= overlaps)
				.count();
	}
	
	public static Set<Point> line(Point from, Point to) {
		Point vector = from.directionVectorTo(to);
		Set<Point> points = new HashSet<>();
		Point p = from;
		points.add(p);
		while (!p.equals(to)) {
			p = p.shift(vector);
			points.add(p);
		}
		return points;
	}
}