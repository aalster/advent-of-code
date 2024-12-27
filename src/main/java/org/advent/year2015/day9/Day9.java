package org.advent.year2015.day9;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

public class Day9 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day9()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 605, 982),
				new ExpectedAnswers("input.txt", 117, 909)
		);
	}
	
	Map<String, Map<String, Integer>> distances;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		distances = new HashMap<>();
		while (input.hasNext()) {
			String[] split = input.nextLine().replace("to", "=").split(" = ");
			int distance = Integer.parseInt(split[2]);
			distances.computeIfAbsent(split[0], k -> new HashMap<>()).put(split[1], distance);
			distances.computeIfAbsent(split[1], k -> new HashMap<>()).put(split[0], distance);
		}
	}
	
	@Override
	public Object part1() {
		return findPaths(distances).stream().mapToInt(Path::distance).min().orElseThrow();
	}
	
	@Override
	public Object part2() {
		return findPaths(distances).stream().mapToInt(Path::distance).max().orElseThrow();
	}
	
	List<Path> findPaths(Map<String, Map<String, Integer>> distances) {
		List<Path> paths = distances.keySet().stream().map(s -> new Path(s, Set.of(), 0)).toList();
		int steps = distances.size() - 1;
		while (steps > 0) {
			paths = paths.stream().flatMap(p -> p.next(distances)).toList();
			steps--;
		}
		return paths;
	}
	
	record Path(String current, Set<String> visited, int distance) {
		
		Stream<Path> next(Map<String, Map<String, Integer>> distances) {
			Set<String> nextVisited = new HashSet<>(visited);
			nextVisited.add(current);
			return distances.get(current).entrySet().stream()
					.filter(e -> !visited.contains(e.getKey()))
					.map(e -> new Path(e.getKey(), nextVisited, distance + e.getValue()));
		}
	}
}