package org.advent.common;

public record Rect(int minX, int maxX, int minY, int maxY) {
	
	public Rect {
		if (maxX < minX || maxY < minY)
			throw new IllegalArgumentException("Invalid rect");
	}
	
	public Rect(Point from, Point to) {
		this(from.x(), to.x(), from.y(), to.y());
	}
	
	public Point topLeft() {
		return new Point(minX, minY);
	}
	
	public Point bottomRight() {
		return new Point(maxX, maxY);
	}
	
	public boolean containsInclusive(Point p) {
		return containsInclusive(p.x(), p.y());
	}
	
	public boolean containsInclusive(int x, int y) {
		return minX <= x && x <= maxX && minY <= y && y <= maxY;
	}
	
	public boolean intersectsInclusive(Rect other) {
		return minX <= other.maxX && other.minX <= maxX && minY <= other.maxY && other.minY <= maxY;
	}
	
	public boolean intersectsExclusive(Rect other) {
		return minX < other.maxX && other.minX < maxX && minY < other.maxY && other.minY < maxY;
	}
	
	public static Rect ofCorners(Point from, Point to) {
		int minX = from.x();
		int maxX = to.x();
		if (maxX < minX) {
			int tmp = minX;
			minX = maxX;
			maxX = tmp;
		}
		int minY = from.y();
		int maxY = to.y();
		if (maxY < minY) {
			int tmp = minY;
			minY = maxY;
			maxY = tmp;
		}
		return new Rect(minX, maxX, minY, maxY);
	}
}
