package org.advent.year2023.day4;

import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day4 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day4.class, "input.txt");
		List<String> lines = new ArrayList<>();
		while (input.hasNext()) {
			lines.add(input.nextLine());
		}
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static long part1(List<String> data) {
		int result = 0;
		for (String line : data) {
			String[] split = line.split(":")[1].split("\\|");
			Set<Integer> winningsNumbers = Arrays.stream(split[0].split(" "))
					.filter(StringUtils::isNotBlank)
					.map(Integer::parseInt)
					.collect(Collectors.toSet());
			int count = (int) Arrays.stream(split[1].split(" "))
					.filter(StringUtils::isNotBlank)
					.map(Integer::parseInt)
					.filter(winningsNumbers::contains)
					.count();
			result += 1 << (count - 1);
		}
		return result;
	}
	
	private static long part2(List<String> data) {
		int[] copies = new int[data.size()];
		Arrays.fill(copies, 1);
		
		int id = 0;
		for (String line : data) {
			String[] split = line.split(":")[1].split("\\|");
			Set<Integer> winningsNumbers = Arrays.stream(split[0].split(" "))
					.filter(StringUtils::isNotBlank)
					.map(Integer::parseInt)
					.collect(Collectors.toSet());
			int count = (int) Arrays.stream(split[1].split(" "))
					.filter(StringUtils::isNotBlank)
					.map(Integer::parseInt)
					.filter(winningsNumbers::contains)
					.count();
			int instances = copies[id];
			for (int i = id + 1; i <= id + count; i++) {
				copies[i] = copies[i] + instances;
			}
			id++;
		}
		return Arrays.stream(copies).sum();
	}
}