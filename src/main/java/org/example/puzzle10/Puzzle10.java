package org.example.puzzle10;

import org.example.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.IntUnaryOperator;

public class Puzzle10 {
	
	public static void main1(String[] args) {
		Scanner input = Utils.scanFileNearClass(Puzzle10.class, "input.txt");
		int result = 0;
		int value = 1;
		int cycle = 0;
		Set<Integer> valuableCycles = Set.of(20, 60, 100, 140, 180, 220);
		while (input.hasNext()) {
			String[] split = input.nextLine().split(" ");
			int delta = 0;
			int cycles = 1;
			if ("addx".equals(split[0])) {
				cycles = 2;
				delta = Integer.parseInt(split[1]);
			}
			while (cycles > 0) {
				cycles--;
				cycle++;
				if (valuableCycles.contains(cycle))
					result += cycle * value;
			}
			value += delta;
		}
		System.out.println("Answer 1: " + result);
	}
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Puzzle10.class, "input.txt");
		int value = 1;
		int cycle = 0;
		while (input.hasNext()) {
			String[] split = input.nextLine().split(" ");
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
				System.out.print(Math.abs(cycle % 40 - value) <= 1 ? '#' : '.');
				cycle++;
			}
			value += delta;
		}
	}
}