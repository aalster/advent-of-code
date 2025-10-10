package org.advent.year2019.day18;

import org.advent.common.Direction;
import org.advent.common.DirectionExt;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day18 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day18()).runAll();
//		new DayRunner(new Day18()).run("example8.txt", 2);
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 8, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 86, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 132, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example4.txt", 136, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example5.txt", 81, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example6.txt", ExpectedAnswers.IGNORE, 8),
				new ExpectedAnswers("example7.txt", ExpectedAnswers.IGNORE, 24),
				new ExpectedAnswers("example8.txt", ExpectedAnswers.IGNORE, 32),
				new ExpectedAnswers("example9.txt", ExpectedAnswers.IGNORE, 72),
				new ExpectedAnswers("input.txt", 4270, 1982)
		);
	}
	
	List<Point> positions;
	Set<Point> walls;
	Map<Point, Character> doors;
	Map<Point, Character> keys;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		Map<Character, List<Point>> fieldMap = Point.readField(Utils.readLines(input));
		positions = fieldMap.get('@');
		walls = new HashSet<>(fieldMap.get('#'));
		doors = fieldMap.entrySet().stream()
				.filter(e -> 'A' <= e.getKey() && e.getKey() <= 'Z')
				.collect(Collectors.toMap(e -> e.getValue().getFirst(), Map.Entry::getKey));
		keys = fieldMap.entrySet().stream()
				.filter(e -> 'a' <= e.getKey() && e.getKey() <= 'z')
				.collect(Collectors.toMap(e -> e.getValue().getFirst(), Map.Entry::getKey));
	}
	
	@Override
	public Object part1() {
		return solve(positions, walls, doors, keys);
	}
	
	@Override
	public Object part2() {
		if (positions.size() == 1) {
			Point center = positions.getFirst();
			positions = Stream.of(DirectionExt.NW, DirectionExt.NE, DirectionExt.SW, DirectionExt.SE)
					.map(d -> d.shift(center))
					.toList();
			walls.add(center);
			walls.addAll(Direction.stream().map(center::shift).toList());
		}
		return solve(positions, walls, doors, keys);
	}
	
	public int solve(List<Point> positions, Set<Point> walls, Map<Point, Character> doors, Map<Point, Character> keys) {
		Map<Character, Point> keysByName = keys.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		
		Map<Point, Map<Character, PathInfo>> paths = new HashMap<>();
		
		Map<Character, PathInfo> startingPaths = new HashMap<>();
		for (Point position : positions) {
			for (Map.Entry<Character, Point> entry : keysByName.entrySet()) {
				PathInfo path = pathInfo(walls, doors, keys, position, entry.getValue());
				if (path != null)
					startingPaths.put(entry.getKey(), path);
			}
			paths.put(position, startingPaths);
		}
		
		List<Character> keysNames = new ArrayList<>(keysByName.keySet());
		while (!keysNames.isEmpty()) {
			Character key = keysNames.removeLast();
			for (Character other : keysNames) {
				PathInfo path = pathInfo(walls, doors, keys, keysByName.get(key), keysByName.get(other));
				if (path == null)
					continue;
				paths.computeIfAbsent(keysByName.get(key), k -> new HashMap<>()).put(other, path);
				paths.computeIfAbsent(keysByName.get(other), k -> new HashMap<>()).put(key, path);
			}
		}
		
		int minTotalDistance = Integer.MAX_VALUE;
		LinkedHashMap<State, Integer> states = new LinkedHashMap<>();
		states.put(new State(positions, keysByName.keySet()), 0);
		while (!states.isEmpty()) {
			State current = states.firstEntry().getKey();
			int currentSteps = states.remove(current);
			if (currentSteps > minTotalDistance)
				continue;
			
			Map<State, Integer> next = current.next(paths, keysByName);
			for (Map.Entry<State, Integer> entry : next.entrySet()) {
				if (entry.getKey().keysLeft.isEmpty()) {
					if (currentSteps + entry.getValue() < minTotalDistance)
						minTotalDistance = currentSteps + entry.getValue();
					continue;
				}
				if (currentSteps + entry.getValue() > minTotalDistance)
					continue;
				states.compute(entry.getKey(), (k, v) -> Math.min(v == null ? Integer.MAX_VALUE : v, currentSteps + entry.getValue()));
			}
		}
		return minTotalDistance;
	}
	
	PathInfo pathInfo(Set<Point> walls, Map<Point, Character> doors, Map<Point, Character> keys, Point start, Point end) {
		Set<Point> path = findPath(walls, start, end);
		if (path.isEmpty())
			return null;
		Set<Character> doorsNames = path.stream().map(doors::get).filter(Objects::nonNull).map(Character::toLowerCase).collect(Collectors.toSet());
		Set<Character> keysNames = path.stream().filter(p -> !p.equals(end)).map(keys::get).filter(Objects::nonNull).collect(Collectors.toSet());
		return new PathInfo(doorsNames, keysNames, path.size());
	}
	
	Set<Point> findPath(Set<Point> walls, Point start, Point end) {
		Map<Point, Integer> visited = pathMap(start, end, walls);
		if (visited.isEmpty())
			return Set.of();
		Integer step = visited.get(end);
		if (step == null)
			return Set.of();
		
		Set<Point> path = new HashSet<>(step);
		while (step > 0) {
			path.add(end);
			step--;
			int currentStep = step;
			end = Direction.stream()
					.map(end::shift)
					.filter(p -> visited.getOrDefault(p, -1) == currentStep)
					.findAny().orElseThrow();
		}
		return path;
	}
	
	Map<Point, Integer> pathMap(Point start, Point end, Set<Point> walls) {
		Map<Point, Integer> visited = new HashMap<>();
		Set<Point> current = Set.of(start);
		int step = 0;
		while (!current.isEmpty()) {
			int currentStep = step;
			Set<Point> next = current.stream()
					.peek(c -> visited.put(c, currentStep))
					.flatMap(c -> Direction.stream().map(c::shift))
					.filter(n -> !visited.containsKey(n))
					.filter(n -> !walls.contains(n))
					.collect(Collectors.toSet());
			
			if (current.contains(end))
				break;
			
			step++;
			current = next;
		}
		return visited;
	}
	
	record PathInfo(Set<Character> doors, Set<Character> keys, int distance) {
	}
	
	record State(List<Point> currentPositions, Set<Character> keysLeft) {
		Map<State, Integer> next(Map<Point, Map<Character, PathInfo>> paths, Map<Character, Point> keysByName) {
			Map<State, Integer> next = new HashMap<>();
			for (int currentPositionIndex = 0; currentPositionIndex < currentPositions.size(); currentPositionIndex++) {
				Point currentPosition = currentPositions.get(currentPositionIndex);
				
				Map<Character, PathInfo> nextPaths = paths.getOrDefault(currentPosition, Map.of());
				for (Character nextKey : keysLeft) {
					PathInfo path = nextPaths.get(nextKey);
					if (path == null)
						continue;
					if (keysLeft.stream().anyMatch(path.doors::contains))
						continue;
					if (path.keys.stream().anyMatch(keysLeft::contains))
						continue;
					
					Set<Character> nextKeysLeft = new HashSet<>(keysLeft);
					nextKeysLeft.remove(nextKey);
					
					List<Point> nextCurrentPositions = new ArrayList<>(currentPositions);
					nextCurrentPositions.set(currentPositionIndex, keysByName.get(nextKey));
					next.put(new State(nextCurrentPositions, nextKeysLeft), path.distance);
				}
			}
			return next;
		}
	}
}