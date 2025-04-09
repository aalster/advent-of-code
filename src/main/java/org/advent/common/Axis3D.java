package org.advent.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Axis3D {
	X, Y, Z;
	
	public Point3D shift(Point3D point, int distance) {
		return switch (this) {
			case X -> new Point3D(point.x() + distance, point.y(), point.z());
			case Y -> new Point3D(point.x(), point.y() + distance, point.z());
			case Z -> new Point3D(point.x(), point.y(), point.z() + distance);
		};
	}
	
	public int ofPoint(Point3D point) {
		return switch (this) {
			case X -> point.x();
			case Y -> point.y();
			case Z -> point.z();
		};
	}
	
	public int minOfRegion(Region3D region) {
		return switch (this) {
			case X -> region.minX();
			case Y -> region.minY();
			case Z -> region.minZ();
		};
	}
	
	public int maxOfRegion(Region3D region) {
		return switch (this) {
			case X -> region.maxX();
			case Y -> region.maxY();
			case Z -> region.maxZ();
		};
	}
}
