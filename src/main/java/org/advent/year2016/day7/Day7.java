package org.advent.year2016.day7;

import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day7 extends AdventDay {
	
	public static void main(String[] args) {
		DayRunner runner = new DayRunner(new Day7());
//		runner.run("example.txt", 1);
		runner.runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 2, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 3),
				new ExpectedAnswers("input.txt", 118, 260)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
	}
	
	@Override
	public Object part1() {
		return lines.stream().filter(this::supportsTls).count();
	}
	
	@Override
	public Object part2() {
		return lines.stream().filter(this::supportsSsl).count();
	}
	
	boolean supportsTls(String line) {
		Pair<List<String>, List<String>> parts = parts(line);
		return parts.right().stream().noneMatch(this::hasAbba) && parts.left().stream().anyMatch(this::hasAbba);
	}
	
	boolean hasAbba(String text) {
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length - 3; i++) {
			if (chars[i] == chars[i + 3] && chars[i + 1] == chars[i + 2] && chars[i] != chars[i + 1])
				return true;
		}
		return false;
	}
	
	boolean supportsSsl(String line) {
		Pair<List<String>, List<String>> parts = parts(line);
		Set<String> outsideAbas = parts.left().stream().flatMap(s -> allAba(s).stream()).collect(Collectors.toSet());
		return parts.right().stream()
				.flatMap(s -> allAba(s).stream())
				.distinct()
				.map(bab -> bab.substring(1) + bab.charAt(1))
				.anyMatch(outsideAbas::contains);
	}
	
	Set<String> allAba(String text) {
		Set<String> result = new HashSet<>();
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length - 2; i++) {
			if (chars[i] == chars[i + 2] && chars[i] != chars[i + 1])
				result.add(text.substring(i, i + 3));
		}
		return result;
	}
	
	Pair<List<String>, List<String>> parts(String line) {
		List<String> outside = new ArrayList<>();
		List<String> inside = new ArrayList<>();
		while (!line.isEmpty()) {
			if (line.startsWith("[")) {
				int end = line.indexOf(']');
				inside.add(line.substring(1, end));
				line = line.substring(end + 1);
			} else {
				int insideStart = line.indexOf('[');
				if (insideStart == -1) {
					outside.add(line);
					line = "";
				} else {
					outside.add(line.substring(0, insideStart));
					line = line.substring(insideStart);
				}
			}
		}
		return Pair.of(outside, inside);
	}
}