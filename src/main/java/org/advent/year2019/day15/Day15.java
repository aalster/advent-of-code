package org.advent.year2019.day15;

import lombok.Data;
import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Day15 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day15()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 280, 400)
		);
	}
	
	IntcodeComputer computer;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		computer = IntcodeComputer.parse(input.nextLine());
	}
	
	@Override
	public Object part1() {
		Map<Point, Location> field = new TreeMap<>(Point.COMPARATOR);
		return findPath(field, Point.ZERO, findOxygenSystem(field, false)).size();
	}
	
	@Override
	public Object part2() {
		Map<Point, Location> field = new TreeMap<>(Point.COMPARATOR);
		Point oxygenSystem = findOxygenSystem(field, true);
		Set<Point> empty = field.entrySet().stream()
				.filter(e -> e.getValue() == Location.EMPTY)
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());
		Map<Point, Integer> fieldSteps = pathMap(oxygenSystem, null, empty);
		return fieldSteps.values().stream().mapToInt(i -> i).max().orElse(0);
	}
	
	private Point findOxygenSystem(Map<Point, Location> field, boolean exploreAllField) {
		Point position = Point.ZERO;
		field.put(position, Location.EMPTY);
		Direction.stream().map(position::shift).forEach(p -> field.put(p, Location.UNKNOWN));
		
		Point oxygenSystem = Point.ZERO;
		while (true) {
			Point _position = position;
			Point unknown = field.entrySet().stream()
					.filter(e -> e.getValue() == Location.UNKNOWN)
					.map(Map.Entry::getKey)
					.min(Comparator.comparing(_position::distanceTo))
					.orElse(null);
			if (unknown == null)
				break;
			
			List<Point> path = findPath(field, position, unknown);
			List<Direction> directions = new ArrayList<>(path.size());
			Point pathCurrent = position;
			for (Point pathPoint : path) {
				directions.add(directionTo(pathCurrent, pathPoint));
				pathCurrent = pathPoint;
			}
			
			while (!directions.isEmpty()) {
				Direction direction = directions.removeFirst();
				long type = computer.runUntilOutput(new DirectionInputProvider(direction));
				
				if (type == 0) {
					field.put(position.shift(direction), Location.WALL);
				} else {
					position = position.shift(direction);
					field.put(position, Location.EMPTY);
					if (type == 2) {
						oxygenSystem = position;
						if (!exploreAllField)
							return oxygenSystem;
					}
					if (type == 1)
						Direction.stream().map(position::shift).forEach(p -> field.putIfAbsent(p, Location.UNKNOWN));
				}
				
//				printField(field, position);
			}
		}
		return oxygenSystem;
	}
	
	private void printField(Map<Point, Location> field, Point position) {
		System.out.println();
		Point.printField(field.keySet(), p -> {
			if (position.equals(p))
				return 'D';
			Location location = field.get(p);
			return switch (location) {
				case WALL -> '#';
				case EMPTY -> '.';
				case UNKNOWN -> '?';
				case null -> ' ';
			};
		});
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	List<Point> findPath(Map<Point, Location> field, Point start, Point end) {
		Set<Point> empty = field.entrySet().stream()
				.filter(e -> e.getValue() == Location.EMPTY)
				.map(Map.Entry::getKey)
				.collect(Collectors.toCollection(HashSet::new));
		empty.add(end);
		
		Map<Point, Integer> visited = pathMap(start, end, empty);
		if (visited.isEmpty())
			return List.of();
		int step = visited.get(end);
		List<Point> path = new ArrayList<>(step);
		while (step > 0) {
			path.add(end);
			step--;
			int currentStep = step;
			end = Direction.stream().map(end::shift).filter(p -> visited.getOrDefault(p, -1) == currentStep).findAny().orElseThrow();
		}
		return path.reversed();
	}
	
	Map<Point, Integer> pathMap(Point start, Point end, Set<Point> available) {
		Map<Point, Integer> visited = new HashMap<>();
		Set<Point> current = Set.of(start);
		int step = 0;
		while (!(end != null && current.contains(end)) && !current.isEmpty()) {
			int currentStep = step;
			current = current.stream()
					.peek(c -> visited.put(c, currentStep))
					.flatMap(c -> Direction.stream().map(c::shift))
					.filter(n -> !visited.containsKey(n))
					.filter(available::contains)
					.collect(Collectors.toSet());
			step++;
		}
		if (end != null)
			visited.put(end, step);
		return visited;
	}
	
	Direction directionTo(Point from, Point to) {
		if (to.x() < from.x())
			return Direction.LEFT;
		if (from.x() < to.x())
			return Direction.RIGHT;
		return to.y() < from.y() ? Direction.UP : Direction.DOWN;
	}
	
	enum Location {
		UNKNOWN, EMPTY, WALL;
	}
	
	@Data
	static class DirectionInputProvider implements InputProvider {
		final Direction direction;
		
		@Override
		public boolean hasNext() {
			return true;
		}
		
		@Override
		public long nextInput() {
			return switch (direction) {
				case UP -> 1;
				case DOWN -> 2;
				case LEFT -> 3;
				case RIGHT -> 4;
			};
		}
	}
}