package org.advent.year2023.day23;

import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day23 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day23.class, "input.txt");
		Field field = Field.parseField(Utils.readLines(input));
		
		System.out.println("Answer 1: " + part1(field));
		System.out.println("Answer 2: " + part2(field));
	}
	
	private static long part1(Field field) {
		Set<Point> forks = new HashSet<>(field.findForks());
		forks.addAll(List.of(field.start(), field.end()));
		
		Map<Point, List<Trail>> trailsMap = forks.stream()
				.flatMap(fork -> field.findTrails(forks, fork).stream())
				.collect(Collectors.groupingBy(Trail::start));
		
		return findMaxPathRecursive(trailsMap, field.start(), field.end(), Set.of());
	}
	
	private static long part2(Field field) {
		Set<Character> slopeChars = Set.of('^', '>', 'v', '<');
		Map<Point, Character> points = field.points().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey,
						e -> slopeChars.contains(e.getValue()) ? '.' : e.getValue()));
		return part1(new Field(points, field.start(), field.end()));
	}
	
	private static int findMaxPathRecursive(Map<Point, List<Trail>> trailsMap, Point current, Point end, Set<Point> visited) {
		if (current.equals(end))
			return 0;
		
		Set<Point> nextVisited = new HashSet<>(visited);
		nextVisited.add(current);
		
		return trailsMap.get(current).stream()
				.filter(trail -> !visited.contains(trail.end()))
				.mapToInt(trail -> trail.length() + findMaxPathRecursive(trailsMap, trail.end(), end, nextVisited))
				.max()
				// пути, не дошедшие до конца не отсекаются, но им сетится отрицательная длина
				.orElse(Integer.MIN_VALUE);
	}
	
	record Trail(Point start, Point end, int length) {
	}
	
	record Field(Map<Point, Character> points, Point start, Point end) {
		
		Set<Point> findForks() {
			return points.entrySet().stream()
					.filter(e -> e.getValue() != '#')
					.map(Map.Entry::getKey)
					.filter(p -> availableSteps(p).count() > 2)
					.collect(Collectors.toSet());
		}
		
		List<Trail> findTrails(Set<Point> forks, Point start) {
			Set<Point> visited = new HashSet<>(List.of(start));
			List<Trail> trails = new ArrayList<>();
			
			List<Pair<Point, Integer>> currents = new ArrayList<>(List.of(Pair.of(start, 0)));
			while (!currents.isEmpty()) {
				Pair<Point, Integer> current = currents.removeFirst();
				
				Point currentPosition = current.left();
				if (currentPosition != start && forks.contains(currentPosition)) {
					trails.add(new Trail(start, currentPosition, current.right()));
					continue;
				}
				
				availableSteps(currentPosition)
						.filter(p -> !visited.contains(p))
						.forEach(p -> {
							visited.add(p);
							currents.add(Pair.of(p, current.right() + 1));
						});
			}
			return trails;
		}
		
		Stream<Point> availableSteps(Point position) {
			return directions(position)
					.map(position::shift)
					.filter(p -> {
						Character c = points.get(p);
						return c != null && c != '#';
					});
		}
		
		Stream<Direction> directions(Point position) {
			Character c = points.get(position);
			return c == '.' ? Direction.stream() : Stream.of(Direction.parseSymbol(c));
		}
		
		static Field parseField(List<String> lines) {
			Map<Point, Character> points = Point.readFieldMap(lines);
			List<Point> empty = points.entrySet().stream()
					.filter(e -> e.getValue() == '.')
					.map(Map.Entry::getKey)
					.sorted(Comparator.comparing(Point::y))
					.toList();
			return new Field(points, empty.getFirst(), empty.getLast());
		}
	}
}