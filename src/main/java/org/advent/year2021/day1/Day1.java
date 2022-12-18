package org.advent.year2021.day1;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day1 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day1.class, "input.txt");
		List<Integer> depths = new ArrayList<>();
		while (input.hasNext())
			depths.add(Integer.parseInt(input.nextLine()));
		
		System.out.println("Answer 1: " + part1(depths));
		System.out.println("Answer 2: " + part2(depths));
	}
	
	private static int part1(List<Integer> depths) {
		int increases = 0;
		int previousDepth = 0;
		for (Integer depth : depths) {
			if (previousDepth < depth)
				increases++;
			previousDepth = depth;
		}
		return increases - 1;
	}
	
	private static int part2(List<Integer> depths) {
		int increases = 0;
		int previousDepth = 0;
		for (int i = 0; i < depths.size() - 2; i++) {
			Integer depth = depths.get(i) + depths.get(i + 1) + depths.get(i + 2);
			if (previousDepth < depth)
				increases++;
			previousDepth = depth;
		}
		return increases - 1;
	}
}