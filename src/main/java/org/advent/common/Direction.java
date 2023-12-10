package org.advent.common;

import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum Direction {
	RIGHT(new Point(1, 0)),
	DOWN(new Point(0, 1)),
	LEFT(new Point(-1, 0)),
	UP(new Point(0, -1));
	
	private final Point p;
	
	public Point shift(Point point) {
		return point.shift(p);
	}
	
	public Direction reverse() {
		return switch (this) {
			case UP -> DOWN;
			case DOWN -> UP;
			case LEFT -> RIGHT;
			case RIGHT -> LEFT;
		};
	}
	
	public String presentation() {
		return switch (this) {
			case UP -> "^";
			case LEFT -> "<";
			case RIGHT -> ">";
			case DOWN -> "v";
		};
	}
	
	public Direction rotate(Direction direction) {
		if (direction == RIGHT) {
			return switch (this) {
				case RIGHT -> DOWN;
				case DOWN -> LEFT;
				case LEFT -> UP;
				case UP -> RIGHT;
			};
		}
		if (direction == LEFT) {
			return switch (this) {
				case RIGHT -> UP;
				case DOWN -> RIGHT;
				case LEFT -> DOWN;
				case UP -> LEFT;
			};
		}
		throw new IllegalArgumentException("Supports only LEFT/RIGHT");
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
}