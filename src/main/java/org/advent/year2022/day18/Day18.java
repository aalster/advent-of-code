package org.advent.year2022.day18;

import org.advent.common.IntPair;
import org.advent.common.Point3D;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day18 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day18()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 64, 58),
				new ExpectedAnswers("input.txt", 4314, 2444)
		);
	}
	
	Set<Point3D> points;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		points = Utils.readLines(input).stream().map(Point3D::parse).collect(Collectors.toSet());
	}
	
	@Override
	public Object part1() {
		return countSides(new Grid3D(points));
	}
	
	@Override
	public Object part2() {
		Grid3D grid = new Grid3D(points);
		grid.cells.addAll(getTrappedPoints(grid));
		return countSides(grid);
	}
	
	Collection<Point3D> getTrappedPoints(Grid3D grid) {
		int minX = grid.min(Point3D::x);
		int maxX = grid.max(Point3D::x);
		int minY = grid.min(Point3D::y);
		int maxY = grid.max(Point3D::y);
		int minZ = grid.min(Point3D::z);
		int maxZ = grid.max(Point3D::z);
		
		Set<Point3D> innerPoints = IntStream.range(minX + 1, maxX)
				.boxed()
				.flatMap(x -> IntStream.range(minY + 1, maxY).mapToObj(y -> new IntPair(x, y)))
				.flatMap(pair -> IntStream.range(minZ + 1, maxZ).mapToObj(z -> new Point3D(pair.left(), pair.right(), z)))
				.filter(pair -> !grid.contains(pair))
				.collect(Collectors.toCollection(HashSet::new));
		
		Set<Point3D> waterPoints = Stream.of(
						getIntPairStream(minY, maxY, minZ, maxZ)
								.flatMap(pair -> Stream.of(new Point3D(minX, pair.left(), pair.right()), new Point3D(maxX, pair.left(), pair.right()))),
						getIntPairStream(minX, maxX, minZ, maxZ)
								.flatMap(pair -> Stream.of(new Point3D(pair.left(), minY, pair.right()), new Point3D(pair.left(), maxY, pair.right()))),
						getIntPairStream(minX, maxX, minY, maxY)
								.flatMap(pair -> Stream.of(new Point3D(pair.left(), pair.right(), minZ), new Point3D(pair.left(), pair.right(), maxZ))))
				.flatMap(s -> s)
				.filter(p -> !grid.contains(p))
				.collect(Collectors.toCollection(HashSet::new));
		
		for (Point3D water : waterPoints) {
			Set<Point3D> touching = Set.of(water);
			while (!touching.isEmpty()) {
				Set<Point3D> currentTouching = touching;
				touching = innerPoints.stream()
						.filter(inner -> currentTouching.stream().anyMatch(inner::touches))
						.collect(Collectors.toSet());
				innerPoints.removeAll(touching);
			}
		}
		
		return innerPoints;
	}
	
	int countSides(Grid3D grid) {
		int minX = grid.min(Point3D::x);
		int maxX = grid.max(Point3D::x);
		int minY = grid.min(Point3D::y);
		int maxY = grid.max(Point3D::y);
		int minZ = grid.min(Point3D::z);
		int maxZ = grid.max(Point3D::z);
		
		int xSides = countSidesForDirection(grid,
				getIntPairStream(minY, maxY, minZ, maxZ),
				pair -> new Point3D(minX, pair.left(), pair.right()),
				cursor -> cursor.x() <= maxX + 1,
				cursor -> cursor.shift(1, 0, 0)
		);
		
		int ySides = countSidesForDirection(grid,
				getIntPairStream(minX, maxX, minZ, maxZ),
				pair -> new Point3D(pair.left(), minY, pair.right()),
				cursor -> cursor.y() <= maxY + 1,
				cursor -> cursor.shift(0, 1, 0)
		);
		
		int zSides = countSidesForDirection(grid,
				getIntPairStream(minX, maxX, minY, maxY),
				pair -> new Point3D(pair.left(), pair.right(), minZ),
				cursor -> cursor.z() <= maxZ + 1,
				cursor -> cursor.shift(0, 0, 1)
		);
		
		return xSides + ySides + zSides;
	}
	
	int countSidesForDirection(Grid3D grid, Stream<IntPair> pairStream, Function<IntPair, Point3D> pointCreator,
	                                          Predicate<Point3D> loopCondition, UnaryOperator<Point3D> cursorShifter) {
		return pairStream
				.mapToInt(pair -> {
					Point3D cursor = pointCreator.apply(pair);
					int sides = 0;
					boolean filled = false;
					while (loopCondition.test(cursor)) {
						boolean nextFilled = grid.contains(cursor);
						sides += filled != nextFilled ? 1 : 0;
						filled = nextFilled;
						cursor = cursorShifter.apply(cursor);
					}
					return sides;
				})
				.sum();
	}
	
	Stream<IntPair> getIntPairStream(int minLeft, int maxLeft, int minRight, int maxRight) {
		return IntStream.rangeClosed(minRight, maxRight)
				.boxed()
				.flatMap(right -> IntStream.rangeClosed(minLeft, maxLeft).mapToObj(left -> new IntPair(left, right)));
	}
	
	
	record Grid3D(Set<Point3D> cells) {
		
		boolean contains(Point3D p) {
			return cells().contains(p);
		}
		
		int min(ToIntFunction<Point3D> mapper) {
			return cells.stream().mapToInt(mapper).min().orElse(0);
		}
		
		int max(ToIntFunction<Point3D> mapper) {
			return cells.stream().mapToInt(mapper).max().orElse(0);
		}
	}
}