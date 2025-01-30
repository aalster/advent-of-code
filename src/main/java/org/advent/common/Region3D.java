package org.advent.common;

import java.util.List;

public record Region3D(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
	public Region3D {
		if (maxX < minX || maxY < minY || maxZ < minZ)
			throw new RuntimeException("Region bounds not sorted");
	}
	
	public List<Point3D> allCorners() {
		return List.of(
				new Point3D(minX, minY, minZ),
				new Point3D(maxX, minY, minZ),
				new Point3D(maxX, maxY, minZ),
				new Point3D(minX, maxY, minZ),
				new Point3D(minX, minY, maxZ),
				new Point3D(maxX, minY, maxZ),
				new Point3D(maxX, maxY, maxZ),
				new Point3D(minX, maxY, maxZ)
		);
	}
	
	public long volume() {
		return (long) (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
	}
	
	public Region3D intersection(Region3D other) {
		int interMinX = Math.max(minX, other.minX);
		int interMaxX = Math.min(maxX, other.maxX);
		int interMinY = Math.max(minY, other.minY);
		int interMaxY = Math.min(maxY, other.maxY);
		int interMinZ = Math.max(minZ, other.minZ);
		int interMaxZ = Math.min(maxZ, other.maxZ);
		if (interMaxX < interMinX || interMaxY < interMinY || interMaxZ < interMinZ)
			return null;
		return new Region3D(interMinX, interMaxX, interMinY, interMaxY, interMinZ, interMaxZ);
	}
	
	// Режет фигуру на части [min, coordinate) и [coordinate, max]
	public Pair<Region3D, Region3D> cut(Axis3D axis, int coordinate) {
		return switch (axis) {
			case X -> Pair.of(new Region3D(minX, coordinate - 1, minY, maxY, minZ, maxZ), new Region3D(coordinate, maxX, minY, maxY, minZ, maxZ));
			case Y -> Pair.of(new Region3D(minX, maxX, minY, coordinate - 1, minZ, maxZ), new Region3D(minX, maxX, coordinate, maxY, minZ, maxZ));
			case Z -> Pair.of(new Region3D(minX, maxX, minY, maxY, minZ, coordinate - 1), new Region3D(minX, maxX, minY, maxY, coordinate, maxZ));
		};
	}
	
	public boolean contains(Point3D p) {
		return contains(p.x(), p.y(), p.z());
	}
	
	public boolean contains(int x, int y, int z) {
		return minX <= x && x <= maxX && minY <= y && y <= maxY && minZ <= z && z <= maxZ;
	}
	
	public static Region3D fromCenter(Point3D center, int radius) {
		return new Region3D(
				center.x() - radius, center.x() + radius,
				center.y() - radius, center.y() + radius,
				center.z() - radius, center.z() + radius);
	}
	
	public static Region3D ofCorners(Point3D left, Point3D right) {
		return new Region3D(
						Math.min(left.x(), right.x()), Math.max(left.x(), right.x()),
						Math.min(left.y(), right.y()), Math.max(left.y(), right.y()),
						Math.min(left.z(), right.z()), Math.max(left.z(), right.z()));
	}
}