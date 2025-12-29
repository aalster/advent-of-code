package org.advent.year2020.day17;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day17 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day17()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 112, 848),
				new ExpectedAnswers("input.txt", 269, 1380)
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
		field = field.extend(1, 1, 1, 1);
		for (int i = 0; i < 6; i++)
			field = field.extend(0, 1, 1, 1).next();
		return field.countActive();
	}
	
	@Override
	public Object part2() {
		field = field.extend(1, 1, 1, 1);
		for (int i = 0; i < 6; i++)
			field = field.extend(1, 1, 1, 1).next();
		return field.countActive();
	}
	
	record Field(int[][][][] cells, int wWidth, int zWidth, int yWidth, int xWidth) {
		Field(int[][][][] cells) {
			this(cells, cells.length, cells[0].length, cells[0][0].length, cells[0][0][0].length);
		}
		
		@SuppressWarnings("SameParameterValue")
		Field extend(int pw, int pz, int py, int px) {
			int[][][][] next = new int[wWidth + pw * 2][zWidth + pz * 2][yWidth + py * 2][xWidth + px * 2];
			for (int w = 0; w < wWidth; w++)
				for (int z = 0; z < zWidth; z++)
					for (int y = 0; y < yWidth; y++)
						System.arraycopy(cells[w][z][y], 0, next[w + pw][z + pz][y + py], px, xWidth);
			return new Field(next);
		}
		
		Field next() {
			int[][][][] next = new int[wWidth][zWidth][yWidth][xWidth];
			for (int w = 1; w < wWidth - 1; w++) {
				for (int z = 1; z < zWidth - 1; z++) {
					for (int y = 1; y < yWidth - 1; y++) {
						for (int x = 1; x < xWidth - 1; x++) {
							int neighbors = neighbors(w, z, y, x);
							if (neighbors == 3 || (neighbors == 2 && cells[w][z][y][x] == 1))
								next[w][z][y][x] = 1;
						}
					}
				}
			}
			return new Field(next);
		}
		
		int neighbors(int w, int z, int y, int x) {
			int neighbors = 0;
			for (int dw = -1; dw <= 1; dw++)
				for (int dz = -1; dz <= 1; dz++)
					for (int dy = -1; dy <= 1; dy++)
						for (int dx = -1; dx <= 1; dx++)
							neighbors += cells[w + dw][z + dz][y + dy][x + dx];
			return neighbors - cells[w][z][y][x];
		}
		
		long countActive() {
			return Arrays.stream(cells).flatMap(Arrays::stream).flatMap(Arrays::stream).flatMapToInt(Arrays::stream).sum();
		}
		
		static Field parse(List<String> lines) {
			int[][][][] cells = new int[1][1][][];
			cells[0][0] = lines.stream().map(l -> l.chars().map(c -> c == '#' ? 1 : 0).toArray()).toArray(int[][]::new);
			return new Field(cells);
		}
	}
}