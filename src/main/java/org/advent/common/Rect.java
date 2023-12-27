package org.advent.common;

public record Rect(int x1, int y1, int x2, int y2) {
	
	public Rect(Point topLeft, Point bottomRight) {
		this(topLeft.x(), topLeft.y(), bottomRight.x(), bottomRight.y());
	}
	
	public boolean containsInclusive(Point p) {
		return x1 <= p.x() && p.x() <= x2 && y1 <= p.y() && p.y() <= y2;
	}
}
