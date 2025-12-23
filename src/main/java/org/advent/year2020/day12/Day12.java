package org.advent.year2020.day12;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day12 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day12()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 25, 286),
				new ExpectedAnswers("input.txt", 1441, 61616)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
	}
	
	@Override
	public Object part1() {
		Point position = Point.ZERO;
		Direction direction = Direction.RIGHT;
		
		for (String line : lines) {
			char action = line.charAt(0);
			int value = Integer.parseInt(line.substring(1));
			
			if (action == 'L') {
				for (int i = (value / 90) % 4; i > 0; i--)
					direction = direction.rotate(Direction.LEFT);
			} else if (action == 'R') {
				for (int i = (value / 90) % 4; i > 0; i--)
					direction = direction.rotate(Direction.RIGHT);
			} else {
				Direction currentDirection = action == 'F' ? direction : Direction.parseCompassLetter(action);
				position = position.move(currentDirection, value);
			}
		}
		return position.distanceTo(Point.ZERO);
	}
	
	@Override
	public Object part2() {
		Point position = Point.ZERO;
		Point waypoint = new Point(10, -1);
		
		for (String line : lines) {
			char action = line.charAt(0);
			int value = Integer.parseInt(line.substring(1));
			
			if (action == 'L') {
				for (int i = (value / 90) % 4; i > 0; i--)
					waypoint = rotateLeft(waypoint);
			} else if (action == 'R') {
				for (int i = (value / 90) % 4; i > 0; i--)
					waypoint = rotateRight(waypoint);
			} else if (action == 'F') {
				position = position.shift(waypoint.scale(value));
			} else {
				waypoint = waypoint.move(Direction.parseCompassLetter(action), value);
			}
		}
		return position.distanceTo(Point.ZERO);
	}
	
	Point rotateLeft(Point p) {
		return new Point(p.y(), -p.x());
	}
	
	Point rotateRight(Point p) {
		return new Point(-p.y(), p.x());
	}
}