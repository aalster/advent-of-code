package org.advent.common;

public enum Direction {
	RIGHT, DOWN, LEFT, UP;
	
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