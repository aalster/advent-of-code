package org.advent.year2021.day2;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day2 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day2()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 150, 900),
				new ExpectedAnswers("input.txt", 1936494, 1997106066)
		);
	}
	
	List<Movement> movements;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		movements = Utils.readLines(input).stream().map(Movement::parse).toList();
	}
	
	@Override
	public Object part1() {
		Point position = new Point(0, 0);
		for (Movement movement : movements)
			position = position.move(movement.direction, movement.value);
		return position.x() * position.y();
	}
	
	@Override
	public Object part2() {
		Point position = new Point(0, 0);
		int aim = 0;
		for (Movement movement : movements) {
			if (movement.direction == Direction.RIGHT) {
				position = position.shift(movement.value, movement.value * aim);
				continue;
			}
			aim += movement.value * (movement.direction == Direction.DOWN ? 1 : -1);
		}
		return position.x() * position.y();
	}
	
	record Movement(Direction direction, int value) {
		static Movement parse(String line) {
			String[] split = line.split(" ");
			Direction direction = switch (split[0]) {
				case "forward" -> Direction.RIGHT;
				case "down" -> Direction.DOWN;
				case "up" -> Direction.UP;
				default -> throw new IllegalArgumentException();
			};
			return new Movement(direction, Integer.parseInt(split[1]));
		}
	}
}