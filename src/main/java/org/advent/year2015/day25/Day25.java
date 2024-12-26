package org.advent.year2015.day25;

import org.advent.common.Utils;

import java.util.Scanner;

public class Day25 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day25.class, "input.txt");
		String[] split = input.nextLine()
				.replace(" column ", "").replace(".", "")
				.split("row ")[1].split(",");
		int row = Integer.parseInt(split[0]);
		int column = Integer.parseInt(split[1]);
		
		System.out.println("Answer: " + part1(row, column));
	}
	
	private static long part1(int row, int column) {
		int diagonalStartRow = row + column - 1;
		int index = diagonalStartRow * (diagonalStartRow - 1) / 2 + column - 1;
		
		long number = 20151125;
		while (index-- > 0)
			number = number * 252533 % 33554393;
		return number;
	}
}