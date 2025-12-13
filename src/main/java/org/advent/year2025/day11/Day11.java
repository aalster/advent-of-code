package org.advent.year2025.day11;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day11 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day11()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 5, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 2),
				new ExpectedAnswers("input.txt", 603, 380961604031372L)
		);
	}
	
	Map<String, List<String>> connections;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		connections = Utils.readLines(input).stream().map(l -> l.split(": ")).collect(Collectors.toMap(
				s -> s[0], s -> List.of(s[1].split(" "))));
	}
	
	@Override
	public Object part1() {
		return solve("you", "out", Set.of(), new HashMap<>());
	}
	
	@Override
	public Object part2() {
		return solve("svr", "out", Set.of("dac", "fft"), new HashMap<>());
	}
	
	long solve(String current, String target, Set<String> requirements, Map<Key, Long> cache) {
		if (current.equals(target))
			return requirements.isEmpty() ? 1 : 0;
		
		Set<String> nextRequirements = requirements.contains(current) ? Utils.removeFromSet(requirements, current) : requirements;
		Key key = new Key(current, nextRequirements);
		Long cached = cache.get(key);
		
		if (cached == null) {
			cached = connections.getOrDefault(current, List.of()).stream()
					.mapToLong(next -> solve(next, target, nextRequirements, cache))
					.sum();
			cache.put(key, cached);
		}
		return cached;
	}
	
	record Key(String current, Set<String> requirements) {
	}
}