package org.advent.year2020.day11;

import org.advent.common.DirectionExt;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day11 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day11()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 37, 26),
				new ExpectedAnswers("input.txt", 2481, 2227)
		);
	}
	
	Field field;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		field = Field.parse(Utils.readLines(input));
	}
	
	@Override
	public Object part1() {
		Field current = field;
		Field prev = null;
		while (!current.equals(prev)) {
			prev = current;
			current = current.next(current::neighbor1, 4);
		}
		return current.occupied();
	}
	
	@Override
	public Object part2() {
		Field current = field;
		Field prev = null;
		while (!current.equals(prev)) {
			prev = current;
			current = current.next(current::neighbor2, 5);
		}
		return current.occupied();
	}
	
	interface NeighborFunction {
		char apply(int x, int y, DirectionExt direction);
	}
	
	record Field(char[][] field) {
		
		Field next(NeighborFunction neighbor, int maxOccupied) {
			char[][] next = new char[field.length][field[0].length];
			for (int y = 0; y < field.length; y++)
				for (int x = 0; x < field[y].length; x++)
					next[y][x] = next(x, y, field[y][x], neighbor, maxOccupied);
			return new Field(next);
		}
		
		char next(int x, int y, char current, NeighborFunction neighbor, int maxOccupied) {
			if (current == 'L') {
				for (DirectionExt direction : DirectionExt.values())
					if (neighbor.apply(x, y, direction) == '#')
						return current;
				return '#';
			}
			if (current == '#') {
				int occupied = 0;
				for (DirectionExt direction : DirectionExt.values()) {
					if (neighbor.apply(x, y, direction) == '#') {
						occupied++;
						if (occupied >= maxOccupied)
							return 'L';
					}
				}
			}
			return current;
		}
		
		char neighbor1(int x, int y, DirectionExt direction) {
			x += direction.getPoint().x();
			y += direction.getPoint().y();
			return field[y][x];
		}
		
		char neighbor2(int x, int y, DirectionExt direction) {
			while (true) {
				x += direction.getPoint().x();
				y += direction.getPoint().y();
				char neighbor = field[y][x];
				if (neighbor != '.')
					return neighbor;
			}
		}
		
		long occupied() {
			return Arrays.stream(field).mapToLong(r -> new String(r).chars().filter(c -> c == '#').count()).sum();
		}
		
		@Override
		public int hashCode() {
			return Arrays.deepHashCode(field);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			if (this == obj)
				return true;
			return obj instanceof Field(char[][] otherField) && Arrays.deepEquals(field, otherField);
		}
		
		static Field parse(List<String> lines) {
			return new Field(extend(lines.stream().map(String::toCharArray).toArray(char[][]::new)));
		}
		
		static char[][] extend(char[][] field) {
			int realWidth = field[0].length;
			char[][] result = new char[field.length + 2][realWidth + 2];
			Arrays.fill(result[0], ' ');
			for (int y = 0; y < field.length; y++) {
				result[y + 1][0] = ' ';
				System.arraycopy(field[y], 0, result[y + 1], 1, realWidth);
				result[y + 1][realWidth + 1] = ' ';
			}
			Arrays.fill(result[result.length - 1], ' ');
			return result;
		}
	}
}