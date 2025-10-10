package org.advent.year2019.day18;

import org.advent.common.Direction;
import org.advent.common.DirectionExt;
import org.advent.common.Pair;
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
//		new DayRunner(new Day18()).run("input.txt", 2);
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
		PathsCache pathsCache = PathsCache.compute(positions, walls, doors, keys);
		
		int minTotalDistance = Integer.MAX_VALUE;
		LinkedHashMap<State, Integer> states = new LinkedHashMap<>();
		states.put(new State(positions, new HashSet<>(keys.values())), 0);
		while (!states.isEmpty()) {
			State current = states.firstEntry().getKey();
			int currentSteps = states.remove(current);
			if (currentSteps > minTotalDistance)
				continue;
			
			List<Pair<State, Integer>> next = current.next(pathsCache);
			for (Pair<State, Integer> pair : next) {
				int nextSteps = currentSteps + pair.right();
				if (pair.left().keysLeft.isEmpty()) {
					if (nextSteps < minTotalDistance)
						minTotalDistance = nextSteps;
					continue;
				}
				if (nextSteps > minTotalDistance)
					continue;
				states.compute(pair.left(), (k, v) -> Math.min(v == null ? Integer.MAX_VALUE : v, nextSteps));
			}
		}
		return minTotalDistance;
	}
	
	record State(List<Point> currentPositions, Set<Character> keysLeft) {
		
		List<Pair<State, Integer>> next(PathsCache pathsCache) {
			List<Pair<State, Integer>> next = new ArrayList<>();
			for (int currentPositionIndex = 0; currentPositionIndex < currentPositions.size(); currentPositionIndex++) {
				Point currentPosition = currentPositions.get(currentPositionIndex);
				for (PathResult path : pathsCache.possiblePaths(currentPosition, keysLeft)) {
					List<Point> nextCurrentPositions = new ArrayList<>(currentPositions);
					nextCurrentPositions.set(currentPositionIndex, path.nextPosition);
					next.add(Pair.of(new State(nextCurrentPositions, path.nextKeysLeft), path.distance));
				}
			}
			return next;
		}
	}
	
	record PathInfo(Set<Character> doors, Set<Character> keys, int distance) {
	}
	
	record PathResult(Point nextPosition, Set<Character> nextKeysLeft, int distance) {
	}
	
	record PathsCache(
			Map<Point, Map<Character, PathInfo>> paths,
			Map<Character, Point> keysByName,
			Map<Point, Map<Set<Character>, List<PathResult>>> cache) {
		
		List<PathResult> possiblePaths(Point currentPosition, Set<Character> keysLeft) {
			return cache.computeIfAbsent(currentPosition, k -> new HashMap<>())
					.computeIfAbsent(keysLeft, k -> {
						Map<Character, PathInfo> nextPaths = paths.get(currentPosition);
						if (nextPaths == null)
							return List.of();
						
						List<PathResult> result = new ArrayList<>();
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
							
							result.add(new PathResult(keysByName.get(nextKey), nextKeysLeft, path.distance));
						}
						return result;
					});
		}
		
		static PathsCache compute(List<Point> positions, Set<Point> walls, Map<Point, Character> doors, Map<Point, Character> keys) {
			Map<Character, Point> keysByName = keys.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
			Map<Point, Map<Character, PathInfo>> paths = new HashMap<>();
			
			Map<Character, PathInfo> startingPaths = new HashMap<>();
			for (Point position : positions) {
				Map<Point, Integer> pathMap = pathMap(position, walls);
				for (Map.Entry<Character, Point> entry : keysByName.entrySet()) {
					PathInfo path = pathInfo(pathMap, doors, keys, position, entry.getValue());
					if (path != null)
						startingPaths.put(entry.getKey(), path);
				}
				paths.put(position, startingPaths);
			}
			
			List<Character> keysNames = new ArrayList<>(keysByName.keySet());
			while (!keysNames.isEmpty()) {
				Character key = keysNames.removeLast();
				Point position = keysByName.get(key);
				Map<Point, Integer> pathMap = pathMap(position, walls);
				for (Character other : keysNames) {
					Point target = keysByName.get(other);
					PathInfo path = pathInfo(pathMap, doors, keys, position, target);
					if (path == null)
						continue;
					paths.computeIfAbsent(position, k -> new HashMap<>()).put(other, path);
					paths.computeIfAbsent(target, k -> new HashMap<>()).put(key, path);
				}
			}
			return new PathsCache(paths, keysByName, new HashMap<>());
		}
		
		static PathInfo pathInfo(Map<Point, Integer> pathMap, Map<Point, Character> doors, Map<Point, Character> keys,
		                         Point start, Point target) {
			if (pathMap.get(target) == null)
				return null;
			
			Set<Point> path = findPath(pathMap, target);
			Set<Character> doorsNames = path.stream()
					.map(doors::get)
					.filter(Objects::nonNull)
					.map(Character::toLowerCase)
					.collect(Collectors.toSet());
			Set<Character> keysNames = path.stream()
					.filter(p -> !p.equals(start))
					.filter(p -> !p.equals(target))
					.map(keys::get)
					.filter(Objects::nonNull)
					.collect(Collectors.toSet());
			return new PathInfo(doorsNames, keysNames, path.size());
		}
		
		static Set<Point> findPath(Map<Point, Integer> pathMap, Point target) {
			int step = pathMap.get(target);
			
			Set<Point> path = new HashSet<>(step);
			while (step > 0) {
				path.add(target);
				step--;
				int currentStep = step;
				target = Direction.stream()
						.map(target::shift)
						.filter(p -> pathMap.getOrDefault(p, -1) == currentStep)
						.findAny().orElseThrow();
			}
			return path;
		}
		
		static Map<Point, Integer> pathMap(Point start, Set<Point> walls) {
			Map<Point, Integer> visited = new HashMap<>();
			Set<Point> current = Set.of(start);
			int step = 0;
			while (!current.isEmpty()) {
				int currentStep = step;
				current = current.stream()
						.peek(c -> visited.put(c, currentStep))
						.flatMap(c -> Direction.stream().map(c::shift))
						.filter(n -> !visited.containsKey(n))
						.filter(n -> !walls.contains(n))
						.collect(Collectors.toSet());
				step++;
			}
			return visited;
		}
	}
}