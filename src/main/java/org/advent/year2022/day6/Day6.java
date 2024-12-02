package org.advent.year2022.day6;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class Day6 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day6.class,"input.txt");
		List<String> lines = Utils.readLines(input);
		
		for (String line : lines)
			System.out.println("Answer 1: " + solve(line, 4));
		for (String line : lines)
			System.out.println("Answer 2: " + solve(line, 14));
	}
	
	private static int solve(String line, int size) {
		if (line.length() < size)
			return 0;
		
		Function<List<Integer>, Boolean> different = list -> list.size() == new HashSet<>(list).size();
		
		List<Integer> chars = new ArrayList<>(line.substring(0, size).chars().boxed().toList());
		if (different.apply(chars))
			return size + 1;
		for (int i = size; i < line.length(); i++) {
			int c = line.charAt(i);
			chars.add(c);
			chars.removeFirst();
			if (different.apply(chars))
				return i + 1;
		}
		return 0;
	}
}