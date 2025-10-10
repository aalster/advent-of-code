package org.advent.year2019.day18;

import org.advent.common.Direction;
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

public class Day18 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day18()).runAll();
//		new DayRunner(new Day18()).run("example.txt", 2);
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
				new ExpectedAnswers("input.txt", 4270, null)
		);
	}
	
	Point position;
	Set<Point> walls;
	Map<Point, Character> doors;
	Map<Point, Character> keys;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		Map<Character, List<Point>> fieldMap = Point.readField(Utils.readLines(input));
		position = fieldMap.get('@').getFirst();
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
		Map<Character, Point> keysByName = keys.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		
		Map<Character, PathInfo> startingPaths = new HashMap<>();
		for (Map.Entry<Character, Point> entry : keysByName.entrySet())
			startingPaths.put(entry.getKey(), pathInfo(walls, doors, keys, position, entry.getValue()));
		
		Map<Character, Map<Character, PathInfo>> paths = new HashMap<>();
		paths.put('@', startingPaths);
		
		List<Character> keysNames = new ArrayList<>(keysByName.keySet());
		while (!keysNames.isEmpty()) {
			Character key = keysNames.removeLast();
			for (Character other : keysNames) {
				PathInfo path = pathInfo(walls, doors, keys, keysByName.get(key), keysByName.get(other));
				paths.computeIfAbsent(key, k -> new HashMap<>()).put(other, path);
				paths.computeIfAbsent(other, k -> new HashMap<>()).put(key, path);
			}
		}
		
		int minTotalDistance = Integer.MAX_VALUE;
		LinkedHashMap<State, Integer> states = new LinkedHashMap<>();
		states.put(new State('@', keysByName.keySet()), 0);
		while (!states.isEmpty()) {
			State current = states.firstEntry().getKey();
			int currentSteps = states.remove(current);
			if (currentSteps > minTotalDistance)
				continue;
			
			Map<State, Integer> next = current.next(paths);
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
	
	@Override
	public Object part2() {
		return null;
	}
	
	PathInfo pathInfo(Set<Point> walls, Map<Point, Character> doors, Map<Point, Character> keys, Point start, Point end) {
		Set<Point> path = findPath(walls, start, end);
		Set<Character> doorsNames = path.stream().map(doors::get).filter(Objects::nonNull).map(Character::toLowerCase).collect(Collectors.toSet());
		Set<Character> keysNames = path.stream().filter(p -> !p.equals(end)).map(keys::get).filter(Objects::nonNull).collect(Collectors.toSet());
		return new PathInfo(doorsNames, keysNames, path.size());
	}
	
	Set<Point> findPath(Set<Point> walls, Point start, Point end) {
		Map<Point, Integer> visited = pathMap(start, end, walls);
		if (visited.isEmpty())
			return Set.of();
		int step = visited.get(end);
		Set<Point> path = new HashSet<>(step);
		while (step > 0) {
			path.add(end);
			step--;
			int currentStep = step;
			end = Direction.stream().map(end::shift).filter(p -> visited.getOrDefault(p, -1) == currentStep).findAny().orElseThrow();
		}
		return path;
	}
	
	Map<Point, Integer> pathMap(Point start, Point end, Set<Point> walls) {
		Map<Point, Integer> visited = new HashMap<>();
		Set<Point> current = Set.of(start);
		int step = 0;
		while (!(end != null && current.contains(end)) && !current.isEmpty()) {
			int currentStep = step;
			current = current.stream()
					.peek(c -> visited.put(c, currentStep))
					.flatMap(c -> Direction.stream().map(c::shift))
					.filter(n -> !visited.containsKey(n))
					.filter(n -> !walls.contains(n))
					.collect(Collectors.toSet());
			step++;
		}
		if (end != null)
			visited.put(end, step);
		return visited;
	}
	
	record PathInfo(Set<Character> doors, Set<Character> keys, int distance) {
	}
	
	record State(Character currentKey, Set<Character> keysLeft) {
		Map<State, Integer> next(Map<Character, Map<Character, PathInfo>> paths) {
			Map<Character, PathInfo> nextPaths = paths.get(currentKey);
			Map<State, Integer> next = new HashMap<>();
			for (Character nextKey : keysLeft) {
				PathInfo path = nextPaths.get(nextKey);
				if (keysLeft.stream().anyMatch(path.doors::contains))
					continue;
				if (path.keys.stream().anyMatch(keysLeft::contains))
					continue;
				
				Set<Character> nextKeysLeft = new HashSet<>(keysLeft);
				nextKeysLeft.remove(nextKey);
				next.put(new State(nextKey, nextKeysLeft), path.distance);
			}
			return next;
		}
	}
}