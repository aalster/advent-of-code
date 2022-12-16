package org.example.puzzle16;

import org.example.Utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Puzzle16 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Puzzle16.class, "example.txt");
		Pattern pattern = Pattern.compile("Valve (.+) has flow rate=(.+); tunnels? leads? to valves? (.+)");
		Map<String, Valve> valves = new LinkedHashMap<>();
		while (input.hasNext()) {
			Matcher matcher = pattern.matcher(input.nextLine());
			if (!matcher.find())
				continue;
			String name = matcher.group(1);
			int rate = Integer.parseInt(matcher.group(2));
			List<String> availableValves = List.of(matcher.group(3).split(", "));
			valves.put(name, new Valve(name, rate, availableValves));
		}
		
		PathService pathService = PathService.computeAllPaths(valves);
		for (Map.Entry<String, Integer> entry : pathService.paths().entrySet())
			System.out.println(entry.getKey() + ": " + entry.getValue());
		
		ValveSystem system = new ValveSystem(valves);
		Valve start = valves.get("AA");
		int time = 30;
		int workers = 1;
		
		System.out.println(maxPressure(system, pathService, start, time, 0));
	}
	
	static int maxPressure(ValveSystem system, PathService pathService, Valve currentValve, int timeLeft, int pressureReleased) {
		ValveSystem currentSystem = system.openValveAndClone(currentValve);
		int currentTimeLeft = timeLeft - 1;
		int currentPressureReleased = pressureReleased + currentSystem.currentRate();
		
		List<Valve> nextValves = currentSystem.closedUsefulValves();
		return nextValves.stream()
				.filter(v -> pathService.path(currentValve, v) <= currentTimeLeft)
				.mapToInt(valve -> {
					int distance = pathService.path(currentValve, valve);
					int nextTimeLeft = currentTimeLeft - distance;
					int nextPressureReleased = currentPressureReleased + distance * currentSystem.currentRate();
					return maxPressure(currentSystem, pathService, valve, nextTimeLeft, nextPressureReleased);
				})
				.max()
				.orElse(pressureReleased + timeLeft * currentSystem.currentRate());
	}
	
	record Valve(String name, int rate, List<String> availableValves) {
		List<Valve> availableValves(Map<String, Valve> allValves) {
			return availableValves.stream().map(allValves::get).toList();
		}
	}
	
	record ValveSystem(Map<String, Valve> valves, Set<Valve> openValves) {
		ValveSystem(Map<String, Valve> valves) {
			this(valves, Set.of());
		}
		
		List<Valve> closedUsefulValves() {
			return valves.values().stream()
					.filter(v -> v.rate > 0)
					.filter(v -> !openValves.contains(v))
					.toList();
		}
		
		ValveSystem openValveAndClone(Valve valve) {
			Set<Valve> openValvesClone = new LinkedHashSet<>(openValves);
			openValvesClone.add(valve);
			return new ValveSystem(valves, openValvesClone);
		}
		
		int currentRate() {
			return openValves.stream().mapToInt(Valve::rate).sum();
		}
	}
	
	record PathService(Map<String, Integer> paths) {
		static PathService computeAllPaths(Map<String, Valve> valves) {
			Map<String, Integer> paths = new HashMap<>();
			for (Valve valve : valves.values()) {
				putPathsRecursive(paths, valves, valve, valve.availableValves(valves), 1);
			}
			return new PathService(paths);
		}
		
		static void putPathsRecursive(Map<String, Integer> paths, Map<String, Valve> valves, Valve start,
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
		
		int path(Valve from, Valve to) {
			try {
				return paths.get(key(from, to));
			} catch (Exception e) {
				throw new RuntimeException("No path for " + from + " - " + to);
			}
		}
		
		static String key(Valve left, Valve right) {
			return key(left.name, right.name);
		}
		
		static String key(String left, String right) {
			return left + "-" + right;
		}
	}
}