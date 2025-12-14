package org.advent.year2025.day12;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day12 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day12()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 2, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 569, ExpectedAnswers.IGNORE)
		);
	}
	static final boolean silent = true;
	Shape[] shapes;
	List<Region> regions;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<List<String>> groups = Utils.splitByEmptyLine(Utils.readLines(input));
		shapes = new Shape[groups.size() - 1];
		for (List<String> group : groups) {
			if (group.getFirst().contains("x")) {
				regions = group.stream().map(Region::parse).toList();
			} else {
				int index = Integer.parseInt(group.getFirst().replace(":", ""));
				shapes[index] = Shape.parse(group.subList(1, group.size()));
			}
		}
	}
	
	@Override
	public Object part1() {
		int maxPresentWidth = Arrays.stream(shapes).mapToInt(s -> s.actualWidth).max().orElseThrow();
		int maxPresentHeight = Arrays.stream(shapes).mapToInt(s -> s.actualHeight).max().orElseThrow();
		return regions.stream().filter(r -> r.fits(shapes, maxPresentWidth, maxPresentHeight)).count();
	}
	
	@Override
	public Object part2() {
		return null;
	}
	
	static class Shape {
		final char[][] cells;
		int cellsCount;
		int actualWidth;
		int actualHeight;
		char nextSymbol = 'A';
		Collection<Shape> variationsCache;
		
		Shape(char[][] cells, int cellsCount, int actualWidth, int actualHeight) {
			this.cells = cells;
			this.cellsCount = cellsCount;
			this.actualWidth = actualWidth;
			this.actualHeight = actualHeight;
		}
		
		boolean intersects(Shape other, int dx, int dy) {
			for (int y = 0; y < other.cells.length; y++) {
				char[] otherRow = other.cells[y];
				for (int x = 0; x < otherRow.length; x++)
					if (otherRow[x] != '.' && cells[y + dy][x + dx] != '.')
						return true;
			}
			return false;
		}
		
		void set(Shape other, int dx, int dy, char value) {
			for (int y = 0; y < other.cells.length; y++) {
				char[] otherRow = other.cells[y];
				for (int x = 0; x < otherRow.length; x++)
					if (otherRow[x] != '.')
						cells[y + dy][x + dx] = value;
			}
		}
		
		void add(Shape other, int dx, int dy) {
			set(other, dx, dy, nextSymbol);
			nextSymbol++;
			cellsCount += other.cellsCount;
			
			actualWidth = Math.max(actualWidth, dx + other.actualWidth);
			actualHeight = Math.max(actualHeight, dy + other.actualHeight);
		}
		
		void remove(Shape other, int dx, int dy) {
			set(other, dx, dy, '.');
			nextSymbol--;
			cellsCount -= other.cellsCount;
			
			rows: while (actualHeight > 0) {
				for (char c : cells[actualHeight - 1])
					if (c != '.')
						break rows;
				actualHeight--;
			}
			cols: while (actualWidth > 0) {
				for (char[] row : cells)
					if (row[actualWidth - 1] != '.')
						break cols;
				actualWidth--;
			}
		}
		
		Shape mirrored() {
			char[][] mirroredCells = new char[cells.length][cells[0].length];
			for (int y = 0; y < cells.length; y++) {
				char[] row = cells[y];
				char[] mirroredRow = mirroredCells[y];
				for (int x = 0; x < row.length; x++)
					mirroredRow[x] = row[row.length - x - 1];
			}
			return new Shape(mirroredCells, cellsCount, actualWidth, actualHeight);
		}
		
		Shape rotated() {
			char[][] rotatedCells = new char[cells[0].length][cells.length];
			for (int y = 0; y < rotatedCells.length; y++) {
				char[] rotatedRow = rotatedCells[y];
				for (int x = 0; x < rotatedRow.length; x++)
					rotatedRow[x] = cells[x][y];
			}
			//noinspection SuspiciousNameCombination
			return new Shape(rotatedCells, cellsCount, actualHeight, actualWidth);
		}
		
		Collection<Shape> variations() {
			if (variationsCache == null) {
				variationsCache = new HashSet<>();
				
				List<Shape> temp = List.of(this, mirrored());
				for (int i = 0; i < 3; i++) {
					temp = temp.stream().map(Shape::rotated).toList();
					variationsCache.addAll(temp);
				}
			}
			return variationsCache;
		}
		
		@Override
		public int hashCode() {
			return Arrays.deepHashCode(cells);
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj instanceof Shape other && Arrays.deepEquals(cells, other.cells);
		}
		
		@Override
		public String toString() {
			return Arrays.stream(cells).map(String::new).collect(Collectors.joining("\n"));
		}
		
		static Shape empty(int width, int height) {
			char[][] cells = new char[height][width];
			for (int y = 0; y < height; y++)
				Arrays.fill(cells[y], '.');
			return new Shape(cells, 0, 0, 0);
		}
		
		static Shape parse(List<String> lines) {
			char[][] cells = new char[lines.size()][];
			int row = 0;
			for (String line : lines)
				cells[row++] = line.toCharArray();
			int cellsCount = (int) lines.stream().mapToLong(line -> line.chars().filter(c -> c == '#').count()).sum();
			return new Shape(cells, cellsCount, cells[0].length, cells.length);
		}
	}
	
	record Region(int width, int height, int[] presents) {
		
		public boolean fits(Shape[] shapes, int maxPresentWidth, int maxPresentHeight) {
			if ((width / maxPresentWidth) * (height / maxPresentHeight) >= Arrays.stream(presents).sum())
				return true;
			if (width * height < IntStream.range(0, presents.length).map(p -> presents[p] * shapes[p].cellsCount).sum())
				return false;
			
			return fitRecursive(Shape.empty(width, height), shapes, 0, presents);
		}
		
		boolean fitRecursive(Shape field, Shape[] shapes, int index, int[] target) {
			if (!silent) {
				System.out.println(field);
				System.out.println();
				Utils.sleep(100);
			}
			
			if (index >= shapes.length)
				return true;
			if (target[index] == 0)
				return fitRecursive(field, shapes, index + 1, target);
			
			Shape shape = shapes[index];
			int[] nextTarget = Arrays.copyOf(target, target.length);
			nextTarget[index]--;
			
			for (Shape variation : shape.variations()) {
				int maxDx = Math.min(width - variation.actualWidth, field.actualWidth);
				int maxDy = Math.min(height - variation.actualHeight, field.actualHeight);
				
				for (int dx = 0; dx <= maxDx; dx++) {
					for (int dy = 0; dy <= maxDy; dy++) {
						if (field.intersects(variation, dx, dy))
							continue;
						
						field.add(variation, dx, dy);
						if (fitRecursive(field, shapes, index, nextTarget))
							return true;
						field.remove(variation, dx, dy);
					}
				}
			}
			
			return false;
		}
		
		static Region parse(String line) {
			String[] split = line.split(": ");
			String[] size = split[0].split("x");
			int[] presents = Arrays.stream(split[1].split(" ")).mapToInt(Integer::parseInt).toArray();
			return new Region(Integer.parseInt(size[0]), Integer.parseInt(size[1]), presents);
		}
	}
}