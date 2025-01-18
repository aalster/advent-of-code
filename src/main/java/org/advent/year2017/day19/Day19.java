package org.advent.year2017.day19;

import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day19 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day19()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "ABCDEF", 38),
				new ExpectedAnswers("input.txt", "NDWHOYRUEA", 17540)
		);
	}
	
	Map<Point, Character> field;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		field = Point.readFieldMap(Utils.readLines(input));
	}
	
	@Override
	public Object part1() {
		return solve().left();
	}
	
	@Override
	public Object part2() {
		return solve().right();
	}
	
	private Pair<String, Integer> solve() {
		Point current = field.entrySet().stream()
				.filter(e -> e.getKey().y() == 0 && e.getValue() != ' ')
				.map(Map.Entry::getKey)
				.findFirst()
				.orElseThrow();
		Direction direction = Direction.DOWN;
		StringBuilder path = new StringBuilder();
		int steps = 1;
		
		main: while (true) {
			Point next = current.shift(direction);
			Character cell = field.getOrDefault(next, ' ');
			if (cell == ' ') {
				for (Direction rotation : List.of(Direction.LEFT, Direction.RIGHT)) {
					Direction nextDirection = direction.rotate(rotation);
					if (field.getOrDefault(current.shift(nextDirection), ' ') != ' ') {
						direction = nextDirection;
						continue main;
					}
				}
				break;
			}
			if (Character.isLetter(cell))
				path.append(cell);
			
			current = next;
			steps++;
		}
		return Pair.of(path.toString(), steps);
	}
}