package org.advent.year2020.day11;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day11 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day11()).runAll();
//		new DayRunner(new Day11()).run("example.txt", 1);
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 37, null),
				new ExpectedAnswers("input.txt", 2481, null)
		);
	}
	
	char[][] field;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		field = Utils.readLines(input).stream().map(String::toCharArray).toArray(char[][]::new);
	}
	
	@Override
	public Object part1() {
		State current = new State(field);
		State prev;
		int steps = 0;
		while (true) {
//			System.out.println(current);
//			System.out.println();
			steps++;
			prev = current;
			current = current.next();
			if (prev.equals(current))
				break;
		}
		return current.occupied();
	}
	
	@Override
	public Object part2() {
		return null;
	}
	
	record State(char[][] field) {
		
		State next() {
			char[][] next = new char[field.length][field[0].length];
			for (int y = 0; y < field.length; y++) {
				char[] row = field[y];
				cell: for (int x = 0; x < row.length; x++) {
					char c = row[x];
					if (c == 'L') {
						for (int dy = Math.max(y - 1, 0); dy < Math.min(y + 2, field.length); dy++) {
							for (int dx = Math.max(x - 1, 0); dx < Math.min(x + 2, row.length); dx++) {
								if (x == dx && y == dy)
									continue;
								if (field[dy][dx] == '#') {
									next[y][x] = c;
									continue cell;
								}
							}
						}
						next[y][x] = '#';
					} else if (c == '#') {
						int occupied = 0;
						for (int dy = Math.max(y - 1, 0); dy < Math.min(y + 2, field.length); dy++) {
							for (int dx = Math.max(x - 1, 0); dx < Math.min(x + 2, row.length); dx++) {
								if (x == dx && y == dy)
									continue;
								if (field[dy][dx] == '#')
									occupied++;
							}
						}
						next[y][x] = occupied >= 4 ? 'L' : c;
					} else {
						next[y][x] = c;
					}
				}
			}
			return new State(next);
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
			if (this == obj)
				return true;
			return obj instanceof State other && Arrays.deepEquals(field, other.field);
		}
		
		@Override
		public String toString() {
			return Arrays.stream(field).map(String::new).collect(Collectors.joining("\n"));
		}
	}
}