package org.advent.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MazeUtils {
	private static final Map<Point, Direction> directionsByPoint = Direction.stream()
			.collect(Collectors.toMap(Direction::getP, d -> d));
	
	public static Map<Point, Integer> stepsMap(Point start, Predicate<Point> available) {
		return stepsMap(start, null, available);
	}
	
	public static Map<Point, Integer> stepsMap(Point start, Point target, Predicate<Point> available) {
		Map<Point, Integer> steps = new HashMap<>();
		Set<Point> current = Set.of(start);
		int step = 0;
		steps.put(start, step);
		while (!(target != null && current.contains(target)) && !current.isEmpty()) {
			int _step = ++step;
			current = current.stream()
					.flatMap(c -> Direction.stream().map(c::shift))
					.filter(available)
					.filter(n -> !steps.containsKey(n))
					.peek(n -> steps.put(n, _step))
					.collect(Collectors.toSet());
		}
		return steps;
	}
	
	public static Map<Point, Integer> stepsMap(Point start, Point target, BiPredicate<Point, Direction> availableDirection) {
		Map<Point, Integer> steps = new HashMap<>();
		Set<Point> current = Set.of(start);
		int step = 0;
		steps.put(start, step);
		while (!current.contains(target) && !current.isEmpty()) {
			int _step = ++step;
			current = current.stream()
					.flatMap(c -> Direction.stream().filter(d -> availableDirection.test(c, d)).map(c::shift))
					.filter(n -> !steps.containsKey(n))
					.peek(c -> steps.put(c, _step))
					.collect(Collectors.toSet());
		}
		return steps;
	}
	
	public static List<Point> findPath(Point start, Point target, Predicate<Point> available) {
		return findPath(stepsMap(start, target, available), target);
	}
	
	public static List<Point> findPath(Map<Point, Integer> stepsMap, Point target) {
		Integer step = stepsMap.get(target);
		if (step == null)
			return List.of();
		
		List<Point> path = new ArrayList<>();
		while (step > 0) {
			path.add(target);
			int _step = --step;
			target = Direction.stream()
					.map(target::shift)
					.filter(n -> stepsMap.getOrDefault(n, -1) == _step)
					.findAny()
					.orElseThrow();
		}
		return path.reversed();
	}
	
	public static List<Direction> pathToDirections(List<Point> path) {
		Point previous = path.getFirst();
		List<Direction> result = new ArrayList<>();
		for (int i = 1; i < path.size(); i++) {
			Point next = path.get(i);
			result.add(directionsByPoint.get(next.subtract(previous)));
			previous = next;
		}
		return result;
	}
}
