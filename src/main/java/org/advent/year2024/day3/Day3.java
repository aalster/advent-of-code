package org.advent.year2024.day3;

import org.advent.common.Utils;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day3.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static long part1(List<String> lines) {
		Pattern pattern = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");
		long result = 0;
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			while (matcher.find()) {
				String left = matcher.group(1);
				String right = matcher.group(2);
				result += Long.parseLong(left) * Long.parseLong(right);
			}
		}
		return result;
	}
	
	private static long part2(List<String> lines) {
		Pattern pattern = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)|do\\(\\)|don't\\(\\)");
		boolean enabled = true;
		long result = 0;
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			while (matcher.find()) {
				String instruction = matcher.group();
				if ("do()".equals(instruction)) {
					enabled = true;
					continue;
				}
				if ("don't()".equals(instruction)) {
					enabled = false;
					continue;
				}
				if (enabled) {
					String left = matcher.group(1);
					String right = matcher.group(2);
					result += Long.parseLong(left) * Long.parseLong(right);
				}
			}
		}
		return result;
	}
}