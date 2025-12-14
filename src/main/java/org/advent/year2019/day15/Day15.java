package org.advent.year2019.day15;

import org.advent.common.Direction;
import org.advent.common.MazeUtils;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer;
import org.advent.year2019.intcode_computer.OutputConsumer;

import java.util.ArrayList;
import java.util.Comparator;
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
	
	static final boolean silent = true;
	long[] program;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		program = IntcodeComputer.parseProgram(input.nextLine());
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
		Map<Point, Integer> fieldSteps = MazeUtils.stepsMap(oxygenSystem, empty::contains);
		return fieldSteps.values().stream().mapToInt(i -> i).max().orElse(0);
	}
	
	Point findOxygenSystem(Map<Point, Location> field, boolean exploreAllField) {
		RepairDroid repairDroid = new RepairDroid(program);
		
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
				long type = repairDroid.step(direction);
				
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
				if (!silent) {
					System.out.println();
					printField(field, position);
					Utils.sleep(200);
				}
			}
		}
		return oxygenSystem;
	}
	
	void printField(Map<Point, Location> field, Point position) {
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
	}
	
	List<Point> findPath(Map<Point, Location> field, Point start, Point end) {
		Set<Point> empty = field.entrySet().stream()
				.filter(e -> e.getValue() == Location.EMPTY)
				.map(Map.Entry::getKey)
				.collect(Collectors.toCollection(HashSet::new));
		empty.add(end);
		
		return MazeUtils.findPath(start, end, empty::contains);
	}
	
	Direction directionTo(Point from, Point to) {
		if (to.x() < from.x())
			return Direction.LEFT;
		if (from.x() < to.x())
			return Direction.RIGHT;
		return to.y() < from.y() ? Direction.UP : Direction.DOWN;
	}
	
	enum Location {
		UNKNOWN, EMPTY, WALL
	}
	
	static class RepairDroid {
		final InputProvider.BufferingInputProvider inputProvider = InputProvider.buffering();
		final OutputConsumer.BufferingOutputConsumer output = OutputConsumer.buffering();
		final IntcodeComputer computer;
		
		RepairDroid(long[] program) {
			this.computer = new IntcodeComputer(program, inputProvider, output);
		}
		
		long step(Direction direction) {
			inputProvider.append(switch (direction) {
				case UP -> 1;
				case DOWN -> 2;
				case LEFT -> 3;
				case RIGHT -> 4;
			});
			computer.run();
			return output.readNext();
		}
	}
}