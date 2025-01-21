package org.advent.year2018.day11;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day11 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day11()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "33,45", "90,269,16"),
				new ExpectedAnswers("example2.txt", "21,61", "232,251,12"),
				new ExpectedAnswers("input.txt", "243,64", "90,101,15")
		);
	}
	
	int serialNumber;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		serialNumber = input.nextInt();
	}
	
	// https://www.youtube.com/watch?v=d3prl6ECWt4&ab_channel=UncleScientist
	@Override
	public Object part1() {
		int[][] summedArea = summedArea(createGrid(serialNumber));
		SquareInfo maxSquare = maxSquare(summedArea, 3);
		return (maxSquare.x + 1) + "," + (maxSquare.y + 1);
	}
	
	@Override
	public Object part2() {
		int[][] summedArea = summedArea(createGrid(serialNumber));
		SquareInfo maxSquare = new SquareInfo(-1, -1, 0, Integer.MIN_VALUE);
		
		for (int size = 1; size <= summedArea.length; size++) {
			SquareInfo square = maxSquare(summedArea, size);
			if (maxSquare.powerLevel < square.powerLevel)
				maxSquare = square;
		}
		return (maxSquare.x + 1) + "," + (maxSquare.y + 1) + "," + maxSquare.size;
	}
	
	SquareInfo maxSquare(int[][] summedArea, int size) {
		int maxPowerLevel = Integer.MIN_VALUE;
		int maxX = 0;
		int maxY = 0;
		
		for (int y = 0; y < summedArea.length - size; y++) {
			for (int x = 0; x < summedArea.length - size; x++) {
				int powerLevel = summedArea[x + size][y + size] - summedArea[x + size][y] - summedArea[x][y + size] + summedArea[x][y];
				if (maxPowerLevel < powerLevel) {
					maxPowerLevel = powerLevel;
					maxX = x;
					maxY = y;
				}
			}
		}
		return new SquareInfo(maxX, maxY, size, maxPowerLevel);
	}
	
	record SquareInfo(int x, int y, int size, int powerLevel) {
	}
	
	int[][] createGrid(int serialNumber) {
		int[][] grid = new int[300][300];
		for (int x = 0; x < grid.length; x++)
			for (int y = 0; y < grid.length; y++)
				grid[x][y] = powerLevel(x + 1, y + 1, serialNumber);
		return grid;
	}
	
	int[][] summedArea(int[][] grid) {
		int[][] summedArea = new int[grid.length + 1][grid.length + 1];
		for (int y = 0; y < summedArea.length; y++)
			for (int x = 0; x < summedArea.length; x++)
				summedArea[x][y] = x == 0 || y == 0 ? -9
						: grid[x - 1][y - 1] + summedArea[x][y - 1] + summedArea[x - 1][y] - summedArea[x - 1][y - 1];
		return summedArea;
	}
	
	int powerLevel(int x, int y, int serialNumber) {
		int powerLevel = ((x + 10) * y + serialNumber) * (x + 10);
		return (powerLevel / 100) % 10 - 5;
	}
}