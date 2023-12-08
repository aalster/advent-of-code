package org.advent.year2021.day13;

import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day13 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day13.class, "input.txt");
		Set<Point> points = new HashSet<>();
		List<Pair<String, Integer>> folds = new ArrayList<>();
		
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty())
				break;
			String[] split = line.split(",");
			points.add(new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
		}
		
		while (input.hasNext()) {
			String[] split = StringUtils.removeStart(input.nextLine(), "fold along ").split("=");
			folds.add(Pair.of(split[0], Integer.parseInt(split[1])));
		}
		
		System.out.println("Answer 1: " + part1(points, folds));
		System.out.println("Answer 2: " + part2(points, folds));
	}
	
	private static long part1(Set<Point> points, List<Pair<String, Integer>> folds) {
		return solve(points, folds.subList(0, 1)).size();
	}
	
	private static String part2(Set<Point> points, List<Pair<String, Integer>> folds) {
		return format(solve(points, folds));
	}
	
	private static Set<Point> solve(Set<Point> points, List<Pair<String, Integer>> folds) {
		for (Pair<String, Integer> fold : folds)
			points = fold(points, fold.left(), fold.right());
		return points;
	}
	
	private static Set<Point> fold(Set<Point> points, String axis, int position) {
		if ("y".equals(axis)) {
			return points.stream().map(p -> p.y() < position ? p : p.shift(0, - 2 * (p.y() - position))).collect(Collectors.toSet());
		} else {
			return points.stream().map(p -> p.x() < position ? p : p.shift(- 2 * (p.x() - position), 0)).collect(Collectors.toSet());
		}
	}
	
	private static String format(Set<Point> points) {
		int width = points.stream().mapToInt(Point::x).max().orElse(0) + 1;
		int height = points.stream().mapToInt(Point::y).max().orElse(0) + 1;
		StringBuilder result = new StringBuilder("\n");
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				result.append(points.contains(new Point(x, y)) ? '#' : '.');
			}
			result.append("\n");
		}
		return result.toString();
	}
}