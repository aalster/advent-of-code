package org.advent.year2020.day2;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day2 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day2()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 2, 1),
				new ExpectedAnswers("input.txt", 622, 263)
		);
	}
	
	List<Password> passwords;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		passwords = Utils.readLines(input).stream().map(Password::parse).toList();
	}
	
	@Override
	public Object part1() {
		return passwords.stream().filter(Password::isValid).count();
	}
	
	@Override
	public Object part2() {
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
		
		static final Pattern pattern = Pattern.compile("(.+)-(.+) (.): (.+)");
		static Password parse(String line) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches())
				throw new RuntimeException("bad line: " + line);
			return new Password(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), matcher.group(3).charAt(0), matcher.group(4));
		}
	}
}