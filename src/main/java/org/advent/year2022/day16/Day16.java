package org.advent.year2022.day16;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day16 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day16.class, "input.txt");
		Pattern pattern = Pattern.compile("Valve (.+) has flow rate=(.+); tunnels? leads? to valves? (.+)");
		Map<String, Valve> valves = new LinkedHashMap<>();
		while (input.hasNext()) {
			Matcher matcher = pattern.matcher(input.nextLine());
			if (matcher.find()) {
				Valve valve = Valve.parse(matcher);
				valves.put(valve.name(), valve);
			}
		}
		
		GameEngine gameEngine = new GameEngine(valves, PathService.computeAllPaths(valves));
		Valve start = valves.get("AA");
		
		System.out.println("Answer 1: " + gameEngine.maxPressure(start, 1, 30));
		System.out.println("Answer 2: " + gameEngine.maxPressure(start, 2, 26));
	}
	
	
	record Valve(String name, int rate, List<String> availableValves) {
		
		List<Valve> availableValves(Map<String, Valve> allValves) {
			return availableValves.stream().map(allValves::get).toList();
		}
		
		static Valve parse(Matcher matcher) {
			String name = matcher.group(1);
			int rate = Integer.parseInt(matcher.group(2));
			List<String> availableValves = List.of(matcher.group(3).split(", "));
			return new Valve(name, rate, availableValves);
		}
	}
	
	record PathService(Map<String, Integer> paths) {
		
		int path(Valve from, Valve to) {
			try {
				return paths.get(key(from, to));
			} catch (Exception e) {
				throw new RuntimeException("No path for " + from + " - " + to);
			}
		}
		
		static String key(Valve left, Valve right) {
			return key(left.name(), right.name());
		}
		
		static String key(String left, String right) {
			return left + "-" + right;
		}
		
		static PathService computeAllPaths(Map<String, Valve> valves) {
			Map<String, Integer> paths = new HashMap<>();
			for (Valve valve : valves.values())
				putPathsRecursive(paths, valves, valve, valve.availableValves(valves), 1);
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
	}
	
	@Data
	@AllArgsConstructor
	static class Worker {
		private final int id;
		private Valve position;
		private Valve target;
		private int remainingDistance;
		
		Worker copy() {
			return new Worker(id, position, target, remainingDistance);
		}
	}
	
	@Data
	@AllArgsConstructor
	static class GameState {
		private final Set<Valve> openValves;
		private final Map<Integer, Worker> workers;
		private int releasedPressure;
		
		void incPressure() {
			int sum = openValves.stream().mapToInt(Valve::rate).sum();
			releasedPressure += sum;
		}
		
		void openValve(Valve valve) {
			openValves.add(valve);
		}
	}
	
	
	record GameEngine(Map<String, Valve> allValves, PathService pathService) {
		
		int maxPressure(Valve start, int workersCount, int time) {
			Map<Integer, Worker> workers = IntStream.range(0, workersCount)
					.mapToObj(id -> new Worker(id, start, null, 0))
					.collect(Collectors.toMap(Worker::getId, w -> w));
			List<GameState> states = List.of(new GameState(new HashSet<>(), workers, 0));
			
			while (time > 0) {
				int timeRemaining = time;
				states = states.stream()
						.flatMap(state -> step(state, timeRemaining))
						.sorted(Comparator.comparing(GameState::getReleasedPressure).reversed())
						.limit(50_000)
						.toList();
				time--;
			}
			
			GameState result = states.stream().max(Comparator.comparing(GameState::getReleasedPressure)).orElse(null);
			if (result == null)
				return 0;
			return result.getReleasedPressure();
		}
		
		Stream<GameState> step(GameState state, int remainingTime) {
			if (state.getWorkers().isEmpty()) {
				state.incPressure();
				return Stream.of(state);
			}
			
			Worker workerWithNoTarget = state.getWorkers().values().stream().filter(w -> w.getTarget() == null).findAny().orElse(null);
			if (workerWithNoTarget != null)
				return nextStates(workerWithNoTarget, state, remainingTime).stream().flatMap(s -> step(s, remainingTime));
			
			state.incPressure();
			for (Worker worker : state.getWorkers().values()) {
				if (worker.getRemainingDistance() > 0) {
					worker.setRemainingDistance(worker.getRemainingDistance() - 1);
					continue;
				}
				state.openValve(worker.getTarget());
				worker.setPosition(worker.getTarget());
				worker.setTarget(null);
			}
			return Stream.of(state);
		}
		
		List<GameState> nextStates(Worker worker, GameState state, int remainingTime) {
			List<Valve> possibleTargets = nextTargets(state, worker.getPosition(), remainingTime);
			if (possibleTargets.isEmpty()) {
				Map<Integer, Worker> workers;
				if (state.getWorkers().size() == 1) {
					workers = Map.of();
				} else {
					workers = new HashMap<>(state.getWorkers());
					workers.remove(worker.getId());
				}
				return List.of(new GameState(new HashSet<>(state.getOpenValves()), workers, state.getReleasedPressure()));
			}
			List<GameState> nextStates = new ArrayList<>();
			for (Valve target : possibleTargets) {
				int remainingDistance = pathService.path(worker.getPosition(), target);
				Worker nextWorker = new Worker(worker.getId(), worker.getPosition(), target, remainingDistance);
				Map<Integer, Worker> workers;
				if (state.getWorkers().size() == 1) {
					workers = Map.of(nextWorker.getId(), nextWorker);
				} else {
					workers = new HashMap<>(state.getWorkers().values().stream().map(Worker::copy)
							.collect(Collectors.toMap(Worker::getId, w -> w)));
					workers.put(nextWorker.getId(), nextWorker);
				}
				GameState nextState = new GameState(new HashSet<>(state.getOpenValves()), workers, state.getReleasedPressure());
				nextStates.add(nextState);
			}
			return nextStates;
		}
		
		List<Valve> nextTargets(GameState state, Valve from, int remainingTime) {
			Set<Valve> currentTargets = state.getWorkers().values().stream()
					.map(Worker::getTarget)
					.filter(Objects::nonNull)
					.collect(Collectors.toSet());
			
			return allValves.values().stream()
					.filter(v -> v.rate() > 0)
					.filter(v -> !state.getOpenValves().contains(v))
					.filter(v -> !currentTargets.contains(v))
					.filter(v -> pathService.path(from, v) <= remainingTime)
					.toList();
		}
	}
}