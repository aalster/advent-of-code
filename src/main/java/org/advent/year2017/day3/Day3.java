package org.advent.year2017.day3;

import org.advent.common.Direction;
import org.advent.common.DirectionExt;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day3 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day3()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 0, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 3, 23),
				new ExpectedAnswers("example3.txt", 2, 25),
				new ExpectedAnswers("example4.txt", 31, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 430, 312453)
		);
	}
	
	int target;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		target = input.nextInt();
	}
	
	@Override
	public Object part1() {
		int number = target;
		int ringDiameter = 1;
		while ((ringDiameter + 2) * (ringDiameter + 2) < number)
			ringDiameter += 2;
		
		number -= ringDiameter * ringDiameter;
		Point position = new Point((ringDiameter - 1) / 2, (ringDiameter - 1) / 2);
		
		if (number > 0) {
			position = position.shift(Direction.RIGHT);
			number--;
		}
		if (number > 0) {
			int distance = Math.min(number, ringDiameter);
			position = position.move(Direction.UP, distance);
			number -= distance;
		}
		Direction direction = Direction.UP;
		while (number > 0) {
			direction = direction.rotate(Direction.LEFT);
			int distance = Math.min(number, ringDiameter + 1);
			position = position.move(direction, distance);
			number -= distance;
		}
		return position.distanceTo(new Point(0, 0));
	}
	
	@Override
	public Object part2() {
		Point position = new Point(0, 0);
		int number = 1;
		Map<Point, Integer> numbers = new HashMap<>(Map.of(position, number));
		
		Direction direction = Direction.RIGHT;
		int targetDistance = 1;
		int currentDistance = 0;
		int rotationsLeft = 2;
		while (number <= target) {
			Point nextPosition = position.shift(direction);
			currentDistance++;
			if (currentDistance >= targetDistance) {
				currentDistance = 0;
				direction = direction.rotate(Direction.LEFT);
				rotationsLeft--;
				if (rotationsLeft <= 0) {
					rotationsLeft = 2;
					targetDistance++;
				}
			}
			
			position = nextPosition;
			number = DirectionExt.stream()
					.map(d -> d.shift(nextPosition))
					.mapToInt(p -> numbers.getOrDefault(p, 0))
					.sum();
			numbers.put(nextPosition, number);
		}
		return number;
	}
}