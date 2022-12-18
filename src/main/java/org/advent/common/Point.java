package org.advent.common;

public record Point(int x, int y) {
	
	public Point shift(int dx, int dy) {
		return new Point(x + dx, y + dy);
	}
	
	public int distanceTo(Point p) {
		return Math.abs(x - p.x) + Math.abs(y - p.y);
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
