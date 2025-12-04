package org.advent.year2025.day4;

import org.advent.common.DirectionExt;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day4 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day4()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 13, 43),
				new ExpectedAnswers("input.txt", 1349, 8277)
		);
	}
	
	Set<Point> rolls;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		rolls = new HashSet<>(Point.readField(Utils.readLines(input)).get('@'));
	}
	
	@Override
	public Object part1() {
		return rolls.stream()
				.filter(r -> DirectionExt.stream().map(d -> d.shift(r)).filter(rolls::contains).count() < 4)
				.count();
	}
	
	@Override
	public Object part2() {
		Map<Point, Integer> rollsNeighbors = rolls.stream()
				.collect(Collectors.toMap(r -> r,
						r -> (int) DirectionExt.stream().map(d -> d.shift(r)).filter(rolls::contains).count()));
		
		int removedTotal = 0;
		while (!rollsNeighbors.isEmpty()) {
			int removed = 0;
			for (Map.Entry<Point, Integer> entry : new ArrayList<>(rollsNeighbors.entrySet())) {
				if (entry.getValue() < 4) {
					Point current = entry.getKey();
					for (DirectionExt direction : DirectionExt.values())
						rollsNeighbors.computeIfPresent(direction.shift(current), (k, v) -> v - 1);
					rollsNeighbors.remove(current);
					removed++;
				}
			}
			if (removed == 0)
				break;
			removedTotal += removed;
		}
		return removedTotal;
	}
}