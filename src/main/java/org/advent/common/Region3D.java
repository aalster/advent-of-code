package org.advent.common;

public record Region3D(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
	public Region3D {
		if (maxX < minX || maxY < minY || maxZ < minZ)
			throw new RuntimeException("Region bounds not sorted");
	}
	
	public long volume() {
		return (long) (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
	}
	
	@SuppressWarnings("RedundantIfStatement")
	public boolean intersects(Region3D other) {
		if (maxX < other.minX || other.maxX < minX)
			return false;
		if (maxY < other.minY || other.maxY < minY)
			return false;
		if (maxZ < other.minZ || other.maxZ < minZ)
			return false;
		return true;
	}
	
	public Region3D intersection(Region3D other) {
		if (!intersects(other))
			return null;
		return new Region3D(
				Math.max(minX, other.minX), Math.min(maxX, other.maxX),
				Math.max(minY, other.minY), Math.min(maxY, other.maxY),
				Math.max(minZ, other.minZ), Math.min(maxZ, other.maxZ));
	}
	
	// Режет фигуру на части [min, coordinate) и [coordinate, max]
	public Pair<Region3D, Region3D> cut(Axis3D axis, int coordinate) {
		return switch (axis) {
			case X -> Pair.of(new Region3D(minX, coordinate - 1, minY, maxY, minZ, maxZ), new Region3D(coordinate, maxX, minY, maxY, minZ, maxZ));
			case Y -> Pair.of(new Region3D(minX, maxX, minY, coordinate - 1, minZ, maxZ), new Region3D(minX, maxX, coordinate, maxY, minZ, maxZ));
			case Z -> Pair.of(new Region3D(minX, maxX, minY, maxY, minZ, coordinate - 1), new Region3D(minX, maxX, minY, maxY, coordinate, maxZ));
		};
	}
	
	public boolean containsInclusive(Point3D p) {
		return containsInclusive(p.x(), p.y(), p.z());
	}
	
	public boolean containsInclusive(int x, int y, int z) {
		return
				minX <= x && x <= maxX &&
				minY <= y && y <= maxY &&
				minZ <= z && z <= maxZ;
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