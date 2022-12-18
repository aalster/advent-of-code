package org.advent.year2022.day17;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.advent.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Day17 {
	
	static final boolean preview = false;
	static final int fieldWidth = 7;
	static final int heightLimit = 1000;
	static final int heightThreshold = heightLimit + 200;
	static final int totalFiguresCount1 = 2022;
	static final long totalFiguresCount2 = 1_000_000_000_000L;
	
	public static void main(String[] args) throws Exception {
		Scanner input = Utils.scanFileNearClass(Day17.class, "input.txt");
		Wind wind = new Wind(input.nextLine());
		Field field = Field.create(fieldWidth);
		FigureFactory figureFactory = new FigureFactory();
		
		List<WindLoopStats> loopStats = new ArrayList<>();
		int currentLoop = 0;
		
		long figuresCount = 0;
		long heightCutoff = 0;
		
		long startTime = System.currentTimeMillis();
		List<Long> delays = new ArrayList<>();
		
		for (long step = 0; ; step++) {
			
			if (currentLoop < wind.loop()) {
				loopStats.add(new WindLoopStats(heightCutoff + field.rockHeight, figuresCount, wind.step, figureFactory.currentShape));
				currentLoop = wind.loop();
				
				if (loopStats.size() == 20) {
					for (WindLoopStats stats : loopStats)
						System.out.println(stats);
					
					WindLoopStats firstLoopStats = loopStats.get(0);
					
					Set<Long> otherLoopsDelta = new HashSet<>();
					for (int i = 1; i < loopStats.size() - 1; i++)
						otherLoopsDelta.add(loopStats.get(i + 1).rockHeight - loopStats.get(i).rockHeight);
					if (otherLoopsDelta.size() != 1)
						throw new RuntimeException("Prediction failed: " + otherLoopsDelta);
					
					WindLoopStats second = loopStats.get(1);
					WindLoopStats third = loopStats.get(2);
					WindLoopStats loop = new WindLoopStats(
							third.rockHeight - second.rockHeight,
							third.figureCount - second.figureCount,
							third.windStep - second.windStep,
							third.figureStep - second.figureStep
					);
					
					while (figuresCount + loop.figureStep + 1000 <= totalFiguresCount2) {
						figuresCount += loop.figureCount;
						heightCutoff += loop.rockHeight;
						wind.step += loop.windStep;
						figureFactory.currentShape += loop.figureStep;
					}
				}
			}
			
			
//			if (wind.step % wind.directions.length() == 0) {
//				System.out.println("Interesting figures: " + figuresCount + ", Height: " + (heightCutoff + field.rockHeight));
//				field.drawField(20);
//				Thread.sleep(1000);
//			}
			
			if (field.fallingFigure == null) {
				heightCutoff += field.limitHeight(heightThreshold, heightLimit);
				
				if (figuresCount == totalFiguresCount1) {
					System.out.println("Answer 1: " + (heightCutoff + field.rockHeight));
//					Thread.sleep(1000);
				}
				
				if (figuresCount >= totalFiguresCount2)
					break;
				field.addFallingFigure(figureFactory.nextFigure());
				figuresCount++;
				
//				if (preview) {
//					field.drawField(20);
//					Thread.sleep(800);
//				}
				
				if (figuresCount % 10000000 == 0) {
					long now = System.currentTimeMillis();
					long delay = now - startTime;
					delays.add(delay);
					System.out.println("\n\nStep: " + step + ", Figures: " + figuresCount + " - " + (100f * figuresCount / totalFiguresCount2) + "%");
					System.out.println("Delay: " + delay + ". Average: " + delays.stream().mapToLong(d -> d).average().orElse(0));
					startTime = now;
//				    field.drawField();
				}
			}
			field.wind(wind.nextDirection());
			if (preview) {
				System.out.println("\n");
				field.drawField(20);
				Thread.sleep(500);
			}
			field.fall();
			if (preview) {
				System.out.println("\n");
				field.drawField(20);
				Thread.sleep(500);
			}
		}
//		field.drawField();
		System.out.println("Answer 2: " + (heightCutoff + field.rockHeight));
	}
	
	record WindLoopStats(long rockHeight, long figureCount, long windStep, long figureStep) {
	
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
			if (next.x < 0 || width <= next.x + figure.width)
				return;
			if (next.y <= rockHeight && figure.intersectsCell(next, cells, Cell.ROCK))
				return;
			
			fallingFigure.setPosition(next);
		}
		
		private void fall() {
			Point position = fallingFigure.position;
			Point next = position.shift(0, -1);
			Figure figure = fallingFigure.figure;
			if (next.y <= rockHeight) {
				if (next.y < 0 || figure.intersectsCell(next, cells, Cell.ROCK)) {
					figure.draw(position, cells, Cell.ROCK);
					rockHeight = Math.max(rockHeight, position.y + figure.height + 1);
					fallingFigure = null;
					return;
				}
			}
			fallingFigure.setPosition(next);
		}
		
		int limitHeight(int thresholdHeight, int limit) {
			if (height() <= thresholdHeight)
				return 0;
			
			int cutoff = height() - limit;
			List<Cell[]> subList = cells.subList(cutoff, height() - 1);
			cells = new ArrayList<>(thresholdHeight + 10);
			cells.addAll(subList);
			rockHeight -= cutoff;
			return cutoff;
		}
		
		void drawField(int rows) {
			if (fallingFigure != null)
				fallingFigure.figure.draw(fallingFigure.position, cells, Cell.FALLING_ROCK);
			int lastRow = rows > 0 ? Math.max(cells.size() - 1 - rows, 0) : 0;
			for (int y = cells.size() - 1; y >= lastRow; y--) {
				Cell[] row = cells.get(y);
				System.out.print("|");
				for (Cell cell : row)
					System.out.print(cell.symbol);
				System.out.println("|");
			}
			System.out.println("+" + "-".repeat(width) + "+");
			if (fallingFigure != null)
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
		static final Figure[] shapes = new Figure[] {horizontal(), cross(), corner(), vertical(), cube()};
		long currentShape = -1;
		
		Figure nextFigure() {
			currentShape++;
			return shapes[(int) (currentShape % shapes.length)];
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
		long step = -1;
		
		int nextDirection() {
			step++;
			return directions.charAt((int) (step % directions.length())) == '<' ? -1 : 1;
		}
		
		String peekNextDirections(int count) {
			StringBuilder result = new StringBuilder(count);
			for (int i = 1; i <= count; i++) {
				result.append(directions.charAt((int) ((step + i) % directions.length())));
			}
			return result.toString();
		}
		
		int loop() {
			return (int) (step / directions.length());
		}
	}
}