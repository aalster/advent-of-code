package org.advent.year2019.day18;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day18 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day18()).runAll();
//		new DayRunner(new Day18()).run("example.txt", 1);
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 8, null),
				new ExpectedAnswers("example2.txt", 86, null),
				new ExpectedAnswers("example3.txt", 132, null),
				new ExpectedAnswers("example4.txt", 136, null),
				new ExpectedAnswers("example5.txt", 81, null),
				new ExpectedAnswers("input.txt", null, null)
		);
	}
	
	Field field;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		Map<Character, List<Point>> fieldMap = Point.readField(Utils.readLines(input));
		Point position = fieldMap.get('@').getFirst();
		Map<Character, Point> doors = fieldMap.entrySet().stream()
				.filter(e -> 'A' <= e.getKey() && e.getKey() <= 'Z')
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFirst()));
		Map<Character, Point> keys = fieldMap.entrySet().stream()
				.filter(e -> 'a' <= e.getKey() && e.getKey() <= 'z')
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFirst()));
		Set<Point> walls = new HashSet<>(fieldMap.get('#'));
		walls.addAll(doors.values());
		field = new Field(position, walls, doors, keys, 0);
	}
	
	@Override
	public Object part1() {
		Map<Point, Character> doors = field.doors.entrySet().stream().collect(Collectors.toMap(java.util.Map.Entry::getValue, java.util.Map.Entry::getKey));
		Map<Character, Point> keys = field.keysLeft();
		Set<Point> walls = new HashSet<>(field.walls);
		walls.removeAll(doors.keySet());
		
		Map<Character, Map<Character, PathInfo>> paths = new HashMap<>();
		Map<Character, PathInfo> startingPaths = new HashMap<>();
		for (Map.Entry<Character, Point> entry : keys.entrySet()) {
			startingPaths.put(entry.getKey(), pathInfo(walls, doors, field.position, entry.getValue()));
		}
		paths.put('@', startingPaths);
		
		List<Character> keysNames = new ArrayList<>(keys.keySet());
		while (!keysNames.isEmpty()) {
			Character key = keysNames.removeLast();
			for (Character other : keysNames) {
				PathInfo path = pathInfo(walls, doors, keys.get(key), keys.get(other));
				paths.computeIfAbsent(key, k -> new HashMap<>()).put(other, path);
				paths.computeIfAbsent(other, k -> new HashMap<>()).put(key, path);
			}
		}
		
		Set<State> states = Set.of(new State('@', field.keysLeft.keySet(), 0));
		int minTotalDistance = Integer.MAX_VALUE;
		while (!states.isEmpty()) {
			int _minTotalDistance = minTotalDistance;
			states = states.stream()
					.flatMap(s -> s.next(paths))
					.filter(f -> f.totalDistance < _minTotalDistance)
//					.peek(Field::print)
					.collect(Collectors.toSet());
			minTotalDistance = states.stream()
					.filter(f -> f.keysLeft.isEmpty())
					.mapToInt(State::totalDistance)
					.min()
					.orElse(minTotalDistance);
			System.out.println("states: " + states.size() + " minTotalDistance: " + minTotalDistance);
		}
		return minTotalDistance;
	}
	
	@Override
	public Object part2() {
		return null;
	}
	
	PathInfo pathInfo(Set<Point> walls, Map<Point, Character> doors, Point start, Point end) {
		Set<Point> path = findPath(walls, start, end);
		Set<Character> doorsNames = path.stream().map(doors::get).filter(Objects::nonNull).map(Character::toLowerCase).collect(Collectors.toSet());
		return new PathInfo(doorsNames, path.size());
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
	
	record PathInfo(Set<Character> doors, int distance) {
	
	}
	
	record State(Character currentKey, Set<Character> keysLeft, int totalDistance) {
		Stream<State> next(Map<Character, Map<Character, PathInfo>> paths) {
			Map<Character, PathInfo> nextPaths = paths.get(currentKey);
			List<State> next = new ArrayList<>();
			for (Character nextKey : keysLeft) {
				PathInfo path = nextPaths.get(nextKey);
				if (keysLeft.stream().anyMatch(path.doors::contains))
					continue;
				
				Set<Character> nextKeysLeft = new HashSet<>(keysLeft);
				nextKeysLeft.remove(nextKey);
				next.add(new State(nextKey, nextKeysLeft, totalDistance + path.distance));
			}
			return next.stream();
		}
	}
	
	record Field(Point position, Set<Point> walls, Map<Character, Point> doors, Map<Character, Point> keysLeft,
	             int totalDistance, int minPossibleDistance) {
		
		public Field(Point position, Set<Point> walls, Map<Character, Point> doors, Map<Character, Point> keysLeft, int totalDistance) {
			this(position, walls, doors, keysLeft, totalDistance, 0);
		}
		
		Stream<Field> next() {
			return keysDistances().entrySet().stream()
					.map(e -> {
						Point nextPosition = keysLeft.get(e.getKey());
						Set<Point> nextWalls = new HashSet<>(walls);
						nextWalls.remove(doors.get(Character.toUpperCase(e.getKey())));
						Map<Character, Point> nextKeysLeft = new HashMap<>(keysLeft);
						nextKeysLeft.remove(e.getKey());
						return new Field(nextPosition, nextWalls, doors, nextKeysLeft, totalDistance + e.getValue());
					});
		}
		
		Map<Character, Integer> keysDistances() {
			Map<Character, Integer> distances = new HashMap<>();
			Set<Point> current = Set.of(position);
			Set<Point> visited = new HashSet<>(current);
			int distance = 0;
			while (distances.size() < keysLeft.size() && !current.isEmpty()) {
				current = current.stream()
						.flatMap(c -> Direction.stream().map(c::shift))
						.filter(n -> !visited.contains(n))
						.filter(n -> !walls.contains(n))
						.collect(Collectors.toSet());
				distance++;
				for (Map.Entry<Character, Point> entry : keysLeft.entrySet())
					if (current.contains(entry.getValue()))
						distances.put(entry.getKey(), distance);
				visited.addAll(current);
			}
//			System.out.println("distances from position: " + position);
//			distances.entrySet().forEach(System.out::println);
			return distances;
		}
		
		void print() {
			System.out.println();
			System.out.println(this);
			Point.printField(walls, p -> {
				if (position.equals(p))
					return '@';
				return walls.contains(p) ? '#' : '.';
			});
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Field other) {
				return other.totalDistance == totalDistance && other.position.equals(position) && other.keysLeft.equals(keysLeft);
			}
			return false;
		}
		
		static int minPossibleDistance(Point position, int current, Collection<Point> keysLeft) {
			return current = keysLeft.stream().mapToInt(position::distanceTo).sum();
		}
	}
	
	public Object part3() {
		Queue<Field> fields = new PriorityQueue<>(Comparator.comparing(Field::totalDistance).thenComparing(f -> f.keysLeft.size()));
		fields.add(field);
		int minTotalDistance = Integer.MAX_VALUE;
		while (!fields.isEmpty()) {
			Field current = fields.poll();
			if (current.totalDistance >= minTotalDistance)
				continue;
			if (current.keysLeft.isEmpty()) {
				minTotalDistance = current.totalDistance;
				continue;
			}
			current.next().forEach(fields::offer);
		}
		return minTotalDistance;
	}
}