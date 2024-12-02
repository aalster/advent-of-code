package org.advent.year2022.day3;

import org.advent.common.Utils;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day3 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day3.class,"input.txt");
		List<String> lines = Utils.readLines(input);
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static long part1(List<String> lines) {
		return lines.stream()
				.map(chars -> {
					Set<Integer> left = chars.substring(0, chars.length() / 2).chars().boxed().collect(Collectors.toSet());
					return (char) chars.substring(chars.length() / 2).chars().filter(left::contains).findAny().orElse(0);
				})
				.mapToInt(Day3::priority)
				.sum();
	}
	
	private static long part2(List<String> lines) {
		int result = 0;
		Iterator<String> iterator = lines.iterator();
		while (iterator.hasNext())
			result += priority(badge(iterator.next(), iterator.next(), iterator.next()));
		return result;
	}
	
	private static char badge(String first, String second, String third) {
		return (char) first.chars()
				.filter(c -> second.indexOf(c) >= 0)
				.filter(c -> third.indexOf(c) >= 0)
				.findAny()
				.orElse(0);
	}
	
	private static int priority(char c) {
		if ('a' <= c && c <= 'z')
			return c - 'a' + 1;
		return c - 'A' + 27;
	}
}