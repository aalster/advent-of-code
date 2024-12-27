package org.advent.year2024.day3;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day3()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 161, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 48),
				new ExpectedAnswers("input.txt", 174561379, 106921067)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		lines = Utils.readLines(Utils.scanFileNearClass(getClass(), file));
	}
	
	@Override
	public Object part1() {
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
	
	@Override
	public Object part2() {
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