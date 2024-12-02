package org.advent.year2022.day10;

import org.advent.common.Utils;

import java.util.List;
import java.util.Scanner;

public class Day10 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day10.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 1: " + part2(lines));
	}
	
	private static int part1(List<String> lines) {
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
	
	public static int part2(List<String> lines) {
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
					System.out.println();
				System.out.print(Math.abs(cycle % 40 - value) <= 1 ? '#' : ' ');
				cycle++;
			}
			value += delta;
		}
		System.out.println();
		return 0;
	}
}