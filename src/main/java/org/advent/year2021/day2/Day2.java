package org.advent.year2021.day2;

import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day2 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day2.class, "input.txt");
		List<Pair<Direction, Integer>> movements = new ArrayList<>();
		while (input.hasNext()) {
			String[] split = input.nextLine().split(" ");
			movements.add(new Pair<>(parseDirection(split[0]), Integer.parseInt(split[1])));
		}
		
		System.out.println("Answer 1: " + part1(movements));
		System.out.println("Answer 2: " + part2(movements));
	}
	
	private static Direction parseDirection(String value) {
		return switch (value) {
			case "forward" -> Direction.RIGHT;
			case "down" -> Direction.DOWN;
			case "up" -> Direction.UP;
			default -> throw new IllegalArgumentException();
		};
	}
	
	private static int part1(List<Pair<Direction, Integer>> movements) {
		Point position = new Point(0, 0);
		for (Pair<Direction, Integer> movement : movements)
			position = position.move(movement.left(), movement.right());
		return position.x() * position.y();
	}
	
	private static int part2(List<Pair<Direction, Integer>> movements) {
		Point position = new Point(0, 0);
		int aim = 0;
		for (Pair<Direction, Integer> movement : movements) {
			if (movement.left() == Direction.RIGHT) {
				position = position.shift(movement.right(), movement.right()* aim);
				continue;
			}
			aim += movement.right() * (movement.left() == Direction.DOWN ? 1 : -1);
		}
		return position.x() * position.y();
	}
}