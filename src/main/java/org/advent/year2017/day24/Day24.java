package org.advent.year2017.day24;

import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day24()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 31, 19),
				new ExpectedAnswers("input.txt", 2006, 1994)
		);
	}
	
	Set<int[]> components;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		components = Utils.readLines(input).stream()
				.map(line -> Arrays.stream(line.split("/")).mapToInt(Integer::parseInt).toArray())
				.collect(Collectors.toSet());
	}
	
	@Override
	public Object part1() {
		Comparator<Pair<Integer, Integer>> comparator = Comparator.comparing(Pair::right);
		return maxStrengthLongest(comparator, components, 0, 0, 0).right();
	}
	
	@Override
	public Object part2() {
		Comparator<Pair<Integer, Integer>> comparator = Comparator.comparing((Pair<Integer, Integer> p) -> p.left()).thenComparing(Pair::right);
		return maxStrengthLongest(comparator, components, 0, 0, 0).right();
	}
	
	Pair<Integer, Integer> maxStrengthLongest(Comparator<Pair<Integer, Integer>> comparator,
	                                          Set<int[]> components, int leftPort, int length, int strength) {
		int nextStrength = strength + leftPort * 2;
		int nextLength = length + 1;
		
		return Stream.concat(
						Stream.of(Pair.of(length, strength + leftPort)),
						components.stream()
								.map(component -> {
									if (component[0] == leftPort)
										return maxStrengthLongest(comparator, removed(components, component), component[1], nextLength, nextStrength);
									if (component[1] == leftPort)
										return maxStrengthLongest(comparator, removed(components, component), component[0], nextLength, nextStrength);
									return null;
								})
								.filter(Objects::nonNull))
				.max(comparator)
				.orElseThrow();
	}
	
	<T> Set<T> removed(Set<T> set, T element) {
		set = new HashSet<>(set);
		set.remove(element);
		return set;
	}
}