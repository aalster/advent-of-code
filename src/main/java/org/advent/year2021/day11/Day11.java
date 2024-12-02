package org.advent.year2021.day11;

import org.advent.common.DirectionExt;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Day11 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day11.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static long part1(List<String> lines) {
		Field field = Field.parse(lines);
		
		long flashes = 0;
		for (int i = 0; i < 100; i++) {
			flashes += field.step();
		}
		return flashes;
	}
	
	private static long part2(List<String> lines) {
		Field field = Field.parse(lines);
		for (int i = 0; i < 1000; i++) {
			field.step();
			Set<Integer> values = new HashSet<>(field.field().values());
			if (values.size() == 1 && values.contains(0))
				return i + 1;
		}
		return 0;
	}
	
	private record Field(Map<Point, Integer> field, int width, int height) {
		
		private int step() {
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