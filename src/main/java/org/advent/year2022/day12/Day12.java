package org.advent.year2022.day12;

import lombok.Data;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;

public class Day12 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day12()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 31, 29),
				new ExpectedAnswers("input.txt", 484, 478)
		);
	}
	
	Cell[][] cells;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<Cell[]> rows = new ArrayList<>();
		int y = 0;
		while (input.hasNext()) {
			rows.add(Cell.parseRow(y, input.nextLine()));
			y++;
		}
		cells = rows.toArray(Cell[][]::new);
	}
	
	@Override
	public Object part1() {
		Grid grid = new Grid(Cell.copy(cells), false);
		
		Cell start = grid.findStart();
		int step = 0;
		start.setSteps(step);
		Cell end = grid.findEnd();
		List<Cell> currentCells = List.of(start);
		while (end.getSteps() < 0) {
			step++;
			currentCells = grid.nextStep(step, currentCells);
			if (currentCells.isEmpty())
				throw new RuntimeException("No path!");
		}
		return end.getSteps();
	}
	
	@Override
	public Object part2() {
		Grid grid = new Grid(Cell.copy(cells), true);
		
		List<Cell> lowest = grid.findLowest();
		Cell end = grid.findEnd();
		int step = 0;
		end.setSteps(step);
		List<Cell> currentCells = List.of(end);
		while (lowest.stream().allMatch(c -> c.getSteps() < 0)) {
			step++;
			currentCells = grid.nextStep(step, currentCells);
			if (currentCells.isEmpty())
				throw new RuntimeException("No path!");
		}
		return lowest.stream().filter(c -> c.getSteps() >= 0).findAny().orElseThrow().getSteps();
	}
	
	record Grid(Cell[][] cells, boolean backwards) {
		Cell findStart() {
			return cellStream().filter(c -> c.getLevel() == 'S').findAny().orElseThrow();
		}
		
		Cell findEnd() {
			return cellStream().filter(c -> c.getLevel() == 'E').findAny().orElseThrow();
		}
		
		List<Cell> findLowest() {
			return cellStream().filter(c -> c.levelValue() == 'a').toList();
		}
		
		private Stream<Cell> cellStream() {
			return Arrays.stream(cells)
					.flatMap(Arrays::stream);
		}
		
		Cell get(int x, int y) {
			return cells[y][x];
		}
		
		int width() {
			return cells[0].length;
		}
		
		int height() {
			return cells.length;
		}
		
		List<Cell> nextStep(int step, List<Cell> currentCells) {
			return currentCells.stream().flatMap(this::availableSteps).peek(c -> c.setSteps(step)).toList();
		}
		
		Stream<Cell> availableSteps(Cell cell) {
			return Stream.of(
							0 < cell.x ? get(cell.x - 1, cell.y) : null,
							cell.x < width() - 1 ? get(cell.x + 1, cell.y) : null,
							0 < cell.y ? get(cell.x, cell.y - 1) : null,
							cell.y < height() - 1 ? get(cell.x, cell.y + 1) : null
					)
					.filter(Objects::nonNull)
					.filter(c -> c.steps < 0)
					.filter(c -> backwards ? cell.reachableFrom(c) : c.reachableFrom(cell));
		}
	}
	
	@Data
	static class Cell {
		final int x;
		final int y;
		final char level;
		int steps = -1;
		
		char levelValue() {
			return switch (level) {
				case 'S' -> 'a';
				case 'E' -> 'z';
				default -> level;
			};
		}
		
		boolean reachableFrom(Cell cell) {
			return levelValue() - cell.levelValue() <= 1;
		}
		
		Cell copy() {
			return new Cell(x, y, level);
		}
		
		static Cell[][] copy(Cell[][] cells) {
			return Stream.of(cells)
					.map(row -> Stream.of(row).map(Cell::copy).toArray(Cell[]::new))
					.toArray(Cell[][]::new);
		}
		
		static Cell[] parseRow(int y, String row) {
			Cell[] cells = new Cell[row.length()];
			for (int i = 0; i < row.length(); i++) {
				char c = row.charAt(i);
				cells[i] = new Cell(i, y, c);
			}
			return cells;
		}
	}
}