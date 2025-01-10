package org.advent.year2017.day12;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day12 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day12()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 6, 2),
				new ExpectedAnswers("input.txt", 128, 209)
		);
	}
	
	Map<Integer, List<Integer>> relations;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		relations = Utils.readLines(input).stream()
				.map(line -> line.split(" <-> "))
				.collect(Collectors.toMap(
						s -> Integer.parseInt(s[0]),
						s -> Stream.of(s[1].split(", ")).map(Integer::parseInt).toList()));
	}
	
	@Override
	public Object part1() {
		return extractGroup(relations.keySet(), 0, relations).size();
	}
	
	@Override
	public Object part2() {
		Set<Integer> elements = new HashSet<>(relations.keySet());
		List<Set<Integer>> groups = new ArrayList<>();
		while (!elements.isEmpty()) {
			Set<Integer> group = extractGroup(elements, elements.iterator().next(), relations);
			groups.add(group);
			elements.removeAll(group);
		}
		return groups.size();
	}
	
	Set<Integer> extractGroup(Set<Integer> elements, Integer first, Map<Integer, List<Integer>> relations) {
		Set<Integer> group = new HashSet<>();
		Set<Integer> current = new HashSet<>(List.of(first));
		while (!current.isEmpty()) {
			group.addAll(current);
			current = current.stream()
					.flatMap(c -> relations.get(c).stream())
					.filter(i -> !group.contains(i))
					.filter(elements::contains)
					.collect(Collectors.toSet());
		}
		return group;
	}
}