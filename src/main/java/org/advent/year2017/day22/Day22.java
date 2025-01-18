package org.advent.year2017.day22;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Day22 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day22()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 5587, 2511944),
				new ExpectedAnswers("input.txt", 5433, 2512599)
		);
	}
	
	Set<Point> initial;
	Point start;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<String> lines = Utils.readLines(input);
		initial = new HashSet<>(Point.readField(lines).get('#'));
		start = new Point(lines.getFirst().length() / 2, lines.size() / 2);
	}
	
	@Override
	public Object part1() {
		Point current = start;
		Set<Point> infected = new HashSet<>(initial);
		Direction direction = Direction.UP;
		int infectedCount = 0;
		for (int i = 0; i < 10000; i++) {
			if (infected.contains(current)) {
				direction = direction.rotate(Direction.RIGHT);
				infected.remove(current);
			} else {
				direction = direction.rotate(Direction.LEFT);
				infected.add(current);
				infectedCount++;
			}
			current = current.shift(direction);
		}
		return infectedCount;
	}
	
	@Override
	public Object part2() {
		char[][] field = new char[500][500];
		int shift = field.length / 2;
		for (Point infected : initial)
			field[shift + infected.x()][shift + infected.y()] = '#';
		
		int x = shift + start.x();
		int y = shift + start.y();
		Direction direction = Direction.UP;
		int infectedCount = 0;
		
		for (int i = 0; i < 10000000; i++) {
			field[x][y] = switch (field[x][y]) {
				case '#' -> {
					direction = direction.rotate(Direction.RIGHT);
					yield 'F';
				}
				case 0, '.' -> {
					direction = direction.rotate(Direction.LEFT);
					yield 'W';
				}
				case 'W' -> {
					infectedCount++;
					yield '#';
				}
				case 'F' -> {
					direction = direction.rotate(Direction.DOWN);
					yield '.';
				}
				default -> throw new IllegalStateException("Unexpected value: " + field[x][y]);
			};
			x += direction.getP().x();
			y += direction.getP().y();
		}
		return infectedCount;
	}
}