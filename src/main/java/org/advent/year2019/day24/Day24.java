package org.advent.year2019.day24;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.IntStream;

public class Day24 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day24()).runAll();
//		new DayRunner(new Day24()).run("example.txt", 2);
//		new DayRunner(new Day24()).run("input.txt", 2);
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 2129920, 99),
				new ExpectedAnswers("input.txt", 32526865, 2009)
		);
	}
	
	int size = 5;
	Set<Point> bugs;
	int minutes;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		bugs = new HashSet<>(Point.readField(Utils.readLines(input)).get('#'));
		minutes = switch (file) {
			case "example.txt" -> 10;
			case "input.txt" -> 200;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		Set<Set<Point>> states = new HashSet<>();
		Set<Point> current = bugs;
		
		while (states.add(current)) {
			Set<Point> next = new HashSet<>();
			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++) {
					Point point = new Point(x, y);
					long nearBugs = Direction.stream().map(point::shift).filter(current::contains).count();
					if (nearBugs == 1 || (nearBugs == 2 && !current.contains(point)))
						next.add(point);
				}
			}
			current = next;
		}
		return current.stream().mapToLong(p -> 1L << (p.y() * 5 + p.x())).sum();
	}
	
	@Override
	public Object part2() {
		Map<Integer, Set<Point>> currentLevels = new HashMap<>(Map.of(0, bugs));
		Rect bounds = Point.bounds(bugs);
		Point middle = new Point(size / 2, size / 2);
		Map<Point, List<Point>> innerChecks = Map.of(
				Direction.RIGHT.getP(), IntStream.range(0, size).mapToObj(y -> new Point(0, y)).toList(),
				Direction.LEFT.getP(), IntStream.range(0, size).mapToObj(y -> new Point(size - 1, y)).toList(),
				Direction.DOWN.getP(), IntStream.range(0, size).mapToObj(x -> new Point(x, 0)).toList(),
				Direction.UP.getP(), IntStream.range(0, size).mapToObj(x -> new Point(x, size - 1)).toList()
		);
		
		for (int minute = 0; minute < minutes; minute++) {
			Map<Integer, Set<Point>> nextLevels = new HashMap<>();
			
			for (Map.Entry<Integer, Set<Point>> entry : currentLevels.entrySet()) {
				int level = entry.getKey();
				Set<Point> current = entry.getValue();
				
				Set<Point> outer = currentLevels.getOrDefault(level - 1, Set.of());
				Set<Point> inner = currentLevels.getOrDefault(level + 1, Set.of());
				
				Set<Point> next = nextLevelRecursive(current, outer, inner, bounds, middle, innerChecks);
				nextLevels.put(level, next);
			}
			
			IntSummaryStatistics stats = currentLevels.keySet().stream().mapToInt(Integer::intValue).summaryStatistics();
			
			Set<Point> nextInner = nextLevelRecursive(Set.of(), currentLevels.get(stats.getMax()), Set.of(), bounds, middle, innerChecks);
			if (!nextInner.isEmpty())
				nextLevels.put(stats.getMax() + 1, nextInner);
			
			Set<Point> nextOuter = nextLevelRecursive(Set.of(), Set.of(), currentLevels.get(stats.getMin()), bounds, middle, innerChecks);
			if (!nextOuter.isEmpty())
				nextLevels.put(stats.getMin() - 1, nextOuter);
			
			currentLevels = nextLevels;
		}
		
//		for (int level = -minutes; level < minutes; level++) {
//			if (!currentLevels.containsKey(level))
//				continue;
//			System.out.println("Level " + level);
//			Point.printField(currentLevels.get(level), '#', '.');
//			System.out.println();
//		}
		
		return currentLevels.values().stream().mapToInt(Set::size).sum();
	}
	
	Set<Point> nextLevelRecursive(Set<Point> current, Set<Point> outer, Set<Point> inner, Rect bounds, Point middle, Map<Point, List<Point>> innerChecks) {
		Set<Point> next = new HashSet<>();
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				Point point = new Point(x, y);
				if (point.equals(middle))
					continue;
				long nearBugs = Direction.stream()
						.map(point::shift)
						.mapToInt(p -> {
							if (!bounds.containsInclusive(p)) {
								return outer.contains(p.subtract(point).shift(middle)) ? 1 : 0;
							}
							if (p.equals(middle)) {
								return (int) innerChecks.get(p.subtract(point)).stream().filter(inner::contains).count();
							}
							return current.contains(p) ? 1 : 0;
						})
						.sum();
				if (nearBugs == 1 || (nearBugs == 2 && !current.contains(point)))
					next.add(point);
			}
		}
		return next;
	}
}