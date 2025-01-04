package org.advent.year2021.day13;

import org.advent.common.AsciiLetters;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day13 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day13()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 17, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 753, "HZLEHJRK")
		);
	}
	
	Set<Point> points;
	List<Pair<String, Integer>> folds;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<List<String>> lines = Utils.splitByEmptyLine(Utils.readLines(input));
		points = lines.getFirst().stream().map(Point::parse).collect(Collectors.toSet());
		folds = lines.getLast().stream()
				.map(line -> StringUtils.removeStart(line, "fold along ").split("="))
				.map(s -> Pair.of(s[0], Integer.parseInt(s[1])))
				.toList();
	}
	
	@Override
	public Object part1() {
		return solve(points, folds.subList(0, 1)).size();
	}
	
	@Override
	public Object part2() {
		return AsciiLetters.parse(solve(points, folds));
	}
	
	Set<Point> solve(Set<Point> points, List<Pair<String, Integer>> folds) {
		for (Pair<String, Integer> fold : folds)
			points = fold(points, fold.left(), fold.right());
		return points;
	}
	
	Set<Point> fold(Set<Point> points, String axis, int position) {
		Function<Point, Point> mapping = "y".equals(axis)
				? p -> p.y() < position ? p : p.shift(0, -2 * (p.y() - position))
				: p -> p.x() < position ? p : p.shift(- 2 * (p.x() - position), 0);
		return points.stream().map(mapping).collect(Collectors.toSet());
	}
}