package org.advent.year2016.day10;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Day10 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day10()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", ExpectedAnswers.IGNORE, 30),
				new ExpectedAnswers("input.txt", 157, 1085)
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
		Set<Integer> target = Set.of(17, 61);
		return compute(lines).entrySet().stream()
				.filter(e -> e.getValue().equals(target))
				.mapToInt(e -> Integer.parseInt(e.getKey().replace("bot ", "")))
				.findAny()
				.orElse(0);
	}
	
	@Override
	public Object part2() {
		Map<String, SortedSet<Integer>> bots = compute(lines);
		return Stream.of("output 0", "output 1", "output 2")
				.mapToInt(name -> bots.get(name).getFirst())
				.reduce(1, (a, b) -> a * b);
	}
	
	Map<String, SortedSet<Integer>> compute(List<String> lines) {
		lines = new ArrayList<>(lines);
		Map<String, SortedSet<Integer>> bots = new HashMap<>();
		while (!lines.isEmpty()) {
			String line = lines.removeFirst();
			if (line.startsWith("value")) {
				String[] split = line.replace("value ", "").split(" goes to ");
				bots.computeIfAbsent(split[1], k -> new TreeSet<>()).add(Integer.parseInt(split[0]));
			} else {
				String[] split = line.replace(" gives low to ", ",")
						.replace(" and high to ", ",").split(",");
				String bot = split[0];
				SortedSet<Integer> values = bots.get(bot);
				if (values == null || values.size() < 2) {
					lines.add(line);
					continue;
				}
				bots.computeIfAbsent(split[1], k -> new TreeSet<>()).add(values.getFirst());
				bots.computeIfAbsent(split[2], k -> new TreeSet<>()).add(values.getLast());
			}
		}
		return bots;
	}
}