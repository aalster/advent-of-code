package org.advent.year2022.day10;

import org.advent.common.AsciiLetters;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day10 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day10()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 13140, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 14040, "ZGCJZJFL")
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
	}
	
	@Override
	public Object part1() {
		int result = 0;
		int value = 1;
		int cycle = 0;
		for (String line : lines) {
			String[] split = line.split(" ");
			int delta = 0;
			int cycles = 1;
			if ("addx".equals(split[0])) {
				cycles = 2;
				delta = Integer.parseInt(split[1]);
			}
			while (cycles > 0) {
				cycles--;
				cycle++;
				if ((cycle - 20) % 40 == 0)
					result += cycle * value;
			}
			value += delta;
		}
		return result;
	}
	
	@Override
	public Object part2() {
		StringBuilder output = new StringBuilder();
		int value = 1;
		int cycle = 0;
		for (String line : lines) {
			String[] split = line.split(" ");
			int delta = 0;
			int cycles = 1;
			if ("addx".equals(split[0])) {
				cycles = 2;
				delta = Integer.parseInt(split[1]);
			}
			while (cycles > 0) {
				cycles--;
				if (cycle % 40 == 0)
					output.append('\n');
				output.append(Math.abs(cycle % 40 - value) <= 1 ? '#' : ' ');
				cycle++;
			}
			value += delta;
		}
		return AsciiLetters.parse(output.toString(), '#');
	}
}