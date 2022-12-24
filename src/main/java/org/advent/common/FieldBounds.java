package org.advent.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public record FieldBounds(Set<Point> field, Map<Integer, Pair<Point, Point>> rows, Map<Integer, Pair<Point, Point>> cols) {
	
	public Point rowMin(int row) {
		return rows.get(row).left();
	}
	
	public Point rowMax(int row) {
		return rows.get(row).right();
	}
	
	public Point colMin(int col) {
		return cols.get(col).left();
	}
	
	public Point colMax(int col) {
		return cols.get(col).right();
	}
	
	public boolean contains(Point point) {
		return field.contains(point);
	}
	
	public Point moveWrappingAround(Point point, Direction direction) {
		point = point.move(direction);
		if (field.contains(point))
			return point;
		return switch (direction) {
			case RIGHT -> rowMin(point.y());
			case LEFT -> rowMax(point.y());
			case DOWN -> colMin(point.x());
			case UP -> colMax(point.x());
		};
	}
	
	public static FieldBounds ofField(Set<Point> field) {
		Map<Integer, Pair<Point, Point>> cols = new HashMap<>();
		Map<Integer, Pair<Point, Point>> rows = new HashMap<>();
		for (Point point : field) {
			Pair<Point, Point> rowPair = rows.get(point.y());
			if (rowPair == null)
				rows.put(point.y(), new Pair<>(point, point));
			else if (point.x() < rowPair.left().x())
				rows.put(point.y(), new Pair<>(point, rowPair.right()));
			else if (rowPair.right().x() < point.x())
				rows.put(point.y(), new Pair<>(rowPair.left(), point));
			
			Pair<Point, Point> colPair = cols.get(point.x());
			if (colPair == null)
				cols.put(point.x(), new Pair<>(point, point));
			else if (point.y() < colPair.left().y())
				cols.put(point.x(), new Pair<>(point, colPair.right()));
			else if (colPair.right().y() < point.y())
				cols.put(point.x(), new Pair<>(colPair.left(), point));
		}
		return new FieldBounds(field, rows, cols);
	}
}