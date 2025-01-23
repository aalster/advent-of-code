package org.advent.year2018.day17;

import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

public class Day17 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day17()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 57, 29),
				new ExpectedAnswers("input.txt", 32439, 26729)
		);
	}
	
	Clay clay;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		clay = Clay.parse(Utils.readLines(input));
	}
	
	@Override
	public Object part1() {
		Pair<Set<Point>, Set<Point>> water = solve();
		return water.left().size() + water.right().size();
	}
	
	@Override
	public Object part2() {
		return solve().left().size();
	}
	
	Pair<Set<Point>, Set<Point>> solve() {
		Range yRange = clay.yRange();
		int maxY = yRange.to;
		Point waterStart = new Point(500, yRange.from);
		Set<Point> settled = new HashSet<>();
		Set<Point> flowing = new HashSet<>();
		solveAndReturnSettled(waterStart, maxY, settled, flowing);
		return Pair.of(settled, flowing);
	}
	
	boolean solveAndReturnSettled(Point start, int maxY, Set<Point> settled, Set<Point> flowing) {
		Point current = start;
		while (true) {
			flowing.add(current);
			Point next = current.shift(Direction.DOWN);
			if (next.y() > maxY || flowing.contains(next))
				return false;
			if (clay.contains(next) || settled.contains(next))
				break;
			current = next;
		}
		
		while (current.y() >= start.y()) {
			List<Point> horizontal = new ArrayList<>();
			horizontal.add(current);
			
			boolean leftSettled = spreadHorizontally(current, Direction.LEFT, settled, flowing, maxY, horizontal);
			boolean rightSettled = spreadHorizontally(current, Direction.RIGHT, settled, flowing, maxY, horizontal);
			if (leftSettled && rightSettled) {
				settled.addAll(horizontal);
				flowing.remove(current);
				current = current.shift(Direction.UP);
				continue;
			}
			flowing.addAll(horizontal);
			return false;
		}
		return true;
	}
	
	boolean spreadHorizontally(Point current, Direction direction, Set<Point> settled, Set<Point> flowing, int maxY, List<Point> horizontal) {
		Point shifted = current.shift(direction);
		while (!clay.contains(shifted)) {
			horizontal.add(shifted);
			Point bottom = shifted.shift(Direction.DOWN);
			if (!clay.contains(bottom) && !settled.contains(bottom))
				if (!solveAndReturnSettled(bottom, maxY, settled, flowing))
					return false;
			shifted = shifted.shift(direction);
		}
		return true;
	}
	
	record Clay(Map<Integer, List<Range>> vertical, Map<Integer, List<Range>> horizontal) {
		
		boolean contains(Point p) {
			return vertical.getOrDefault(p.x(), List.of()).stream().anyMatch(r -> r.contains(p.y()))
					|| horizontal.getOrDefault(p.y(), List.of()).stream().anyMatch(r -> r.contains(p.x()));
		}
		
		Range yRange() {
			return Stream.concat(
							horizontal.keySet().stream().map(y -> new Range(y, y)),
							vertical.values().stream().flatMap(List::stream))
					.reduce(Range::outerRange)
					.orElseThrow();
		}
		
		static Clay parse(List<String> lines) {
			Map<Integer, List<Range>> vertical = new HashMap<>();
			Map<Integer, List<Range>> horizontal = new HashMap<>();
			for (String line : lines) {
				String[] split = line.replace(", ", "=").split("=");
				("x".equals(split[0]) ? vertical : horizontal)
						.computeIfAbsent(Integer.parseInt(split[1]), k -> new ArrayList<>()).add(Range.parse(split[3]));
			}
			return new Clay(vertical, horizontal);
		}
	}
	
	record Range(int from, int to) {
		
		boolean contains(int value) {
			return from <= value && value <= to;
		}
		
		Range outerRange(Range other) {
			return new Range(Math.min(from, other.from), Math.max(to, other.to));
		}
		
		static Range parse(String line) {
			String[] parts = line.split("\\.\\.");
			return new Range(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
		}
	}
}