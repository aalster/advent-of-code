package org.advent.year2022.day14;

import lombok.RequiredArgsConstructor;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Day14 {
	static final boolean partTwo = true;
	
	public static void main(String[] args) throws Exception {
		Scanner input = Utils.scanFileNearClass(Day14.class, "input.txt");
		List<Path> rockPaths = new ArrayList<>();
		while (input.hasNext())
			rockPaths.add(Path.parse(input.nextLine()));
		
		Point sandSource = new Point(500, 0);
		
		IntSummaryStatistics xStats = Stream.concat(Stream.of(sandSource), rockPaths.stream().flatMap(p -> p.points().stream()))
				.mapToInt(Point::x).summaryStatistics();
		IntSummaryStatistics yStats = Stream.concat(Stream.of(sandSource), rockPaths.stream().flatMap(p -> p.points().stream()))
				.mapToInt(Point::y).summaryStatistics();
		Point shift = new Point(3 - xStats.getMin() + yStats.getMax() + 5, -yStats.getMin());
		rockPaths = rockPaths.stream().map(p -> p.shift(shift.x(), shift.y())).toList();
		sandSource = sandSource.shift(shift.x(), shift.y());
		
		Field field = new Field(new Cell[yStats.getMax() + shift.y() + 3][xStats.getMax() + shift.x() + 3 + yStats.getMax() + 5]);
		field.fill(Cell.EMPTY);
		field.put(sandSource, Cell.SAND_SOURCE);
		for (Path rockPath : rockPaths)
			field.fillPath(rockPath, Cell.ROCK);
		
		if (partTwo)
			field.fillLine(new Point(0, field.height() - 1), new Point(field.width() - 1, field.height() - 1), Cell.ROCK);
		
		long step = 0;
		boolean voidReached = false;
		while (!voidReached) {
			Point sand = sandSource.shift(0, 0);
			if (field.get(sand) == Cell.SAND)
				break;
			while (true) {
				step++;
//				field.put(sand, Cell.FALLING_SAND);
//				if (step % 100 == 0) {
//					System.out.println(field);
//					Thread.sleep(70);
//				}
//				field.put(sand, Cell.EMPTY);
				
				if (sand.y() >= field.height() - 1) {
					voidReached = true;
					break;
				}
				Point nextSand = field.fallTarget(sand);
				if (nextSand == null) {
					field.put(sand, Cell.SAND);
					break;
				}
				sand = nextSand;
			}
		}
		System.out.println(field);
		if (voidReached)
			System.out.println("VOID!");
		System.out.println("Answer " + (partTwo ? 2 : 1) + ": " + field.count(Cell.SAND));
	}
	
	record Field(Cell[][] cells) {
		Cell get(Point p) {
			return cells[p.y()][p.x()];
		}
		
		void put(Point p, Cell c) {
			cells[p.y()][p.x()] = c;
		}
		
		Point fallTarget(Point p) {
			Point center = p.shift(0, 1);
			if (get(center) == Cell.EMPTY)
				return center;
			Point left = p.shift(-1, 1);
			if (get(left) == Cell.EMPTY)
				return left;
			Point right = p.shift(1, 1);
			if (get(right) == Cell.EMPTY)
				return right;
			return null;
		}
		
		long count(Cell c) {
			return Stream.of(cells).flatMap(Stream::of).filter(cell -> cell == c).count();
		}
		
		void fill(Cell c) {
			for (Cell[] row : cells)
				Arrays.fill(row, c);
		}
		
		void fillPath(Path path, Cell c) {
			Iterator<Point> iterator = path.points().iterator();
			Point prev = iterator.next();
			while (iterator.hasNext()) {
				Point next = iterator.next();
				fillLine(prev, next, c);
				prev = next;
			}
		}
		
		void fillLine(Point from, Point to, Cell c) {
			while (!from.equals(to)) {
				put(from, c);
				from = from.shift(Integer.compare(to.x(), from.x()), Integer.compare(to.y(), from.y()));
			}
			put(from, c);
		}
		
		int width() {
			return cells[0].length;
		}
		
		int height() {
			return cells.length;
		}
		
		@Override
		public String toString() {
			StringBuilder r = new StringBuilder(width() * height());
			for (Cell[] row : cells) {
				for (Cell cell : row)
					r.append(cell.symbol);
				r.append("\n");
			}
			return r.toString();
		}
	}
	
	@RequiredArgsConstructor
	enum Cell {
		EMPTY('.'),
		ROCK('#'),
		SAND_SOURCE('+'),
		FALLING_SAND('~'),
		SAND('O');
		
		final char symbol;
		
		@Override
		public String toString() {
			return Character.toString(symbol);
		}
	}
	
	record Path(List<Point> points) {
		Path shift(int dx, int dy) {
			return new Path(points.stream().map(p -> p.shift(dx, dy)).toList());
		}
		
		static Path parse(String value) {
			return new Path(Arrays.stream(value.split(" -> ")).map(Point::parse).toList());
		}
	}
}