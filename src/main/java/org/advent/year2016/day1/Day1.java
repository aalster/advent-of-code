package org.advent.year2016.day1;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.ExpectedAnswers;
import org.advent.runner.DayRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Day1 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day1()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 12, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 4),
				new ExpectedAnswers("input.txt", 253, 126)
		);
	}
	
	List<String> instructions;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(Day1.class, file);
		instructions = List.of(input.nextLine().split(", "));
	}
	
	@Override
	public Object part1() {
		Point start = new Point(0, 0);
		Point end = start;
		Direction direction = Direction.UP;
		for (String instruction : instructions) {
			direction = direction.rotate(Direction.parseLetter(instruction.charAt(0)));
			int length = Integer.parseInt(instruction.substring(1));
			end = end.move(direction, length);
		}
		return start.distanceTo(end);
	}
	
	@Override
	public Object part2() {
		Point start = new Point(0, 0);
		Set<Point> visited = new HashSet<>(Set.of(start));
		
		Point current = start;
		Direction direction = Direction.UP;
		for (String instruction : instructions) {
			direction = direction.rotate(Direction.parseLetter(instruction.charAt(0)));
			int length = Integer.parseInt(instruction.substring(1));
			while (length > 0) {
				length--;
				current = current.move(direction, 1);
				if (!visited.add(current))
					return start.distanceTo(current);
			}
		}
		return 0;
	}
}