package org.advent.year2016.day13;

import org.advent.common.Direction;
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

public class Day13 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day13()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 11, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 82, 138)
		);
	}
	
	int favoriteNumber;
	Point target;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		favoriteNumber = input.nextInt();
		target = switch (file) {
			case "example.txt" -> new Point(7, 4);
			case "input.txt" -> new Point(31,39);
			default -> throw new IllegalArgumentException("Unknown file " + file);
		};
	}
	
	@Override
	public Object part1() {
		Maze maze = new Maze(favoriteNumber);
		Set<Point> currents = Set.of(new Point(1, 1));
		Set<Point> visited = new HashSet<>();
		int step = 0;
		while (!currents.contains(target)) {
			step++;
			visited.addAll(currents);
			currents = currents.stream()
					.flatMap(c -> Direction.stream().map(c::shift))
					.filter(maze::free)
					.filter(n -> !visited.contains(n))
					.collect(Collectors.toSet());
		}
		return step;
	}
	
	@Override
	public Object part2() {
		Maze maze = new Maze(favoriteNumber);
		Set<Point> currents = Set.of(new Point(1, 1));
		Set<Point> visited = new HashSet<>();
		int step = 50;
		while (step >= 0) {
			step--;
			visited.addAll(currents);
			currents = currents.stream()
					.flatMap(c -> Direction.stream().map(c::shift))
					.filter(maze::free)
					.filter(n -> !visited.contains(n))
					.collect(Collectors.toSet());
		}
		return visited.size();
	}
	
	record Maze(int favoriteNumber, Map<Point, Boolean> freeCells) {
		
		Maze(int favoriteNumber) {
			this(favoriteNumber, new HashMap<>());
		}
		
		boolean free(Point p) {
			return p.x() >= 0 && p.y() >= 0 && freeCells.computeIfAbsent(p, k -> calcIsFree(k.x(), k.y()));
		}
		
		boolean calcIsFree(int x, int y) {
			return Integer.bitCount((x + 3) * x + 2 * x * y + (y + 1) * y + favoriteNumber) % 2 == 0;
		}
	}
}