package org.advent.year2016.day24;

import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Point;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day24()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 14, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 462, 676)
		);
	}
	
	Point start;
	Set<Point> targets;
	Set<Point> open;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		Map<Character, List<Point>> field = Point.readField(Utils.readLines(input));
		start = field.get('0').getFirst();
		targets = field.entrySet().stream()
				.filter(e -> Character.isDigit(e.getKey()))
				.flatMap(e -> e.getValue().stream())
				.filter(p -> !p.equals(start))
				.collect(Collectors.toSet());
		open = field.entrySet().stream()
				.filter(e -> e.getKey() != '#')
				.flatMap(e -> e.getValue().stream())
				.collect(Collectors.toSet());
	}
	
	@Override
	public Object part1() {
		return solve(false);
	}
	
	@Override
	public Object part2() {
		return solve(true);
	}
	
	int solve(boolean returnToStart) {
		Map<Set<Point>, Integer> lengths = new HashMap<>();
		Stream.concat(Stream.of(start), targets.stream())
				.forEach(from -> targets.stream()
						.filter(to -> !from.equals(to))
						.forEach(to -> lengths.computeIfAbsent(Set.of(from, to), k -> pathSteps(from, to, open))));
		
		Map<Pair<Point, Set<Point>>, Integer> bestPaths = new HashMap<>();
		return nextPath(lengths, bestPaths, start, targets, returnToStart ? start : null, 0);
	}
	
	int nextPath(Map<Set<Point>, Integer> lengths, Map<Pair<Point, Set<Point>>, Integer> bestPaths,
	             Point position, Set<Point> targets, Point lastTarget, int pathLength) {
		if (targets.isEmpty())
			return pathLength + (lastTarget == null ? 0 : lengths.get(Set.of(position, lastTarget)));
		
		if (pathLength > bestPaths.compute(Pair.of(position, targets),
				(k, v) -> v == null ? pathLength : Math.min(v, pathLength)))
			return Integer.MAX_VALUE;
		
		return targets.stream()
				.mapToInt(target -> nextPath(lengths, bestPaths, target, removed(targets, target), lastTarget,
						pathLength + lengths.get(Set.of(position, target))))
				.min()
				.orElse(Integer.MAX_VALUE);
	}
	
	
	int pathSteps(Point start, Point end, Set<Point> open) {
		Set<Point> current = Set.of(start);
		Set<Point> visited = new HashSet<>();
		int step = 0;
		while (!current.isEmpty() && !current.contains(end)) {
			visited.addAll(current);
			current = current.stream()
					.flatMap(c -> Direction.stream().map(c::shift))
					.filter(p -> !visited.contains(p))
					.filter(open::contains)
					.collect(Collectors.toSet());
			step++;
		}
		return step;
	}
	
	<T> Set<T> removed(Set<T> set, T item) {
		set = new HashSet<>(set);
		set.remove(item);
		return set;
	}
}