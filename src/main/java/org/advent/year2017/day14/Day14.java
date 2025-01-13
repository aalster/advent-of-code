package org.advent.year2017.day14;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2017.day10.Day10;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day14 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day14()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 8108, 1242),
				new ExpectedAnswers("input.txt", 8222, 1086)
		);
	}
	
	String line;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		line = input.nextLine();
	}
	
	@Override
	public Object part1() {
		return IntStream.range(0, 128)
				.mapToObj(row -> hashToBinary(Day10.knotHash(line + "-" + row)))
				.mapToLong(hashBinary -> hashBinary.chars().filter(c -> c == '1').count())
				.sum();
	}
	
	@Override
	public Object part2() {
		List<String> squares = IntStream.range(0, 128)
				.mapToObj(row -> hashToBinary(Day10.knotHash(line + "-" + row)))
				.toList();
		Set<Point> points = new HashSet<>(Point.readField(squares).get('1'));
		int count = 0;
		while (!points.isEmpty()) {
			points.removeAll(extractGroup(points, points.iterator().next()));
			count++;
		}
		return count;
	}
	
	Set<Point> extractGroup(Set<Point> points, Point sample) {
		Set<Point> group = new HashSet<>();
		Set<Point> current = Set.of(sample);
		while (!current.isEmpty()) {
			group.addAll(current);
			current = current.stream()
					.flatMap(c -> Direction.stream().map(c::shift))
					.filter(points::contains)
					.filter(p -> !group.contains(p))
					.collect(Collectors.toSet());
		}
		return group;
	}
	
	String hashToBinary(String hash) {
		StringBuilder result = new StringBuilder();
		while (!hash.isEmpty()) {
			String part = Integer.toBinaryString(Integer.valueOf(hash.substring(0, 1), 16));
			result.append("0".repeat(4 - part.length())).append(part);
			hash = hash.substring(1);
		}
		return result.toString();
	}
}