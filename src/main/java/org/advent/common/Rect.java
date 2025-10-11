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
		if (maxX() < other.minX() || other.maxX() < minX())
			return false;
		if (maxY() < other.minY() || other.maxY() < minY())
			return false;
		return true;
	}
}
