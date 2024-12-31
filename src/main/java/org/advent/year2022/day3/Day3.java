package org.advent.year2022.day3;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day3 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day3()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 157, 70),
				new ExpectedAnswers("input.txt", 8085, 2515)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		lines = Utils.readLines(Utils.scanFileNearClass(getClass(), file));
	}
	
	@Override
	public Object part1() {
		return lines.stream()
				.map(chars -> {
					Set<Integer> left = chars.substring(0, chars.length() / 2).chars().boxed().collect(Collectors.toSet());
					return (char) chars.substring(chars.length() / 2).chars().filter(left::contains).findAny().orElse(0);
				})
				.mapToInt(this::priority)
				.sum();
	}
	
	@Override
	public Object part2() {
		int result = 0;
		Iterator<String> iterator = lines.iterator();
		while (iterator.hasNext())
			result += priority(badge(iterator.next(), iterator.next(), iterator.next()));
		return result;
	}
	
	char badge(String first, String second, String third) {
		return (char) first.chars()
				.filter(c -> second.indexOf(c) >= 0)
				.filter(c -> third.indexOf(c) >= 0)
				.findAny()
				.orElse(0);
	}
	
	int priority(char c) {
		if ('a' <= c && c <= 'z')
			return c - 'a' + 1;
		return c - 'A' + 27;
	}
}