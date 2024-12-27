package org.advent.year2015.day3;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Day3 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day3()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 4, 3),
				new ExpectedAnswers("example2.txt", 2, 11),
				new ExpectedAnswers("input.txt", 2592, 2360)
		);
	}
	
	String line;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		line = input.nextLine();
	}
	
	@Override
	public Object part1() {
		Point current = new Point(0, 0);
		Set<Point> visited = new HashSet<>(List.of(current));
		for (char direction : line.toCharArray()) {
			current = current.shift(Direction.parseSymbol(direction));
			visited.add(current);
		}
		return visited.size();
	}
	
	@Override
	public Object part2() {
		Point start = new Point(0, 0);
		Point[] current = new Point[] {start, start};
		Set<Point> visited = new HashSet<>(List.of(start));
		int turn = 0;
		for (char direction : line.toCharArray()) {
			current[turn] = current[turn].shift(Direction.parseSymbol(direction));
			visited.add(current[turn]);
			turn = (turn + 1) % 2;
		}
		return visited.size();
	}
}