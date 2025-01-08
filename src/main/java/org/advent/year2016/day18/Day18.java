package org.advent.year2016.day18;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day18 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day18()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 6, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 38, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 2005, 20008491)
		);
	}
	
	String line;
	int rows;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		line = input.nextLine();
		rows = switch (file) {
			case "example.txt" -> 3;
			case "example2.txt" -> 10;
			case "input.txt" -> 40;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		return solve(line, rows);
	}
	
	@Override
	public Object part2() {
		return solve(line, 400000);
	}
	
	int solve(String line, int rows) {
		int width = line.length();
		int[] traps = new int[width + 2];
		int trapsCount = 0;
		for (int i = 1; i < traps.length - 1; i++) {
			traps[i] = line.charAt(i - 1) == '^' ? 1 : 0;
			trapsCount += traps[i];
		}
		int row = 1;
		while (row < rows) {
			int[] nextTraps = new int[width + 2];
			for (int i = 1; i < nextTraps.length - 1; i++) {
				nextTraps[i] = traps[i - 1] ^ traps[i + 1];
				trapsCount += nextTraps[i];
			}
			traps = nextTraps;
			row++;
		}
		return width * rows - trapsCount;
	}
}