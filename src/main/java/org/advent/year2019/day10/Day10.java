package org.advent.year2019.day10;

import org.advent.common.NumbersAdventUtils;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.SequencedSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Day10 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day10()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 8, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 33, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 35, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example4.txt", 41, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example5.txt", 210, 802),
				new ExpectedAnswers("input.txt", 288, 616)
		);
	}
	
	List<Point> asteroids;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		asteroids = Point.readField(Utils.readLines(input)).get('#');
	}
	
	@Override
	public Object part1() {
		return bestLocation().right();
	}
	
	@Override
	public Object part2() {
		Point laser = bestLocation().left();
		Comparator<Point> angleComparator = Comparator.comparing((Point p) ->
				(Math.atan2(p.y(), p.x()) + Math.PI * 2.5) % (Math.PI * 2));
		
		int targetsCount = 200;
		while (true) {
			Collection<Point> targetsRelative = visibleAsteroidsRelative(asteroids, laser);
			if (targetsRelative.size() < targetsCount) {
				targetsCount -= targetsRelative.size();
				continue;
			}
			Point target = targetsRelative.stream()
					.sorted(angleComparator)
					.skip(targetsCount - 1)
					.map(t -> t.shift(laser))
					.findFirst()
					.orElseThrow();
			return target.x() * 100 + target.y();
		}
	}
	
	private Pair<Point, Integer> bestLocation() {
		int maxVisible = 0;
		Point best = null;
		for (Point asteroid : asteroids) {
			int visible = visibleAsteroidsRelative(asteroids, asteroid).size();
			if (maxVisible < visible) {
				maxVisible = visible;
				best = asteroid;
			}
		}
		return Pair.of(best, maxVisible);
	}
	
	Set<Point> visibleAsteroidsRelative(Collection<Point> asteroids, Point start) {
		SequencedSet<Point> sortedAsteroids = asteroids.stream()
				.map(a -> a.subtract(start))
				.sorted(Comparator.comparing(Point.ZERO::distanceTo))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		sortedAsteroids.remove(Point.ZERO);
		Rect bounds = Point.bounds(sortedAsteroids);
		
		Set<Point> visibleAsteroids = new HashSet<>();
		
		while (!sortedAsteroids.isEmpty()) {
			Point asteroid = sortedAsteroids.removeFirst();
			visibleAsteroids.add(asteroid);
			
			Point directionVector = directionVector(asteroid);
			asteroid = asteroid.shift(directionVector);
			while (bounds.containsInclusive(asteroid)) {
				sortedAsteroids.remove(asteroid);
				asteroid = asteroid.shift(directionVector);
			}
		}
		return visibleAsteroids;
	}
	
	Point directionVector(Point point) {
		int x = point.x();
		int y = point.y();
		int gcd = NumbersAdventUtils.gcd(Math.abs(x), Math.abs(y)); // Находим НОД
		return new Point(x / gcd, y / gcd);
	}
}