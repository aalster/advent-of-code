package org.advent.year2025.day4;

import org.advent.common.DirectionExt;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SequencedSet;
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
		
		SequencedSet<Point> queue = new LinkedHashSet<>(rolls);
		int removedTotal = 0;
		while (!queue.isEmpty()) {
			Point current = queue.removeFirst();
			
			if (rollsNeighbors.get(current) < 4) {
				rollsNeighbors.remove(current);
				removedTotal++;
				for (DirectionExt direction : DirectionExt.values()) {
					Point neighbor = direction.shift(current);
					if (rollsNeighbors.computeIfPresent(neighbor, (k, v) -> v - 1) != null)
						queue.add(neighbor);
				}
			}
		}
		return removedTotal;
	}
}