package org.advent.year2023.day18;

import org.advent.common.BigPoint;
import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day18 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day18.class, "input.txt");
		List<String> lines = new ArrayList<>();
		while (input.hasNext()) {
			lines.add(input.nextLine());
		}
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static long part1(List<String> lines) {
		return solveNaive(lines.stream().map(Move::parse1).toList());
	}
	
	private static BigInteger part2(List<String> lines) {
		return solveFast(lines.stream().map(Move::parse1).toList());
	}
	
	private static int solveNaive(List<Move> moves) {
		Set<Point> path = new HashSet<>();
		Point current = new Point(0, 0);
		path.add(current);
		for (Move move : moves) {
			for (long i = 0; i < move.meters(); i++) {
				current = current.shift(move.direction());
				path.add(current);
			}
		}
		Set<Point> middle = path.stream()
				.collect(Collectors.groupingBy(Point::y))
				.values().stream()
				.filter(row -> row.size() == 2)
				.map(row -> Pair.of(row.getFirst(), row.getLast()))
				.filter(p -> Math.abs(p.left().x() - p.right().x()) > 2)
				.map(p -> new Point((p.left().x() + p.right().x()) / 2, p.left().y()))
				.collect(Collectors.toSet());
		
		Set<Point> inside = new HashSet<>();
		
		while (!middle.isEmpty()) {
			middle = middle.stream()
					.flatMap(p -> Direction.stream().map(p::shift))
					.filter(p -> !inside.contains(p))
					.filter(p -> !path.contains(p))
					.collect(Collectors.toSet());
			inside.addAll(middle);
		}

//		Point.printField(Stream.of(path, inside).flatMap(Set::stream).collect(Collectors.toSet()), '#', '.');
		return path.size() + inside.size();
	}
	
	private static BigInteger solveFast(List<Move> moves) {
		BigPoint current = new BigPoint(BigInteger.ZERO, BigInteger.ZERO);
		Set<Line> lines = new HashSet<>();
		for (Move move : moves) {
			BigPoint next = current.shift(move.direction(), BigInteger.valueOf(move.meters()));
			lines.add(Line.of(current, next));
			current = next;
		}
		Shape shape = Shape.of(lines);
//		shape.print();
		BigInteger result = BigInteger.ZERO;
		while (!shape.lines().isEmpty()) {
			BigInteger area = shape.cutTop();
			result = result.add(area);
//			System.out.println("\nCut " + area + ": ");
//			shape.print();
		}
		
		return result;
	}
	
	record Line(BigPoint start, BigPoint end, boolean horizontal) {
		
		Line shift(Direction direction, BigInteger amount) {
			return new Line(start.shift(direction, amount), end.shift(direction, amount), horizontal);
		}
		
		BigInteger length() {
			return BigInteger.ONE.add(horizontal ? end.x().subtract(start.x()) : end.y().subtract(start.y()));
		}
		
		BigInteger intersectionLength(Line l) {
			if (l.horizontal != horizontal)
				throw new RuntimeException("Not supported");
			if (horizontal && start.y().compareTo(l.start.y()) == 0)
				return min(end.x(), l.end.x()).subtract(max(start.x(), l.start.x())).add(BigInteger.ONE);
			else if (!horizontal && start.x().compareTo(l.start.x()) == 0)
				return min(end.y(), l.end.y()).subtract(max(start.y(), l.start.y())).add(BigInteger.ONE);
			throw new RuntimeException("Not supported");
		}
		
		Stream<BigPoint> containingPoints() {
			return Stream.iterate(start, current -> !current.equals(end),
					bigPoint -> bigPoint.shift(horizontal ? Direction.RIGHT : Direction.DOWN, BigInteger.ONE));
		}
		
		Line combine(Line l) {
			if (l.horizontal != horizontal)
				throw new IllegalArgumentException("Different orientation");
			List<BigPoint> points = Stream.of(start, end, l.start, l.end)
					.collect(Collectors.toMap(p -> p, p -> 1, Integer::sum))
					.entrySet().stream()
					.filter(e -> e.getValue() == 1)
					.map(Map.Entry::getKey)
					.toList();
			if (points.size() != 2)
				throw new IllegalArgumentException("Lines don't touch");
			return Line.of(points.getFirst(), points.getLast());
		}
		
		static BigInteger lengthSum(Collection<Line> lines) {
			return lines.stream().map(Line::length).reduce(BigInteger.ZERO, BigInteger::add);
		}
		
		static Line of(BigPoint start, BigPoint end) {
			if (start.equals(end))
				throw new IllegalArgumentException("Zero line");
			if (start.x().compareTo(end.x()) != 0 && start.y().compareTo(end.y()) != 0)
				throw new IllegalArgumentException("Line must be vertical or horizontal");
			
			boolean horizontal = start.y().compareTo(end.y()) == 0;
			if (start.x().add(start.y()).compareTo(end.x().add(end.y())) < 0)
				return new Line(start, end, horizontal);
			else
				return new Line(end, start, horizontal);
		}
	}
	
	record Shape(Set<Line> lines, Map<BigPoint, List<Line>> edges) {
		
		Line otherLine(BigPoint edge, Line exclude) {
			List<Line> list = edges.getOrDefault(edge, List.of()).stream().filter(l -> !l.equals(exclude)).toList();
			if (list.size() != 1) {
				System.out.println(edges.entrySet().stream()
						.map(e -> e.getKey() + ": " + e.getValue())
						.collect(Collectors.joining("\n")));
				throw new RuntimeException("Bad edge " + edge + ": " + list);
			}
			return list.getFirst();
		}
		
		BigInteger cutTop() {
			Line top = lines.stream()
					.filter(Line::horizontal)
					.min(Comparator.comparing(l -> l.start().y()))
					.orElseThrow(() -> new RuntimeException("Top not found"));
			Line left = otherLine(top.start(), top);
			Line right = otherLine(top.end(), top);
			
			if (left.end().y().compareTo(right.end().y()) == 0) {
				Line nextLeft = otherLine(left.end(), left);
				Line nextRight = otherLine(right.end(), right);
				if (nextLeft.equals(nextRight)) {
					replaceLines(List.of(), lines.stream().toList());
					return top.length().multiply(left.length());
				} else {
					Line nextTop = top.shift(Direction.DOWN, left.length().subtract(BigInteger.ONE)).combine(nextLeft).combine(nextRight);
					if (checkIntersection(nextTop, Set.of(top, left, right, nextLeft, nextRight))) {
						rotateRight();
						return BigInteger.ZERO;
					}
					replaceLines(List.of(nextTop), List.of(top, left, right, nextLeft, nextRight));
					return top.length().multiply(left.length().subtract(BigInteger.ONE))
							.add(top.shift(Direction.DOWN, left.length().subtract(BigInteger.ONE)).intersectionLength(nextLeft).subtract(BigInteger.ONE))
							.add(top.shift(Direction.DOWN, right.length().subtract(BigInteger.ONE)).intersectionLength(nextRight).subtract(BigInteger.ONE));
				}
			}
			Line min = left.end().y().compareTo(right.end().y()) < 0 ? left : right;
			Line max = min == left ? right : left;
			Line maxLeftover = Line.of(max.start().shift(Direction.DOWN, min.length().subtract(BigInteger.ONE)), max.end());
			Line nextMin = otherLine(min.end(), min);
			Line nextTop = top.shift(Direction.DOWN, min.length().subtract(BigInteger.ONE)).combine(nextMin);
			if (checkIntersection(nextTop, Set.of(top, left, right, nextMin))) {
				rotateRight();
				return BigInteger.ZERO;
			}
			replaceLines(List.of(nextTop, maxLeftover), List.of(top, left, right, nextMin));
			return top.length().multiply(min.length().subtract(BigInteger.ONE))
					.add(top.shift(Direction.DOWN, min.length().subtract(BigInteger.ONE)).intersectionLength(nextMin).subtract(BigInteger.ONE));
		}
		
		void replaceLines(List<Line> add, List<Line> remove) {
			remove.forEach(lines::remove);
			lines.addAll(add);
			for (Line line : remove)
				Stream.of(line.start(), line.end()).forEach(edge -> edges.get(edge).remove(line));
			for (Line line : add)
				Stream.of(line.start(), line.end()).forEach(edge -> edges.computeIfAbsent(edge, k -> new ArrayList<>()).add(line));
		}
		
		boolean checkIntersection(Line nextTop, Set<Line> exclude) {
			return lines.stream()
					.filter(l -> !exclude.contains(l))
					.flatMap(l -> Stream.of(l.start, l.end))
					.filter(p -> p.y().compareTo(nextTop.start.y()) < 0)
					.anyMatch(p -> nextTop.start.x().compareTo(p.x()) <= 0 && p.x().compareTo(nextTop.end.x()) <= 0);
		}
		
		void rotateRight() {
			UnaryOperator<BigPoint> pointRotation = p -> new BigPoint(p.y().negate(), p.x());
			Set<Line> rotated = lines.stream().map(line -> Line.of(pointRotation.apply(line.start()), pointRotation.apply(line.end()))).collect(Collectors.toSet());
			lines.clear();
			lines.addAll(rotated);
			edges.clear();
			edges.putAll(edgesMap(rotated));
		}
		
		BigInteger perimeter() {
			return Line.lengthSum(lines).subtract(BigInteger.valueOf(lines.size()));
		}
		
		void print() {
			BigPoint.printField(lines.stream().flatMap(Line::containingPoints).collect(Collectors.toSet()), '#', '.');
		}
		
		static Shape of(Set<Line> lines) {
			return new Shape(new HashSet<>(lines), edgesMap(lines));
		}
		
		private static Map<BigPoint, List<Line>> edgesMap(Set<Line> lines) {
			Map<BigPoint, List<Line>> edges = new HashMap<>();
			for (Line line : lines)
				Stream.of(line.start(), line.end())
						.forEach(p -> edges.computeIfAbsent(p, k -> new ArrayList<>()).add(line));
			return edges;
		}
	}
	
	record Move(Direction direction, long meters) {
		static final Map<String, Direction> directionsByLetter = Direction.stream()
				.collect(Collectors.toMap(d -> d.name().substring(0, 1), d -> d));
		static final Direction[] directionsByIndex = {Direction.RIGHT, Direction.DOWN, Direction.LEFT, Direction.UP};
		
		static Move parse1(String line) {
			String[] split = line.split(" ");
			return new Move(directionsByLetter.get(split[0]), Long.parseLong(split[1]));
		}
		
		static Move parse2(String line) {
			String hex = StringUtils.substringBefore(StringUtils.substringAfter(line, "#"), ")");
			long value = Long.parseLong(hex, 16);
			return new Move(directionsByIndex[(int) (value % 16)], value / 16);
		}
	}
	
	static BigInteger min(BigInteger l, BigInteger r) {
		return l.compareTo(r) < 0 ? l : r;
	}
	
	static BigInteger max(BigInteger l, BigInteger r) {
		return l.compareTo(r) < 0 ? r : l;
	}
}