package org.advent.year2016.day2;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.ExpectedAnswers;
import org.advent.runner.DayRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day2 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day2()).runAll();
//		new DayRunner(new Day2()).run("input.txt");
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 1985, "5DB3"),
				new ExpectedAnswers("input.txt", 47978, "659AD")
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		lines = Utils.readLines(Utils.scanFileNearClass(getClass(), file));
	}
	
	@Override
	public Object part1() {
		List<String> buttons = List.of(
				"123",
				"456",
				"789");
		return Integer.parseInt(solve(new Point(1, 1), buttons(buttons)));
	}
	
	@Override
	public Object part2() {
		List<String> buttons = List.of(
				"  1",
				" 234",
				"56789",
				" ABC",
				"  D");
		return solve(new Point(0, 2), buttons(buttons));
	}
	
	private String solve(Point start, Map<Point, Character> buttons) {
		StringBuilder code = new StringBuilder();
		Point current = start;
		for (String line : lines) {
			for (char c : line.toCharArray()) {
				Point next = current.shift(Direction.parseLetter(c));
				if (buttons.containsKey(next))
					current = next;
			}
			code.append(buttons.get(current));
		}
		return code.toString();
	}
	
	private Map<Point, Character> buttons(List<String> lines) {
		return Point.readFieldMap(lines).entrySet().stream()
				.filter(e -> e.getValue() != ' ')
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}