package org.advent.year2022.day16;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

record PathService(Map<String, Integer> paths) {
	
	public int path(Valve from, Valve to) {
		try {
			return paths.get(key(from, to));
		} catch (Exception e) {
			throw new RuntimeException("No path for " + from + " - " + to);
		}
	}
	
	private static String key(Valve left, Valve right) {
		return key(left.name(), right.name());
	}
	
	private static String key(String left, String right) {
		return left + "-" + right;
	}
	
	public static PathService computeAllPaths(Map<String, Valve> valves) {
		Map<String, Integer> paths = new HashMap<>();
		for (Valve valve : valves.values()) {
			putPathsRecursive(paths, valves, valve, valve.availableValves(valves), 1);
		}
		return new PathService(paths);
	}
	
	private static void putPathsRecursive(Map<String, Integer> paths, Map<String, Valve> valves, Valve start,
	                              Collection<Valve> inspectingValves, int time) {
		Set<Valve> next = new HashSet<>();
		for (Valve valve : inspectingValves) {
			if (valve.equals(start))
				continue;
			String key = key(start, valve);
			if (paths.containsKey(key))
				continue;
			paths.put(key, time);
			next.addAll(valve.availableValves(valves));
		}
		if (!next.isEmpty())
			putPathsRecursive(paths, valves, start, next, time + 1);
	}
}
