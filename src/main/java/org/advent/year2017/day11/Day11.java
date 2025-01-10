package org.advent.year2017.day11;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day11 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day11()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 3, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 0, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 2, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example4.txt", 3, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 764, 1532)
		);
	}
	
	List<String> directions;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		directions = Arrays.stream(input.nextLine().split(",")).toList();
	}
	
	@Override
	public Object part1() {
		HexPosition position = new HexPosition();
		for (String direction : directions)
			position.step(direction);
		return position.distance;
	}
	
	@Override
	public Object part2() {
		HexPosition position = new HexPosition();
		int maxDistance = 0;
		for (String direction : directions)
			maxDistance = Math.max(maxDistance, position.step(direction));
		return maxDistance;
	}
	
	static class HexPosition {
		static final Map<String, Integer> indexesClockwise = Map.of(
				"n", 0,
				"ne", 1,
				"se", 2,
				"s", 3,
				"sw", 4,
				"nw", 5);
		
		final int[] directions = new int[6];
		int distance = 0;
		
		int step(String direction) {
			int index = indexesClockwise.get(direction);
			if (getDirection(index + 3) > 0) {
				updateDirection(index + 3, -1);
				distance--;
			} else if (getDirection(index + 2) > 0) {
				updateDirection(index + 2, -1);
				updateDirection(index + 1, 1);
			} else if (getDirection(index - 2) > 0) {
				updateDirection(index - 2, -1);
				updateDirection(index - 1, 1);
			} else {
				updateDirection(index, 1);
				distance++;
			}
			return distance;
		}
		
		int getDirection(int index) {
			return directions[(index + 6) % 6];
		}
		
		void updateDirection(int index, int delta) {
			directions[(index + 6) % 6] += delta;
		}
	}
}