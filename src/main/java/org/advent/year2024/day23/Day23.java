package org.advent.year2024.day23;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day23 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day23.class, "input.txt");
		Map<String, Set<String>> connections = new HashMap<>();
		for (String line : Utils.readLines(input)) {
			String[] split = line.split("-");
			connections.computeIfAbsent(split[0], k -> new HashSet<>()).add(split[1]);
			connections.computeIfAbsent(split[1], k -> new HashSet<>()).add(split[0]);
		}
		
		System.out.println("Answer 1: " + part1(connections));
		System.out.println("Answer 2: " + part2(connections));
	}
	
	private static long part1(Map<String, Set<String>> connections) {
		Set<Set<String>> triples = new HashSet<>();
		for (Map.Entry<String, Set<String>> entry : connections.entrySet())
			if (entry.getKey().startsWith("t") && entry.getValue().size() > 1)
				for (String first : entry.getValue())
					for (String second : entry.getValue())
						if (connections.get(first).contains(second))
							triples.add(Set.of(entry.getKey(), first, second));
		return triples.size();
	}
	
	private static String part2(Map<String, Set<String>> connections) {
		List<Set<String>> parties = new ArrayList<>();
		for (String player : connections.keySet())
			parties.add(new HashSet<>(List.of(player)));
		
		for (Map.Entry<String, Set<String>> entry : connections.entrySet())
			for (Set<String> party : parties)
				if (entry.getValue().containsAll(party))
					party.add(entry.getKey());
		
		Set<String> maxParty = new HashSet<>(parties).stream().max(Comparator.comparing(Set::size)).orElseThrow();
		return maxParty.stream().sorted(Comparator.naturalOrder()).collect(Collectors.joining(","));
	}
}