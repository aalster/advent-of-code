package org.example.puzzle18;

import org.example.Utils;

import java.util.Arrays;
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

public class Puzzle18 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Puzzle18.class, "input.txt");
		Grid3D grid = new Grid3D();
		while (input.hasNext()) {
			grid.add(Point3D.parse(input.nextLine()));
		}
		System.out.println("Answer 1: " + countSides(grid));
		grid.cells.addAll(getTrappedPoints(grid));
		System.out.println("Answer 2: " + countSides(grid));
	}
	
	static Collection<Point3D> getTrappedPoints(Grid3D grid) {
		int minX = grid.min(Point3D::x);
		int maxX = grid.max(Point3D::x);
		int minY = grid.min(Point3D::y);
		int maxY = grid.max(Point3D::y);
		int minZ = grid.min(Point3D::z);
		int maxZ = grid.max(Point3D::z);
		
		Set<Point3D> innerPoints = IntStream.range(minX + 1, maxX)
				.boxed()
				.flatMap(x -> IntStream.range(minY + 1, maxY).mapToObj(y -> new IntPair(x, y)))
				.flatMap(pair -> IntStream.range(minZ + 1, maxZ).mapToObj(z -> new Point3D(pair.left, pair.right, z)))
				.filter(pair -> !grid.contains(pair))
				.collect(Collectors.toCollection(HashSet::new));
		
		Set<Point3D> waterPoints = Stream.of(
						getIntPairStream(minY, maxY, minZ, maxZ)
								.flatMap(pair -> Stream.of(new Point3D(minX, pair.left, pair.right), new Point3D(maxX, pair.left, pair.right))),
						getIntPairStream(minX, maxX, minZ, maxZ)
								.flatMap(pair -> Stream.of(new Point3D(pair.left, minY, pair.right), new Point3D(pair.left, maxY, pair.right))),
						getIntPairStream(minX, maxX, minY, maxY)
								.flatMap(pair -> Stream.of(new Point3D(pair.left, pair.right, minZ), new Point3D(pair.left, pair.right, maxZ))))
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
	
	static int countSides(Grid3D grid) {
		int minX = grid.min(Point3D::x);
		int maxX = grid.max(Point3D::x);
		int minY = grid.min(Point3D::y);
		int maxY = grid.max(Point3D::y);
		int minZ = grid.min(Point3D::z);
		int maxZ = grid.max(Point3D::z);
		
		int xSides = countSidesForDirection(grid,
				getIntPairStream(minY, maxY, minZ, maxZ),
				pair -> new Point3D(minX, pair.left, pair.right),
				cursor -> cursor.x <= maxX + 1,
				cursor -> cursor.shift(1, 0, 0)
		);
		
		int ySides = countSidesForDirection(grid,
				getIntPairStream(minX, maxX, minZ, maxZ),
				pair -> new Point3D(pair.left, minY, pair.right),
				cursor -> cursor.y <= maxY + 1,
				cursor -> cursor.shift(0, 1, 0)
		);
		
		int zSides = countSidesForDirection(grid,
				getIntPairStream(minX, maxX, minY, maxY),
				pair -> new Point3D(pair.left, pair.right, minZ),
				cursor -> cursor.z <= maxZ + 1,
				cursor -> cursor.shift(0, 0, 1)
		);
		
		return xSides + ySides + zSides;
	}
	
	private static int countSidesForDirection(Grid3D grid, Stream<IntPair> pairStream, Function<IntPair, Point3D> pointCreator,
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
	
	private static Stream<IntPair> getIntPairStream(int minLeft, int maxLeft, int minRight, int maxRight) {
		return IntStream.rangeClosed(minRight, maxRight)
				.boxed()
				.flatMap(right -> IntStream.rangeClosed(minLeft, maxLeft).mapToObj(left -> new IntPair(left, right)));
	}
	
	record IntPair(int left, int right) {
	}
	
	record Point3D(int x, int y, int z) {
		
		Point3D shift(int dx, int dy, int dz) {
			return new Point3D(x + dx, y + dy, z + dz);
		}
		
		boolean touches(Point3D p) {
			return
					(p.x == x && p.y == y && Math.abs(p.z - z) == 1) ||
					(p.x == x && p.z == z && Math.abs(p.y - y) == 1) ||
					(p.z == z && p.y == y && Math.abs(p.x - x) == 1);
		}
		
		static Point3D parse(String value) {
			int[] c = Arrays.stream(value.split(",")).mapToInt(Integer::parseInt).toArray();
			return new Point3D(c[0], c[1], c[2]);
		}
	}
	
	record Grid3D(Set<Point3D> cells) {
		Grid3D() {
			this(new HashSet<>());
		}
		
		void add(Point3D p) {
			cells.add(p);
		}
		
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