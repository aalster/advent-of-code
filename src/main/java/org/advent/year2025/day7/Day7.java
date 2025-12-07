package org.advent.year2025.day7;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SequencedSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Day7 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day7()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 21, 40),
				new ExpectedAnswers("input.txt", 1585, 16716444407407L)
		);
	}
	
	Point start;
	Set<Point> splitters;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		Map<Character, List<Point>> field = Point.readField(Utils.readLines(input));
		start = field.get('S').getFirst();
		splitters = new HashSet<>(field.get('^'));
	}
	
	@Override
	public Object part1() {
		Set<Point> beam = Set.of(start);
		int maxY = Point.maxY(splitters);
		int splits = 0;
		main: while (true) {
			Set<Point> nextBeam = new HashSet<>();
			for (Point p : beam) {
				if (p.y() > maxY)
					break main;
				
				p = p.shift(Direction.DOWN);
				if (splitters.contains(p)) {
					nextBeam.add(p.shift(Direction.LEFT));
					nextBeam.add(p.shift(Direction.RIGHT));
					splits++;
				} else {
					nextBeam.add(p);
				}
			}
			beam = nextBeam;
		}
		return splits;
	}
	
	@Override
	public Object part2() {
		SequencedSet<Point> sortedSplitters = splitters.stream()
				.sorted(Comparator.comparing(Point::y))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		return countPaths(sortedSplitters, start, new HashMap<>());
	}
	
	long countPaths(SequencedSet<Point> splitters, Point current, Map<Point, Long> cache) {
		Long cached = cache.get(current);
		if (cached == null) {
			Point splitter = splitters.stream()
					.filter(s -> current.x() == s.x() && current.y() < s.y())
					.findFirst()
					.orElse(null);
			if (splitter == null)
				cached = 1L;
			else
				cached = countPaths(splitters, splitter.shift(Direction.LEFT), cache)
						+ countPaths(splitters, splitter.shift(Direction.RIGHT), cache);
			cache.put(current, cached);
		}
		return cached;
	}
}