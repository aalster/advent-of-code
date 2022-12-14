package org.example.puzzle3;

import org.example.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Puzzle3 {
	
	public static void main1(String[] args) {
		Scanner input = Utils.scanFileNearClass(Puzzle3.class,"input.txt");
		System.out.println(input.tokens().map(Puzzle3::common).mapToInt(Puzzle3::priority).sum());
	}
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Puzzle3.class,"input.txt");
		int result = 0;
		while (input.hasNext())
			result += priority(badge(input.nextLine(), input.nextLine(), input.nextLine()));
		System.out.println(result);
	}
	
	private static char badge(String first, String second, String third) {
		return (char) first.chars()
				.filter(c -> second.indexOf(c) >= 0)
				.filter(c -> third.indexOf(c) >= 0)
				.findAny()
				.orElse(0);
	}
	
	private static char common(String chars) {
		Set<Integer> left = chars.substring(0, chars.length() / 2).chars().boxed().collect(Collectors.toSet());
		return (char) chars.substring(chars.length() / 2).chars().filter(left::contains).findAny().orElse(0);
	}
	
	private static int priority(char c) {
		if ('a' <= c && c <= 'z')
			return c - 'a' + 1;
		return c - 'A' + 27;
	}
}