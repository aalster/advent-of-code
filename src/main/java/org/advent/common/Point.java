package org.advent.common;

import java.util.Collection;
import java.util.IntSummaryStatistics;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public record Point(int x, int y) {
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
	
	public int distanceTo(Point p) {
		return Math.abs(x - p.x) + Math.abs(y - p.y);
	}
	
	public Point directionVectorTo(Point p) {
		DirectionExt vertical = p.y == y ? null : p.y < y ? DirectionExt.N : DirectionExt.S;
		DirectionExt horizontal = p.x == x ? null : p.x < x ? DirectionExt.W : DirectionExt.E;
		return Stream.of(horizontal, vertical).filter(Objects::nonNull).map(DirectionExt::getPoint).reduce(new Point(0, 0), Point::shift);
	}
	
	public boolean inRect(Point from, Point toExclusive) {
		if (toExclusive.x < from.x || toExclusive.y < from.y)
			throw new IllegalArgumentException("to lower than from");
		return from.x <= x && x < toExclusive.x && from.y <= y && y < toExclusive.y;
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
	
	public int manhattanDistance(Point p) {
		return Math.abs(p.x - x) + Math.abs(p.y - y);
	}
	
	public static Point parse(String value) {
		String[] split = value.split(",");
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
	
	public static void printField(Set<Point> field, char filled, char empty) {
		IntSummaryStatistics xStats = field.stream().mapToInt(Point::x).summaryStatistics();
		IntSummaryStatistics yStats = field.stream().mapToInt(Point::y).summaryStatistics();
		for (int y = yStats.getMin(); y <= yStats.getMax(); y++) {
			for (int x = xStats.getMin(); x <= xStats.getMax(); x++)
				System.out.print(field.contains(new Point(x, y)) ? filled : empty);
			System.out.println();
		}
	}
}
