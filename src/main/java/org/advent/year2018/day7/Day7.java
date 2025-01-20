package org.advent.year2018.day7;

import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SequencedSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Day7 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day7()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "CABDFE", 15),
				new ExpectedAnswers("input.txt", "GRTAHKLQVYWXMUBCZPIJFEDNSO", 1115)
		);
	}
	
	Map<String, Set<String>> requirements;
	int workers;
	int extraTime;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		requirements = Utils.readLines(input).stream().map(l -> l.split(" "))
				.collect(Collectors.groupingBy(s -> s[7], Collectors.mapping(s -> s[1], Collectors.toSet())));
		workers = switch (file) {
			case "example.txt" -> 2;
			case "input.txt" -> 5;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
		extraTime = switch (file) {
			case "example.txt" -> 0;
			case "input.txt" -> 60;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		return solve(requirements, 1, 0).left();
	}
	
	@Override
	public Object part2() {
		return solve(requirements, workers, extraTime).right();
	}
	
	Pair<String, Integer> solve(Map<String, Set<String>> requirements, int workers, int extraTime) {
		Set<String> unprocessed = new TreeSet<>(requirements.keySet());
		requirements.values().forEach(unprocessed::addAll);
		
		SequencedSet<String> completed = new LinkedHashSet<>();
		Map<String, Integer> running = new HashMap<>();
		int totalTime = 0;
		
		while (!unprocessed.isEmpty() || !running.isEmpty()) {
			int freeWorkers = workers - running.size();
			List<String> available = unprocessed.stream()
					.filter(s -> completed.containsAll(requirements.getOrDefault(s, Set.of())))
					.limit(freeWorkers)
					.toList();
			for (String s : available) {
				unprocessed.remove(s);
				running.put(s, extraTime + s.charAt(0) - 'A' + 1);
			}
			int minTime = running.values().stream().mapToInt(i -> i).min().orElseThrow();
			for (String s : new ArrayList<>(running.keySet())) {
				int time = running.get(s);
				if (time == minTime) {
					completed.add(s);
					running.remove(s);
				} else {
					running.put(s, time - minTime);
				}
			}
			totalTime += minTime;
		}
		return Pair.of(String.join("", completed), totalTime);
	}
}