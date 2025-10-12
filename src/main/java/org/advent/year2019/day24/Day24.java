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
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.IntStream;

public class Day24 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day24()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 2129920, 99),
				new ExpectedAnswers("input.txt", 32526865, 2009)
		);
	}
	
	final int size = 5;
	final Rect bounds = new Rect(Point.ZERO, new Point(size - 1, size - 1));
	final Point middle = new Point(size / 2, size / 2);
	final Map<Point, List<Point>> innerChecks = Map.of(
			Direction.RIGHT.getP(), IntStream.range(0, size).mapToObj(y -> new Point(0, y)).toList(),
			Direction.LEFT.getP(), IntStream.range(0, size).mapToObj(y -> new Point(size - 1, y)).toList(),
			Direction.DOWN.getP(), IntStream.range(0, size).mapToObj(x -> new Point(x, 0)).toList(),
			Direction.UP.getP(), IntStream.range(0, size).mapToObj(x -> new Point(x, size - 1)).toList()
	);
	
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
		int minLevel = 0;
		int maxLevel = 0;
		
		for (int minute = 0; minute < minutes; minute++) {
			Map<Integer, Set<Point>> nextLevels = new HashMap<>();
			
			for (Map.Entry<Integer, Set<Point>> entry : currentLevels.entrySet()) {
				int level = entry.getKey();
				Set<Point> outer = currentLevels.getOrDefault(level - 1, Set.of());
				Set<Point> inner = currentLevels.getOrDefault(level + 1, Set.of());
				nextLevels.put(level, nextLevelRecursive(entry.getValue(), outer, inner));
			}
			
			Set<Point> nextInner = nextLevelRecursive(Set.of(), currentLevels.get(maxLevel), Set.of());
			if (!nextInner.isEmpty())
				nextLevels.put(++maxLevel, nextInner);
			
			Set<Point> nextOuter = nextLevelRecursive(Set.of(), Set.of(), currentLevels.get(minLevel));
			if (!nextOuter.isEmpty())
				nextLevels.put(--minLevel, nextOuter);
			
			currentLevels = nextLevels;
		}
		return currentLevels.values().stream().mapToInt(Set::size).sum();
	}
	
	Set<Point> nextLevelRecursive(Set<Point> current, Set<Point> outer, Set<Point> inner) {
		Set<Point> next = new HashSet<>();
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				Point point = new Point(x, y);
				if (point.equals(middle))
					continue;
				long nearBugs = Direction.stream()
						.map(point::shift)
						.mapToInt(p -> {
							if (!bounds.containsInclusive(p))
								return outer.contains(p.subtract(point).shift(middle)) ? 1 : 0;
							if (p.equals(middle))
								return (int) innerChecks.get(p.subtract(point)).stream().filter(inner::contains).count();
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