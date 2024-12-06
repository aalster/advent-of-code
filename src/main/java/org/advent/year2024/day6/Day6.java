package org.advent.year2024.day6;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Day6 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day6.class, "input.txt");
		Map<Character, List<Point>> field = Point.readField(Utils.readLines(input));
		Rect bounds = Point.bounds(field.get('.'));
		Set<Point> obstacles = new HashSet<>(field.get('#'));
		Point start = field.get('^').getFirst();
		Direction direction = Direction.UP;
		
		System.out.println("Answer 1: " + part1(bounds, obstacles, start, direction));
		System.out.println("Answer 2: " + part2(bounds, obstacles, start, direction));
	}
	
	private static long part1(Rect bounds, Set<Point> obstacles, Point start, Direction direction) {
		return leaveFieldSteps(bounds, obstacles, new HashMap<>(), start, direction);
	}
	
	private static long part2(Rect bounds, Set<Point> obstacles, Point start, Direction direction) {
		Map<Point, Set<Direction>> visited = new HashMap<>();
		Point current = start;
		Set<Point> loopPlaces = new HashSet<>();
		while (bounds.containsInclusive(current)) {
			Direction turnRight = direction.rotate(Direction.RIGHT);
			Point next = current.move(direction);
			if (obstacles.contains(next)) {
				direction = turnRight;
				continue;
			}
			
			if (!visited.containsKey(next)) {
				Set<Point> nextObstacles = new HashSet<>(obstacles);
				nextObstacles.add(next);
				if (leaveFieldSteps(bounds, nextObstacles, visited, current, turnRight) == -1)
					loopPlaces.add(next);
			}
			
			visited.computeIfAbsent(current, k -> new HashSet<>()).add(direction);
			current = next;
		}
		return loopPlaces.size();
	}
	
	private static long leaveFieldSteps(Rect bounds, Set<Point> obstacles, Map<Point, Set<Direction>> visitedBefore, Point current, Direction direction) {
		Map<Point, Set<Direction>> visited = new HashMap<>();
		for (Map.Entry<Point, Set<Direction>> entry : visitedBefore.entrySet())
			visited.put(entry.getKey(), new HashSet<>(entry.getValue()));
		
		while (bounds.containsInclusive(current)) {
			if (visited.getOrDefault(current, Set.of()).contains(direction))
				return -1;
			
			visited.computeIfAbsent(current, k -> new HashSet<>()).add(direction);
			
			Point next = current.move(direction);
			if (obstacles.contains(next))
				direction = direction.rotate(Direction.RIGHT);
			else
				current = next;
		}
		return visited.size();
	}
}