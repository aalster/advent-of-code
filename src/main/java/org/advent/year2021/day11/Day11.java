package org.advent.year2021.day11;

import org.advent.common.DirectionExt;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Day11 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day11()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 1656, 195),
				new ExpectedAnswers("input.txt", 1773, 494)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
	}
	
	@Override
	public Object part1() {
		Field field = Field.parse(lines);
		
		long flashes = 0;
		for (int i = 0; i < 100; i++) {
			flashes += field.step();
		}
		return flashes;
	}
	
	@Override
	public Object part2() {
		Field field = Field.parse(lines);
		for (int i = 0; i < 1000; i++) {
			field.step();
			Set<Integer> values = new HashSet<>(field.field().values());
			if (values.size() == 1 && values.contains(0))
				return i + 1;
		}
		return 0;
	}
	
	record Field(Map<Point, Integer> field, int width, int height) {
		
		int step() {
			Set<Point> flashedOnCurrentStep = new HashSet<>(width * height);
			field.keySet().forEach(k -> field.computeIfPresent(k, (k2, v) -> v + 1));
			while (true) {
				List<Point> flashes = field.entrySet().stream().filter(e -> e.getValue() > 9).map(Map.Entry::getKey).toList();
				if (flashes.isEmpty())
					break;
				flashedOnCurrentStep.addAll(flashes);
				flashes.stream()
						.flatMap(p -> Arrays.stream(DirectionExt.values()).map(d -> d.shift(p)))
						.forEach(p -> field.computeIfPresent(p, (p2, v) -> v + 1));
				flashes.forEach(p -> field.put(p, 0));
			}
			flashedOnCurrentStep.forEach(p -> field.put(p, 0));
			return flashedOnCurrentStep.size();
		}
		
		static Field parse(List<String> lines) {
			Map<Point, Integer> field = new HashMap<>();
			int y = 0;
			for (String line : lines) {
				for (int x = 0; x < line.length(); x++)
					field.put(new Point(x, y), line.charAt(x) - '0');
				y++;
			}
			return new Field(field, lines.getFirst().length(), lines.size());
		}
	}
}