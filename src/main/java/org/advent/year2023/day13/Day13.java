package org.advent.year2023.day13;

import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day13 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day13()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 405, 400),
				new ExpectedAnswers("input.txt", 40006, 28627)
		);
	}
	
	List<Set<Point>> fields;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		fields = new ArrayList<>();
		Set<Point> field = new HashSet<>();
		int y = 0;
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty()) {
				fields.add(field);
				field = new HashSet<>();
				y = 0;
				continue;
			}
			for (int x = 0; x < line.length(); x++)
				if (line.charAt(x) == '#')
					field.add(new Point(x, y));
			y++;
		}
		if (!field.isEmpty())
			fields.add(field);
	}
	
	@Override
	public Object part1() {
		return solve(fields, 0);
	}
	
	@Override
	public Object part2() {
		return solve(fields, 1);
	}
	
	long solve(List<Set<Point>> fields, int differences) {
		return fields.stream()
				.mapToLong(field -> verticalColumnsSum(field, differences) + 100 * horizontalRowsSum(field, differences))
				.sum();
	}
	
	long verticalColumnsSum(Set<Point> field, int differences) {
		int maxX = Point.maxX(field);
		int result = 0;
		
		for (int x = 1; x <= maxX; x++) {
			int _x = x;
			int filterTo = x * 2;
			int filterFrom = filterTo - maxX - 1;
			
			Set<Point> subField = field.stream()
					.filter(p -> filterFrom <= p.x() && p.x() < filterTo)
					.collect(Collectors.toSet());
			
			Set<Point> mirrored = subField.stream()
					.map(p -> p.x() >= _x ? new Point(_x * 2 - p.x() - 1, p.y()) : p)
					.collect(Collectors.toSet());
			
			if (Math.abs(subField.size() - mirrored.size() * 2) == differences)
				result += x;
		}
		return result;
	}
	
	long horizontalRowsSum(Set<Point> field, int differences) {
		return verticalColumnsSum(field.stream().map(p -> new Point(p.y(), p.x())).collect(Collectors.toSet()), differences);
	}
}