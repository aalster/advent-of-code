package org.advent.year2024.day10;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Day10 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day10.class, "input.txt");
		Map<Point, Integer> field = Point.readFieldMap(Utils.readLines(input)).entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() - '0'));
		
		System.out.println("Answer 1: " + solve(field, false));
		System.out.println("Answer 2: " + solve(field, true));
	}
	
	private static long solve(Map<Point, Integer> field, boolean uniquePaths) {
		Collector<Point, ?, Collection<Point>> collector = Collectors.toCollection(
				uniquePaths ? ArrayList::new : HashSet::new);
		
		int result = 0;
		for (Map.Entry<Point, Integer> entry : field.entrySet()) {
			if (entry.getValue() != 0)
				continue;
			
			Collection<Point> current = List.of(entry.getKey());
			int steps = 0;
			while (steps < 9) {
				steps++;
				int _steps = steps;
				current = current.stream()
						.flatMap(c -> Direction.stream().map(c::shift))
						.filter(p -> field.containsKey(p) && field.get(p) == _steps)
						.collect(collector);
			}
			result += current.size();
		}
		return result;
	}
}