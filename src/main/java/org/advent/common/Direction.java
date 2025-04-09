package org.advent.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Direction {
	UP(0, new Point(0, -1), true),
	RIGHT(1, new Point(1, 0), false),
	DOWN(2, new Point(0, 1), true),
	LEFT(3, new Point(-1, 0), false);
	
	private final int indexClockwise;
	private final Point p;
	private final boolean vertical;
	
	public Point shift(Point point) {
		return point.shift(p);
	}
	
	public Direction reverse() {
		return rotate(DOWN);
	}
	
	public Direction mirror() {
		return switch (this) {
			case LEFT -> RIGHT;
			case RIGHT -> LEFT;
			default -> this;
		};
	}
	
	public Direction rotate(Direction direction) {
		return values()[(indexClockwise + direction.indexClockwise) % 4];
	}
	
	public String presentation() {
		return String.valueOf(presentationChar());
	}
	
	public char presentationChar() {
		return switch (this) {
			case UP -> '^';
			case RIGHT -> '>';
			case DOWN -> 'v';
			case LEFT -> '<';
		};
	}
	
	public static Stream<Direction> stream() {
		return Stream.of(Direction.values());
	}
	
	public static Direction parseSymbol(char symbol) {
		return switch (symbol) {
			case '^' -> UP;
			case '>' -> RIGHT;
			case 'v', 'V' -> DOWN;
			case '<' -> LEFT;
			default -> throw new IllegalArgumentException("" + symbol);
		};
	}
	
	public static Direction parseLetter(char symbol) {
		return switch (symbol) {
			case 'U' -> UP;
			case 'R' -> RIGHT;
			case 'D' -> DOWN;
			case 'L' -> LEFT;
			default -> throw new IllegalArgumentException("" + symbol);
		};
	}
	
	public static Direction parseCompassLetter(char symbol) {
		return switch (symbol) {
			case 'N' -> UP;
			case 'E' -> RIGHT;
			case 'S' -> DOWN;
			case 'W' -> LEFT;
			default -> throw new IllegalArgumentException("" + symbol);
		};
	}
}