package org.advent.year2017.day21;

import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day21 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day21()).runAll();
	}
	
	final PointsPattern start = PointsPattern.parsePattern(".#./..#/###");
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 12, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 167, 2425195)
		);
	}
	
	Map<PointsPattern, PointsPattern> rules;
	int iterations;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		rules = new HashMap<>();
		for (String line : Utils.readLines(input)) {
			String[] split = line.split(" => ");
			PointsPattern value = PointsPattern.parsePattern(split[1]);
			for (PointsPattern keyVariant : PointsPattern.parsePattern(split[0]).allVariants())
				rules.put(keyVariant, value);
		}
		iterations = switch (file) {
			case "example.txt" -> 2;
			case "input.txt" -> 5;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		return start.enhanceSize(new HashMap<>(), rules, iterations);
	}
	
	@Override
	public Object part2() {
		return start.enhanceSize(new HashMap<>(), rules, 18);
	}
	
	record PointsPattern(Set<Point> points, int size) {
		
		public int enhanceSize(Map<Set<Point>, List<PointsPattern>> triplesCache, Map<PointsPattern, PointsPattern> rules, int iterations) {
			// Каждые 3 хода блок 3x3 превращается в 9x9, у которого каждая часть 3x3 считается независимо
			if (size == 3 && iterations >= 3) {
				List<PointsPattern> cachedNext = triplesCache.get(points);
				if (cachedNext == null) {
					PointsPattern triple = enhance(rules).enhance(rules).enhance(rules);
					Map<Point, Set<Point>> squares = splitToSquares(triple.points, 3);
					cachedNext = squares.values().stream().map(p -> new PointsPattern(p, 3)).toList();
					triplesCache.put(points, cachedNext);
				}
				return cachedNext.stream().mapToInt(p -> p.enhanceSize(triplesCache, rules, iterations - 3)).sum();
			}
			
			PointsPattern current = this;
			for (int i = 0; i < iterations; i++)
				current = current.enhance(rules);
			return current.points.size();
		}
		
		public PointsPattern enhance(Map<PointsPattern, PointsPattern> rules) {
			int squareSize = size % 2 == 0 ? 2 : 3;
			Map<Point, Set<Point>> squares = splitToSquares(points, squareSize);
			
			Set<Point> result = new HashSet<>();
			for (int y = 0; y < size / squareSize; y++) {
				for (int x = 0; x < size / squareSize; x++) {
					Point squareIndex = new Point(x, y);
					Point newSquarePosition = squareIndex.scale(squareSize + 1);
					PointsPattern key = new PointsPattern(squares.getOrDefault(squareIndex, Set.of()), squareSize);
					result.addAll(rules.get(key).points.stream().map(newSquarePosition::shift).toList());
				}
			}
			return new PointsPattern(result, size / squareSize * (squareSize + 1));
		}
		
		private Map<Point, Set<Point>> splitToSquares(Set<Point> points, int squareSize) {
			Map<Point, Set<Point>> squares = new HashMap<>();
			for (Point p : points)
				squares.computeIfAbsent(new Point(p.x() / squareSize, p.y() / squareSize), k -> new HashSet<>())
						.add(new Point(p.x() % squareSize, p.y() % squareSize));
			return squares;
		}
		
		Set<PointsPattern> allVariants() {
			int maxCoordinate = size - 1;
			List<Set<Point>> variants = new ArrayList<>();
			variants.add(points);
			variants.add(points.stream().map(p1 -> new Point(maxCoordinate - p1.x(), p1.y())).collect(Collectors.toSet()));
			
			Set<Point> rotated = points;
			for (int i = 0; i < 3; i++) {
				rotated = rotated.stream().map(p -> new Point(maxCoordinate - p.y(), p.x())).collect(Collectors.toSet());
				variants.add(rotated);
				variants.add(rotated.stream().map(p -> new Point(maxCoordinate - p.x(), p.y())).collect(Collectors.toSet()));
			}
			return variants.stream().map(p -> new PointsPattern(p, size)).collect(Collectors.toSet());
		}
		
		static PointsPattern parsePattern(String pattern) {
			List<String> lines = List.of(pattern.split("/"));
			Set<Point> points = new HashSet<>(Point.readField(lines).getOrDefault('#', List.of()));
			return new PointsPattern(points, lines.size());
		}
	}
}