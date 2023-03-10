package org.advent.common;

import java.util.Objects;
import java.util.stream.Stream;

public record Point(int x, int y) {
	public static final Point ZERO = new Point(0, 0);
	
	public Point shift(int dx, int dy) {
		return new Point(x + dx, y + dy);
	}
	
	public Point shift(Point p) {
		return new Point(x + p.x, y + p.y);
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
	
	public static Point parse(String value) {
		String[] split = value.split(",");
		int x = Integer.parseInt(split[0]);
		int y = Integer.parseInt(split[1]);
		return new Point(x, y);
	}
}
