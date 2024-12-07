package org.advent.common;

import java.util.Arrays;

public record Point3D(int x, int y, int z) {
	
	public Point3D shift(int dx, int dy, int dz) {
		return new Point3D(x + dx, y + dy, z + dz);
	}
	
	public Point3D shift(Point3D delta) {
		return shift(delta.x, delta.y, delta.z);
	}
	
	public Point3D subtract(Point3D other) {
		return shift(-other.x, -other.y, -other.z);
	}
	
	public int manhattanDistance(Point3D other) {
		return Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z);
	}
	
	/*
	
	   ^ Y
	   |
	   |
	   |   X
	   0--->
	  /
	 / Z
	 
	 */
	
	// Повернуть вдоль оси X по часовой стрелке (смотря из центра в положительную сторону оси)
	public Point3D rotateRightAlongX() {
		return new Point3D(x, -z, y);
	}
	
	// Повернуть вдоль оси Z по часовой стрелке (смотря из центра в положительную сторону оси)
	public Point3D rotateRightAlongZ() {
		//noinspection SuspiciousNameCombination
		return new Point3D(-y, x, z);
	}
	
	// Повернуть вдоль оси Y по часовой стрелке (смотря из центра в положительную сторону оси)
	public Point3D rotateRightAlongY() {
		return new Point3D(z, y, -x);
	}
	
	public boolean touches(Point3D p) {
		return (p.x == x && p.y == y && Math.abs(p.z - z) == 1) ||
				(p.x == x && p.z == z && Math.abs(p.y - y) == 1) ||
				(p.z == z && p.y == y && Math.abs(p.x - x) == 1);
	}
	
	public static Point3D parse(String value) {
		int[] c = Arrays.stream(value.split(",")).mapToInt(Integer::parseInt).toArray();
		return new Point3D(c[0], c[1], c[2]);
	}
}