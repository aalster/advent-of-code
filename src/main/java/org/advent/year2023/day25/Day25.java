package org.advent.year2023.day25;

import org.advent.common.Pair;
import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day25 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day25.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		
		System.out.println("Answer 1: " + part1(lines));
	}
	
	private static long part1(List<String> lines) {
		Map<String, Set<String>> connections = new HashMap<>();
		for (String line : lines) {
			String[] split = line.split(":");
			String left = split[0];
			List<String> rights = Arrays.stream(split[1].split(" ")).filter(StringUtils::isNotBlank).toList();
			
			connections.computeIfAbsent(left, k -> new HashSet<>()).addAll(rights);
			for (String right : rights)
				connections.computeIfAbsent(right, k -> new HashSet<>()).add(left);
		}
		
		String start = connections.keySet().iterator().next();
		
		for (String end : connections.keySet()) {
			if (start.equals(end))
				continue;
			
			Set<Pair<String, String>> attemptedRemoves = new LinkedHashSet<>();
			
			Path path0 = Objects.requireNonNull(shortestPath(connections, start, end));
			for (Pair<String, String> connection1 : path0.usedConnections()) {
				Map<String, Set<String>> removed1 = removeConnection(connections, connection1);
				Path path1 = Objects.requireNonNull(shortestPath(removed1, start, end));
				for (Pair<String, String> connection2 : path1.usedConnections()) {
					if (attemptedRemoves.contains(connection2))
						continue;
					
					Map<String, Set<String>> removed2 = removeConnection(removed1, connection2);
					Path path2 = Objects.requireNonNull(shortestPath(removed2, start, end));
					for (Pair<String, String> connection3 : path2.usedConnections()) {
						if (attemptedRemoves.contains(connection3))
							continue;
						
						Map<String, Set<String>> removed3 = removeConnection(removed2, connection3);
						Path path3 = shortestPath(removed3, start, end);
						if (path3 == null)
							return splitByGroups(removed3).stream().mapToInt(Set::size).reduce(1, (a, b) -> a * b);
					}
				}
				attemptedRemoves.add(connection1);
			}
		}
		return 0;
	}
	
	static Map<String, Set<String>> removeConnection(Map<String, Set<String>> connections, Pair<String, String> remove) {
		Map<String, Set<String>> result = new HashMap<>();
		for (Map.Entry<String, Set<String>> entry : connections.entrySet())
			result.put(entry.getKey(), new HashSet<>(entry.getValue()));
		result.get(remove.left()).remove(remove.right());
		result.get(remove.right()).remove(remove.left());
		return result;
	}
	
	static List<Set<String>> splitByGroups(Map<String, Set<String>> connections) {
		Set<String> groupsToCheck = new HashSet<>(connections.keySet());
		List<Set<String>> groups = new ArrayList<>();
		
		while (!groupsToCheck.isEmpty()) {
			Set<String> currentGroup = new HashSet<>();
			List<String> currents = List.of(groupsToCheck.iterator().next());
			while (!currents.isEmpty()) {
				currents = currents.stream()
						.peek(currentGroup::add)
						.peek(groupsToCheck::remove)
						.flatMap(c -> connections.getOrDefault(c, Set.of()).stream())
						.filter(next -> !currentGroup.contains(next))
						.toList();
			}
			groups.add(currentGroup);
		}
		return groups;
	}
	
	static Path shortestPath(Map<String, Set<String>> connections, String start, String end) {
		List<Path> currents = List.of(new Path(start, Set.of()));
		while (!currents.isEmpty()) {
			Optional<Path> result = currents.stream().filter(p -> p.current.equals(end)).findAny();
			if (result.isPresent())
				return result.get();
			
			currents = currents.stream().flatMap(p -> p.step(connections)).toList();
			Set<String> allVisited = currents.stream().flatMap(p -> p.visited.stream()).collect(Collectors.toSet());
			currents = currents.stream().filter(p -> !allVisited.contains(p.current)).toList();
		}
		return null;
	}
	
	record Path(String current, Set<String> visited) {
		
		List<Pair<String, String>> usedConnections() {
			String[] array = visited.toArray(String[]::new);
			List<Pair<String, String>> connections = new ArrayList<>();
			for (int i = 1; i < array.length; i++)
				connections.add(Pair.of(array[i - 1], array[i]));
			connections.add(Pair.of(array[array.length - 1], current));
			return connections;
		}
		
		Stream<Path> step(Map<String, Set<String>> connections) {
			Set<String> nextVisited = new LinkedHashSet<>(visited);
			nextVisited.add(current);
			
			return connections.get(current).stream()
					.filter(next -> !visited.contains(next))
					.map(next -> new Path(next, nextVisited));
		}
	}
}