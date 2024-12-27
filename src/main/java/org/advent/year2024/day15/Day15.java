package org.advent.year2024.day15;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AbstractDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Day15 extends AbstractDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day15()).run("input.txt");
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", null, null),
				new ExpectedAnswers("input.txt", null, null)
		);
	}
	
	Map<Point, Character> field;
	String moves;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<String> fieldLines = new ArrayList<>();
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty())
				break;
			fieldLines.add(line);
		}
		field = Point.readFieldMap(fieldLines);
		moves = String.join("", Utils.readLines(input));
	}
	
	@Override
	public Object part1() {
		field = new HashMap<>(field);
		Point current = findStart(field);
		
		for (char c : moves.toCharArray()) {
			Direction d = Direction.parseSymbol(c);
			Point nextEmpty = nextEmpty(field, current, d);
			if (nextEmpty == null)
				continue;
			
			Point nextMove = current.move(d);
			if (!nextMove.equals(nextEmpty))
				field.put(nextEmpty, 'O');
			field.put(current, '.');
			field.put(nextMove, '@');
			current = nextMove;
		}
		return gps(field);
	}
	
	@Override
	public Object part2() {
		field = widenField(field);
		Point current = findStart(field);
		
		for (char c : moves.toCharArray()) {
			Direction d = Direction.parseSymbol(c);
			Point nextEmpty = nextEmpty(field, current, d);
			if (nextEmpty == null)
				continue;
			
			Point nextMove = current.move(d);
			if (!nextMove.equals(nextEmpty)) {
				Map<Point, Character> diff = diff(field, nextMove, d);
				if (diff == null)
					continue;
				field.putAll(diff);
			}
			field.put(current, '.');
			field.put(nextMove, '@');
			current = nextMove;
		}
		return gps(field);
	}
	
	Point nextEmpty(Map<Point, Character> field, Point current, Direction d) {
		while (field.get(current) != '.') {
			current = current.move(d);
			if (field.get(current) == '#')
				return null;
		}
		return current;
	}
	
	Map<Point, Character> diff(Map<Point, Character> field, Point currentPosition, Direction d) {
		Map<Point, Character> diff = new HashMap<>();
		Map<Point, Character> current = new HashMap<>();
		current.put(currentPosition, field.get(currentPosition));
		
		while (!current.isEmpty()) {
			for (Point point : new ArrayList<>(current.keySet())) {
				Point secondBoxPart = point.shift(field.get(point) == '[' ? Direction.RIGHT : Direction.LEFT);
				current.put(secondBoxPart, field.get(secondBoxPart));
			}
			
			Map<Point, Character> next = new HashMap<>();
			for (Map.Entry<Point, Character> entry : current.entrySet()) {
				diff.putIfAbsent(entry.getKey(), '.');
				Point nextPosition = entry.getKey().shift(d);
				diff.put(nextPosition, entry.getValue());
				
				if (!current.containsKey(nextPosition)) {
					Character nextObject = field.get(nextPosition);
					if (nextObject == '#')
						return null;
					if (nextObject == '[' || nextObject == ']')
						next.put(nextPosition, field.get(nextPosition));
				}
			}
			current = next;
		}
		return diff;
	}
	
	Point findStart(Map<Point, Character> field) {
		return field.entrySet().stream()
				.filter(e -> e.getValue() == '@')
				.map(Map.Entry::getKey)
				.findAny().orElseThrow();
	}
	
	int gps(Map<Point, Character> field) {
		Set<Character> boxChars = Set.of('O', '[');
		return field.entrySet().stream()
				.filter(e -> boxChars.contains(e.getValue()))
				.map(Map.Entry::getKey)
				.mapToInt(p -> p.y() * 100 + p.x())
				.sum();
	}
	
	Map<Point, Character> widenField(Map<Point, Character> field) {
		Map<Point, Character> wideField = new HashMap<>(field.size() * 2);
		for (Map.Entry<Point, Character> entry : field.entrySet()) {
			String cells = switch (entry.getValue()) {
				case '#' -> "##";
				case 'O' -> "[]";
				case '.' -> "..";
				case '@' -> "@.";
				default -> throw new IllegalStateException("Unexpected value: " + entry.getValue());
			};
			Point p = entry.getKey();
			wideField.put(new Point(p.x() * 2, p.y()), cells.charAt(0));
			wideField.put(new Point(p.x() * 2 + 1, p.y()), cells.charAt(1));
		}
		return wideField;
	}
}