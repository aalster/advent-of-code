package org.advent.year2024.day18;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day18 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day18()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 22, "6,1"),
				new ExpectedAnswers("input.txt", 360, "58,62")
		);
	}
	
	List<Point> bytes;
	Data data;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		bytes = Utils.readLines(input).stream().map(Point::parse).toList();
		data = switch (file) {
			case "example.txt" -> new Data(6, 12);
			case "input.txt" -> new Data(70, 1024);
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		return exitDistance(bytes, data.bytesFallen);
	}
	
	@Override
	public Object part2() {
		int min = 0;
		int max = bytes.size();
		while (min < max) {
			int mid = (min + max) / 2;
			int steps = exitDistance(bytes, mid);
			if (steps == 0)
				max = mid;
			else
				min = mid + 1;
		}
		return bytes.stream().skip(min - 1).findFirst().map(p -> p.x() + "," + p.y()).orElse("");
	}
	
	int exitDistance(List<Point> bytes, int bytesFallen) {
		Set<Point> corrupted = bytes.stream().limit(bytesFallen).collect(Collectors.toSet());
		Point start = new Point(0, 0);
		Point finish = new Point(data.fieldSize, data.fieldSize);
		Rect bounds = new Rect(start, finish);
		
		Set<Point> visited = new HashSet<>();
		Set<Point> current = Set.of(start);
		int steps = 0;
		while (!current.isEmpty()) {
			if (current.contains(finish))
				return steps;
			steps++;
			current = current.stream()
					.flatMap(p -> Direction.stream().map(p::shift))
					.filter(p -> !corrupted.contains(p))
					.filter(p -> !visited.contains(p))
					.filter(bounds::containsInclusive)
					.collect(Collectors.toSet());
			visited.addAll(current);
		}
		return 0;
	}
	
	record Data(int fieldSize, int bytesFallen) {
	}
}