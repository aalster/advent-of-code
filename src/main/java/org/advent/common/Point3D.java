package org.advent.common;

import java.util.Arrays;

public record Point3D(int x, int y, int z) {
	
	public Point3D shift(int dx, int dy, int dz) {
		return new Point3D(x + dx, y + dy, z + dz);
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