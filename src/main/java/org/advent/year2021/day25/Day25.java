package org.advent.year2021.day25;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day25 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day25.class, "input.txt");
		Map<Character, List<Point>> field = Point.readField(Utils.readLines(input));
		
		System.out.println("Answer: " + part1(field));
	}
	
	private static long part1(Map<Character, List<Point>> field) {
		int maxX = Point.maxX(field.get('.'));
		int maxY = Point.maxY(field.get('.'));
		Map<Point, Direction> cucumbers = new HashMap<>();
		for (Point p : field.get('>'))
			cucumbers.put(p, Direction.RIGHT);
		for (Point p : field.get('v'))
			cucumbers.put(p, Direction.DOWN);
		
		List<Direction> directions = List.of(Direction.RIGHT, Direction.DOWN);
		
		int steps = 0;
		boolean moving = true;
		while (moving) {
			moving = false;
			for (Direction direction : directions) {
				Map<Point, Direction> nextCucumbers = new HashMap<>();
				for (Map.Entry<Point, Direction> entry : cucumbers.entrySet()) {
					if (entry.getValue() != direction) {
						nextCucumbers.put(entry.getKey(), entry.getValue());
						continue;
					}
					Point position = entry.getKey();
					Point nextPosition = position.shift(direction);
					if (nextPosition.x() > maxX)
						nextPosition = new Point(0, nextPosition.y());
					if (nextPosition.y() > maxY)
						nextPosition = new Point(nextPosition.x(), 0);
					
					boolean occupied = cucumbers.containsKey(nextPosition);
					nextCucumbers.put(occupied ? position : nextPosition, direction);
					moving = moving || !occupied;
				}
				cucumbers = nextCucumbers;
			}
			steps++;
		}
		
		return steps;
	}
}