package org.advent.year2022.day12;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Day12_2 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day12_2.class, "input.txt");
		List<Cell[]> rows = new ArrayList<>();
		int y = 0;
		while (input.hasNext()) {
			rows.add(Cell.parseRow(y, input.nextLine()));
			y++;
		}
		
		Grid grid = new Grid(rows.toArray(Cell[][]::new));
		grid.printLevels();
		grid.print();
		
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
			System.out.println("\nStep: " + step);
			grid.print();
		}
		System.out.println("Start at: " + lowest.stream().filter(c -> c.getSteps() >= 0).findAny().orElse(null));
	}
	
	record Grid(Cell[][] cells) {
		Cell findStart() {
			for (Cell[] row : cells)
				for (Cell cell : row)
					if (cell.getLevel() == 'S')
						return cell;
			throw new RuntimeException("Start not found");
		}
		
		Cell findEnd() {
			for (Cell[] row : cells)
				for (Cell cell : row)
					if (cell.getLevel() == 'E')
						return cell;
			throw new RuntimeException("End not found");
		}
		
		List<Cell> findLowest() {
			return Arrays.stream(cells)
					.flatMap(Arrays::stream)
					.filter(c -> c.levelValue() == 'a')
					.toList();
		}
		
		void printLevels() {
			for (Cell[] row : cells) {
				for (Cell cell : row)
					System.out.print(cell.level);
				System.out.println();
			}
		}
		
		void print() {
			System.out.println("┼──────".repeat(width()) + "┼");
			for (Cell[] row : cells) {
				for (Cell cell : row)
					System.out.print("│" + StringUtils.leftPad((cell.steps >= 0 ? cell.steps + " " : "") + cell.level, 6));
				System.out.println("│");
				System.out.println("┼──────".repeat(row.length) + "┼");
			}
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
			List<Cell> cells = new ArrayList<>(4);
			if (0 < cell.x)
				cells.add(get(cell.x - 1, cell.y));
			if (cell.x < width() - 1)
				cells.add(get(cell.x + 1, cell.y));
			if (0 < cell.y)
				cells.add(get(cell.x, cell.y - 1));
			if (cell.y < height() - 1)
				cells.add(get(cell.x, cell.y + 1));
			return cells.stream()
					.filter(c -> c.steps < 0)
					.filter(cell::reachableFrom);
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