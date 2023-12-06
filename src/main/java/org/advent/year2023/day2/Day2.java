package org.advent.year2023.day2;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day2 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day2.class, "example.txt");
		List<String> lines = new ArrayList<>();
		while (input.hasNext()) {
			lines.add(input.nextLine());
		}
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2());
	}
	
	private static int part1(List<String> data) {
		return 0;
	}
	
	private static int part2() {
		return 0;
	}
}