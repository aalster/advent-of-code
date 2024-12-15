package org.advent.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum DirectionExt {
	N(new Point(0, -1)),
	S(new Point(0, 1)),
	W(new Point(-1, 0)),
	E(new Point(1, 0)),
	
	NW(new Point(-1, -1)),
	NE(new Point(1, -1)),
	SW(new Point(-1, 1)),
	SE(new Point(1, 1));
	
	private final Point point;
	
	public Point shift(Point p) {
		return p.shift(point);
	}
	
	public static Stream<DirectionExt> stream() {
		return EnumSet.allOf(DirectionExt.class).stream();
	}
}