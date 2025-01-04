package org.advent.year2016.day9;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day9 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day9()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 6, 6),
				new ExpectedAnswers("example2.txt", 7, 7),
				new ExpectedAnswers("example3.txt", 9, 9),
				new ExpectedAnswers("example4.txt", 11, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example5.txt", 6, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example6.txt", 18, 20),
				new ExpectedAnswers("example7.txt", ExpectedAnswers.IGNORE, 241920),
				new ExpectedAnswers("example8.txt", ExpectedAnswers.IGNORE, 445),
				new ExpectedAnswers("input.txt", 138735, 11125026826L)
		);
	}
	
	String line;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		line = input.nextLine();
	}
	
	@Override
	public Object part1() {
		return decompressedLength(line, false);
	}
	
	@Override
	public Object part2() {
		return decompressedLength(line, true);
	}
	
	long decompressedLength(String text, boolean versionTwo) {
		long length = 0;
		Pattern pattern = Pattern.compile("\\((\\d+)x(\\d+)\\)");
		while (!text.isEmpty()) {
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				int start = matcher.start();
				int end = matcher.end();
				int size = Integer.parseInt(matcher.group(1));
				int repeats = Integer.parseInt(matcher.group(2));
				
				long subLength = versionTwo ? decompressedLength(text.substring(end, end + size), true) : size;
				length += start + subLength * repeats;
				text = text.substring(end + size);
			} else {
				length += text.length();
				break;
			}
		}
		return length;
	}
}