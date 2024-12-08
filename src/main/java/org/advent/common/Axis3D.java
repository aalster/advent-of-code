package org.advent.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Axis3D {
	X, Y, Z;
	
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
