package org.advent.common;

public record Point(int x, int y) {
	
	public Point shift(int dx, int dy) {
		return new Point(x + dx, y + dy);
	}
	
	public int distanceTo(Point p) {
		return Math.abs(x - p.x) + Math.abs(y - p.y);
	}
	
	public Point move(Direction d) {
		return switch (d) {
			case LEFT -> new Point(x - 1, y);
			case RIGHT -> new Point(x + 1, y);
			case UP -> new Point(x, y - 1);
			case DOWN -> new Point(x, y + 1);
		};
	}
	
	public static Point parse(String value) {
		String[] split = value.split(",");
		int x = Integer.parseInt(split[0]);
		int y = Integer.parseInt(split[1]);
		return new Point(x, y);
	}
}
