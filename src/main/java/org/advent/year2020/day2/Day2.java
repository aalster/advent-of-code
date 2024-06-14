package org.advent.year2020.day2;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day2 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day2.class, "input.txt");
		List<Password> passwords = new ArrayList<>();
		while (input.hasNext()) {
			passwords.add(Password.parse(input.nextLine()));
		}
		
		System.out.println("Answer 1: " + part1(passwords));
		System.out.println("Answer 2: " + part2(passwords));
	}
	
	private static long part1(List<Password> passwords) {
		return passwords.stream().filter(Password::isValid).count();
	}
	
	private static long part2(List<Password> passwords) {
		return passwords.stream().filter(Password::isValid2).count();
	}
	
	record Password(int from, int to, char letter, String password) {
		
		boolean isValid() {
			int count = (int) password.chars().filter(c -> c == letter).count();
			return from <= count && count <= to;
		}
		
		boolean isValid2() {
			char left = password.charAt(from - 1);
			char right = password.charAt(to - 1);
			return (left == letter) != (right == letter);
		}
		
		private static final Pattern pattern = Pattern.compile("(.+)-(.+) (.): (.+)");
		static Password parse(String line) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches())
				throw new RuntimeException("bad line: " + line);
			return new Password(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), matcher.group(3).charAt(0), matcher.group(4));
		}
	}
}