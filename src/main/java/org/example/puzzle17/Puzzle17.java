package org.example.puzzle17;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Puzzle17 {
	
	static final boolean preview = false;
	static final int fieldWidth = 7;
	static final int heightThreshold = 60;
	static final int heightLimit = 50;
	static final int totalFiguresCount = 2022;
//	static final long totalFiguresCount = 1_000_000_000_000L;
	
	public static void main(String[] args) throws Exception {
		Scanner input = Utils.scanFileNearClass(Puzzle17.class, "example.txt");
		Wind wind = new Wind(input.nextLine());
		Field field = Field.create(fieldWidth);
		FigureFactory figureFactory = new FigureFactory();
		
		long figuresCount = 0;
		long heightCutoff = 0;
		
		long startTime = System.currentTimeMillis();
		
		for (long step = 0; ; step++) {
			if (field.fallingFigure == null) {
				heightCutoff += field.limitHeight(heightThreshold, heightLimit);
				
				if (figuresCount >= totalFiguresCount)
					break;
				field.addFallingFigure(figureFactory.nextFigure());
				figuresCount++;
				
				if (preview) {
					field.drawField();
					Thread.sleep(800);
				}
			}
			int windDirection = wind.nextDirection();
			if (step % 10000000 == 0) {
				long now = System.currentTimeMillis();
				System.out.println("\n\nStep: " + step + ", Figures: " + figuresCount + " - " + (100f * figuresCount / totalFiguresCount) + "%");
				System.out.println("delay: " + (now - startTime));
				startTime = now;
//				field.drawField();
			}
			field.wind(windDirection);
			if (preview) {
				field.drawField();
				Thread.sleep(500);
			}
			field.fall();
			if (preview) {
				System.out.println("\n");
				field.drawField();
				Thread.sleep(500);
			}
		}
//		field.drawField();
		System.out.println("Answer: " + (heightCutoff + field.rockHeight()));
	}
	
	@AllArgsConstructor
	static class Field {
		int width;
		int rockHeight;
		List<Cell[]> cells;
		FallingFigure fallingFigure;
		
		private void wind(int windDirection) {
			Point next = fallingFigure.position.shift(windDirection, 0);
			Figure figure = fallingFigure.figure;
			if (0 <= next.x && next.x + figure.width < width && !figure.intersectsCell(next, cells, Cell.ROCK))
				fallingFigure.setPosition(next);
		}
		
		private void fall() {
			Point next = fallingFigure.position.shift(0, -1);
			Figure figure = fallingFigure.figure;
			if (next.y < 0 || figure.intersectsCell(next, cells, Cell.ROCK)) {
				figure.draw(fallingFigure.position, cells, Cell.ROCK);
				fallingFigure = null;
			} else {
				fallingFigure.setPosition(next);
			}
		}
		
		int limitHeight(int thresholdHeight, int limit) {
			if (height() <= thresholdHeight)
				return 0;
			
			int cutoff = height() - limit;
			List<Cell[]> subList = cells.subList(cutoff, height() - 1);
			cells = new ArrayList<>(thresholdHeight + 10);
			cells.addAll(subList);
			return cutoff;
		}
		
		void drawField() {
			fallingFigure.figure.draw(fallingFigure.position, cells, Cell.FALLING_ROCK);
			for (int y = cells.size() - 1; y >= 0; y--) {
				Cell[] row = cells.get(y);
				System.out.print("|");
				for (Cell cell : row)
					System.out.print(cell.symbol);
				System.out.println("|");
			}
			System.out.println("+" + "-".repeat(width) + "+");
			fallingFigure.figure.draw(fallingFigure.position, cells, Cell.EMPTY);
		}
		
		int height() {
			return cells.size();
		}
		
		int rockHeight() {
			int rowHeight = height() - 1;
			while (rowHeight >= 0) {
				Cell[] row = cells.get(rowHeight);
				for (Cell cell : row)
					if (cell == Cell.ROCK)
						return rowHeight + 1;
				rowHeight--;
			}
			return 0;
		}
		
		void addFallingFigure(Figure figure) {
			int rockHeight = rockHeight();
			Point position = new Point(2, rockHeight + 3);
			fallingFigure = new FallingFigure(figure, position);
			int newHeight = position.y + figure.height;
			while (height() <= newHeight)
				cells.add(createEmptyRow(width));
		}
		
		static Field create(int width) {
			List<Cell[]> cells = new ArrayList<>();
			for (int i = 0; i < width; i++)
				cells.add(createEmptyRow(width));
			return new Field(width, 0, cells, null);
		}
		
		static Cell[] createEmptyRow(int width) {
			Cell[] row = new Cell[width];
			Arrays.fill(row, Cell.EMPTY);
			return row;
		}
	}
	
	@AllArgsConstructor
	static class FallingFigure {
		final Figure figure;
		Point position;
		
		void setPosition(Point position) {
			this.position = position;
		}
	}
	
	record Figure(
			List<Point> relationalPoints,
			int width,
			int height
	) {
		Figure(List<Point> relationalPoints) {
			this(relationalPoints, calcWidth(relationalPoints), calcHeight(relationalPoints));
		}
		
		void draw(Point position, List<Cell[]> cells, Cell c) {
			for (Point point : relationalPoints)
				cells.get(position.y + point.y)[position.x + point.x] = c;
		}
		
		boolean intersectsCell(Point position, List<Cell[]> cells, Cell c) {
			for (Point point : relationalPoints)
				if (cells.get(position.y + point.y)[position.x + point.x] == c)
					return true;
			return false;
		}
		
		static int calcWidth(List<Point> points) {
			return points.stream().mapToInt(Point::x).max().orElse(0);
		}
		
		static int calcHeight(List<Point> points) {
			return points.stream().mapToInt(Point::y).max().orElse(0);
		}
	}
	
	static class FigureFactory {
		final Figure[] shapes = new Figure[] {horizontal(), cross(), corner(), vertical(), cube()};
		int currentShape = -1;
		
		Figure nextFigure() {
			currentShape++;
			return shapes[currentShape % shapes.length];
		}
		
		static Figure horizontal() {
			return new Figure(List.of(
					new Point(0, 0),
					new Point(1, 0),
					new Point(2, 0),
					new Point(3, 0)
			));
		}
		
		static Figure cross() {
			return new Figure(List.of(
					new Point(1, 0),
					new Point(0, 1),
					new Point(1, 1),
					new Point(2, 1),
					new Point(1, 2)
			));
		}
		
		static Figure corner() {
			return new Figure(List.of(
					new Point(0, 0),
					new Point(1, 0),
					new Point(2, 0),
					new Point(2, 1),
					new Point(2, 2)
			));
		}
		
		static Figure vertical() {
			return new Figure(List.of(
					new Point(0, 0),
					new Point(0, 1),
					new Point(0, 2),
					new Point(0, 3)
			));
		}
		
		static Figure cube() {
			return new Figure(List.of(
					new Point(0, 0),
					new Point(1, 0),
					new Point(0, 1),
					new Point(1, 1)
			));
		}
	}
	
	record Point(int x, int y) {
		
		Point shift(int dx, int dy) {
			return new Point(x + dx, y + dy);
		}
	}
	
	@Getter
	@RequiredArgsConstructor
	enum Cell {
		EMPTY('.'),
		ROCK('#'),
		FALLING_ROCK('@');
		
		final char symbol;
	}
	
	@RequiredArgsConstructor
	static class Wind {
		final String directions;
		int step = -1;
		
		int nextDirection() {
			step++;
			if (step >= directions.length())
				step = 0;
			return directions.charAt(step) == '<' ? -1 : 1;
		}
	}
}