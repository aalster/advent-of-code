package org.advent.year2021.day14;

import org.advent.common.Pair;
import org.advent.common.Utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day14 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day14.class, "input.txt");
		String template = input.nextLine();
		Map<String, String> mappings = new HashMap<>();
		while (input.hasNext()) {
			String[] split = input.nextLine().split(" -> ");
			if (split.length != 2)
				continue;
			mappings.put(split[0], split[1]);
		}
		
		System.out.println("Answer 1: " + part1(template, mappings));
		System.out.println("Answer 2: " + part2(template, mappings));
	}
	
	private static long part1(String template, Map<String, String> mappings) {
		return solveNaive(template, mappings, 10);
	}
	
	private static long part2(String template, Map<String, String> mappings) {
		return solveRecursive(template, mappings, 40);
	}
	
	@SuppressWarnings("SameParameterValue")
	private static long solveNaive(String template, Map<String, String> mappings, int totalSteps) {
		String current = template;
		StringBuilder next;
		for (int step = 0; step < totalSteps; step++) {
			next = new StringBuilder(current.length() * 2);
			for (int i = 0; i < current.length() - 1; i++) {
				String pair = current.substring(i, i + 2);
				next.append(pair.charAt(0)).append(mappings.get(pair));
			}
			next.append(current.charAt(current.length() - 1));
			current = next.toString();
		}
		Pair<Long, Long> edges = countEdges(current);
		return edges.left() - edges.right();
	}
	
	@SuppressWarnings("SameParameterValue")
	private static long solveRecursive(String template, Map<String, String> mappings, int totalSteps) {
		Map<Pair<Character, Character>, LinkedHashMap<Integer, Map<Character, Long>>> cache = new HashMap<>();
		for (Map.Entry<String, String> entry : mappings.entrySet()) {
			String pairStr = entry.getKey();
			Pair<Character, Character> pair = new Pair<>(pairStr.charAt(0), pairStr.charAt(1));
			cache.put(pair, new LinkedHashMap<>(Map.of(1, Map.of(entry.getValue().charAt(0), 1L))));
		}
		Map<Character, Long> result = new HashMap<>();
		for (int i = 0; i < template.length() - 1; i++) {
			Character left = template.charAt(i);
			Character right = template.charAt(i + 1);
			result.compute(left, (k, v) -> v == null ? 1L : v + 1);
			combine(result, List.of(middle(Pair.of(left, right), totalSteps, cache)));
		}
		result.compute(template.charAt(template.length() - 1), (k, v) -> v == null ? 1L : v + 1);
		LongSummaryStatistics stats = result.values().stream().mapToLong(v -> v).summaryStatistics();
		return stats.getMax() - stats.getMin();
	}
	
	private static Map<Character, Long> middle(Pair<Character, Character> pair, int stepsLeft,
	                                           Map<Pair<Character, Character>, LinkedHashMap<Integer, Map<Character, Long>>> cache) {
		Map<Character, Long> middle = cache.get(pair).get(stepsLeft);
		if (middle == null) {
			Map<Character, Long> level1 = cache.get(pair).get(1);
			Character level1Char = level1.keySet().iterator().next();
			Map<Character, Long> middleLeft = middle(new Pair<>(pair.left(), level1Char), stepsLeft - 1, cache);
			Map<Character, Long> middleRight = middle(new Pair<>(level1Char, pair.right()), stepsLeft - 1, cache);
			middle = combine(new HashMap<>(), List.of(middleLeft, level1, middleRight));
			cache.get(pair).put(stepsLeft, middle);
		}
		return middle;
	}
	
	private static <K> Map<K, Long> combine(Map<K, Long> dest, List<Map<K, Long>> maps) {
		maps.stream()
				.flatMap(map -> map.keySet().stream())
				.distinct()
				.map(k -> new Pair<>(k, maps.stream().mapToLong(m -> m.getOrDefault(k, 0L)).sum()))
				.forEach(pair -> dest.compute(pair.left(), (k, v) -> pair.right() + (v == null ? 0 : v)));
		return dest;
	}
	
	private static Pair<Long, Long> countEdges(String current) {
		Map<Integer, Long> counts = current.chars().boxed().collect(Collectors.groupingBy(c -> c, Collectors.counting()));
		LongSummaryStatistics stats = counts.values().stream().mapToLong(v -> v).summaryStatistics();
		return new Pair<>(stats.getMax(), stats.getMin());
	}
}