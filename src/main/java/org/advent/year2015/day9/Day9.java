package org.advent.year2015.day9;

import org.advent.common.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

public class Day9 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day9.class, "input.txt");
		Map<String, Map<String, Integer>> distances = new HashMap<>();
		while (input.hasNext()) {
			String[] split = input.nextLine().replace("to", "=").split(" = ");
			int distance = Integer.parseInt(split[2]);
			distances.computeIfAbsent(split[0], k -> new HashMap<>()).put(split[1], distance);
			distances.computeIfAbsent(split[1], k -> new HashMap<>()).put(split[0], distance);
		}
		
		System.out.println("Answer 1: " + part1(distances));
		System.out.println("Answer 2: " + part2(distances));
	}
	
	private static long part1(Map<String, Map<String, Integer>> distances) {
		return findPaths(distances).stream().mapToInt(Path::distance).min().orElseThrow();
	}
	
	private static long part2(Map<String, Map<String, Integer>> distances) {
		return findPaths(distances).stream().mapToInt(Path::distance).max().orElseThrow();
	}
	
	private static List<Path> findPaths(Map<String, Map<String, Integer>> distances) {
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