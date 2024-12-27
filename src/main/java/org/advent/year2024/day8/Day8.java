package org.advent.year2024.day8;

import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Day8 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day8()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 14, 34),
				new ExpectedAnswers("input.txt", 254, 951)
		);
	}
	
	Map<Character, List<Point>> field;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		field = Point.readField(Utils.readLines(input));
	}
	
	@Override
	public Object part1() {
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
	
	@Override
	public Object part2() {
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