package org.advent.year2022.day8;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.IntUnaryOperator;

public class Day8 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day8()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 21, 8),
				new ExpectedAnswers("input.txt", 1688, 410400)
		);
	}
	
	Grid grid;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		grid = new Grid();
		while (input.hasNext())
			grid.addRow(input.nextLine().chars().map(c -> c - '0').boxed().toList());
	}
	
	@Override
	public Object part1() {
		int count = 0;
		for (int row = 0; row < grid.rows(); row++)
			for (int column = 0; column < grid.columns(); column++)
				count += grid.visible(column, row) ? 1 : 0;
		return count;
	}
	
	@Override
	public Object part2() {
		int maxDistance = 0;
		for (int row = 0; row < grid.rows(); row++) {
			for (int column = 0; column < grid.columns(); column++) {
				int distance = grid.visibleDistance(column, row);
				if (maxDistance < distance)
					maxDistance = distance;
			}
		}
		return maxDistance;
	}
	
	static class Grid {
		private final List<List<Integer>> heights = new ArrayList<>();
		
		void addRow(List<Integer> values) {
			heights.add(values);
		}
		
		int rows() {
			return heights.size();
		}
		
		int columns() {
			return heights.getFirst().size();
		}
		
		boolean visible(final int column, final int row) {
			final int value = get(column, row);
			int x = column;
			while (true) {
				x--;
				if (x < 0)
					return true;
				if (get(x, row) >= value)
					break;
			}
			x = column;
			while (true) {
				x++;
				if (x >= columns())
					return true;
				if (get(x, row) >= value)
					break;
			}
			int y = row;
			while (true) {
				y--;
				if (y < 0)
					return true;
				if (get(column, y) >= value)
					break;
			}
			y = row;
			while (true) {
				y++;
				if (y >= rows())
					return true;
				if (get(column, y) >= value)
					break;
			}
			return false;
		}
		
		int visibleDistance(final int column, final int row) {
			return visibleDistance(column, row, x -> x - 1, IntUnaryOperator.identity())
					* visibleDistance(column, row, x -> x + 1, IntUnaryOperator.identity())
					* visibleDistance(column, row, IntUnaryOperator.identity(), y -> y - 1)
					* visibleDistance(column, row, IntUnaryOperator.identity(), y -> y + 1);
		}
		
		int visibleDistance(final int column, final int row, IntUnaryOperator columnOperator, IntUnaryOperator rowOperator) {
			final int value = get(column, row);
			int x = column;
			int y = row;
			int distance = 0;
			while (true) {
				x = columnOperator.applyAsInt(x);
				y = rowOperator.applyAsInt(y);
				if (x < 0 || y < 0 || x >= columns() || y >= rows())
					break;
				if (get(x, y) >= value) {
					distance++;
					break;
				}
				distance++;
			}
			return distance;
		}
		
		int get(int x, int y) {
			return heights.get(y).get(x);
		}
	}
}