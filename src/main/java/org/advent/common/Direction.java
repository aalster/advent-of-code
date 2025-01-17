package org.advent.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Direction {
	UP(0, new Point(0, -1)),
	RIGHT(1, new Point(1, 0)),
	DOWN(2, new Point(0, 1)),
	LEFT(3, new Point(-1, 0));
	
	private final int indexClockwise;
	private final Point p;
	
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
			case LEFT -> '<';
			case RIGHT -> '>';
			case DOWN -> 'v';
		};
	}
	
	public static Stream<Direction> stream() {
		return Stream.of(Direction.values());
	}
	
	public static Direction parseSymbol(char symbol) {
		return switch (symbol) {
			case '>' -> RIGHT;
			case '<' -> LEFT;
			case '^' -> UP;
			case 'v', 'V' -> DOWN;
			default -> throw new IllegalArgumentException("" + symbol);
		};
	}
	
	public static Direction parseLetter(char symbol) {
		return switch (symbol) {
			case 'R' -> RIGHT;
			case 'L' -> LEFT;
			case 'U' -> UP;
			case 'D' -> DOWN;
			default -> throw new IllegalArgumentException("" + symbol);
		};
	}
}