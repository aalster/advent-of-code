package org.advent.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public record Point(int x, int y) {
	public static final Comparator<Point> COMPARATOR = Comparator.comparing(Point::y).thenComparing(Point::x);
	public static final Point ZERO = new Point(0, 0);
	
	public Point shift(int dx, int dy) {
		return new Point(x + dx, y + dy);
	}
	
	public Point shift(Point p) {
		return new Point(x + p.x, y + p.y);
	}
	
	public Point shift(Direction direction) {
		return direction.shift(this);
	}
	
	public Point move(Direction direction) {
		return move(direction, 1);
	}
	
	public Point move(Direction direction, int distance) {
		return switch (direction) {
			case LEFT -> new Point(x - distance, y);
			case RIGHT -> new Point(x + distance, y);
			case UP -> new Point(x, y - distance);
			case DOWN -> new Point(x, y + distance);
		};
	}
	
	public Point scale(int scale) {
		return new Point(x * scale, y * scale);
	}
	
	public Point subtract(Point p) {
		return new Point(x - p.x, y - p.y);
	}
	
	public int distanceTo(Point p) {
		return Math.abs(x - p.x) + Math.abs(y - p.y);
	}
	
	public boolean inRect(Point from, Point toExclusive) {
		if (toExclusive.x < from.x || toExclusive.y < from.y)
			throw new IllegalArgumentException("to lower than from");
		return from.x <= x && x < toExclusive.x && from.y <= y && y < toExclusive.y;
	}
	
	public static Point parse(String value) {
		return parse(value, ",");
	}
	
	public static Point parse(String value, String delimiterRegex) {
		String[] split = value.split(delimiterRegex);
		int x = Integer.parseInt(split[0]);
		int y = Integer.parseInt(split[1]);
		return new Point(x, y);
	}
	
	public static int minX(Collection<Point> points) {
		return points.stream().mapToInt(Point::x).min().orElseThrow();
	}
	
	public static int maxX(Collection<Point> points) {
		return points.stream().mapToInt(Point::x).max().orElseThrow();
	}
	
	public static int minY(Collection<Point> points) {
		return points.stream().mapToInt(Point::y).min().orElseThrow();
	}
	
	public static int maxY(Collection<Point> points) {
		return points.stream().mapToInt(Point::y).max().orElseThrow();
	}
	
	public static Point minBound(Collection<Point> points) {
		return new Point(minX(points), minY(points));
	}
	
	public static Point maxBound(Collection<Point> points) {
		return new Point(maxX(points), maxY(points));
	}
	
	public static Rect bounds(Collection<Point> points) {
		return new Rect(minBound(points), maxBound(points));
	}
	
	public static void printField(Set<Point> field, char filled, char empty) {
		IntSummaryStatistics xStats = field.stream().mapToInt(Point::x).summaryStatistics();
		IntSummaryStatistics yStats = field.stream().mapToInt(Point::y).summaryStatistics();
		for (int y = yStats.getMin(); y <= yStats.getMax(); y++) {
			for (int x = xStats.getMin(); x <= xStats.getMax(); x++)
				System.out.print(field.contains(new Point(x, y)) ? filled : empty);
			System.out.println();
		}
	}
	
	public static void printField(Collection<Point> field, Function<Point, Character> symbol) {
		IntSummaryStatistics xStats = field.stream().mapToInt(Point::x).summaryStatistics();
		IntSummaryStatistics yStats = field.stream().mapToInt(Point::y).summaryStatistics();
		for (int y = yStats.getMin(); y <= yStats.getMax(); y++) {
			for (int x = xStats.getMin(); x <= xStats.getMax(); x++)
				System.out.print(symbol.apply(new Point(x, y)));
			System.out.println();
		}
	}
	
	public static Map<Character, List<Point>> readField(List<String> lines) {
		Map<Character, List<Point>> field = new HashMap<>();
		int y = 0;
		for (String line : lines) {
			int x = 0;
			for (char c : line.toCharArray()) {
				field.computeIfAbsent(c, k -> new ArrayList<>()).add(new Point(x, y));
				x++;
			}
			y++;
		}
		return field;
	}
	
	public static Map<Point, Character> readFieldMap(List<String> lines) {
		Map<Point, Character> field = new HashMap<>();
		int y = 0;
		for (String line : lines) {
			int x = 0;
			for (char c : line.toCharArray()) {
				field.put(new Point(x, y), c);
				x++;
			}
			y++;
		}
		return field;
	}
}
