package org.advent.year2022.day16;

import lombok.AllArgsConstructor;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day16 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day16()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 1651, 1707),
				new ExpectedAnswers("input.txt", 2077, 2741)
		);
	}
	
	GameEngine gameEngine;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		Map<String, Valve> valves = Utils.readLines(input).stream()
				.map(Valve::parse)
				.collect(Collectors.toMap(Valve::name, v -> v));
		gameEngine = new GameEngine(valves, PathService.computeAllPaths(valves));
	}
	
	@Override
	public Object part1() {
		return gameEngine.maxPressure(1, 30);
	}
	
	@Override
	public Object part2() {
		return gameEngine.maxPressure(2, 26);
	}
	
	record Valve(String name, int rate, List<String> availableValves) {
		
		List<Valve> availableValves(Map<String, Valve> allValves) {
			return availableValves.stream().map(allValves::get).toList();
		}
		
		static final Pattern pattern = Pattern.compile("Valve (.+) has flow rate=(.+); tunnels? leads? to valves? (.+)");
		
		static Valve parse(String line) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.find())
				throw new IllegalArgumentException("Invalid line: " + line);
			
			String name = matcher.group(1);
			int rate = Integer.parseInt(matcher.group(2));
			List<String> availableValves = List.of(matcher.group(3).split(", "));
			return new Valve(name, rate, availableValves);
		}
	}
	
	record PathService(Map<String, Map<String, Integer>> paths) {
		
		int path(String from, String to) {
			return paths.get(from).get(to);
		}
		
		static PathService computeAllPaths(Map<String, Valve> valves) {
			Map<String, Map<String, Integer>> paths = new HashMap<>();
			for (Valve valve : valves.values()) {
				Map<String, Integer> currentPaths = new HashMap<>();
				putPathsRecursive(currentPaths, valves, valve, valve.availableValves(valves), 1);
				paths.put(valve.name(), currentPaths);
			}
			return new PathService(paths);
		}
		
		static void putPathsRecursive(Map<String, Integer> paths, Map<String, Valve> valves, Valve start,
		                                      Collection<Valve> inspectingValves, int time) {
			Set<Valve> next = new HashSet<>();
			for (Valve valve : inspectingValves) {
				if (valve.equals(start))
					continue;
				String key = valve.name;
				if (paths.putIfAbsent(key, time) != null)
					continue;
				next.addAll(valve.availableValves(valves));
			}
			if (!next.isEmpty())
				putPathsRecursive(paths, valves, start, next, time + 1);
		}
	}
	
	@AllArgsConstructor
	static class Worker {
		String position;
		String target;
		int remainingDistance;
		
		Worker copy() {
			return new Worker(position, target, remainingDistance);
		}
	}
	
	@AllArgsConstructor
	static class GameState {
		final Set<String> valvesLeft;
		final List<Worker> workers;
		int releasedPressure;
		
		void openValve(Valve valve, int timeLeft) {
			releasedPressure += valve.rate() * timeLeft;
		}
	}
	
	
	record GameEngine(Map<String, Valve> allValves, PathService pathService) {
		
		int maxPressure(int workersCount, int time) {
			List<Worker> workers = IntStream.range(0, workersCount)
					.mapToObj(id -> new Worker("AA", null, 0))
					.toList();
			
			Set<String> valvesLeft = allValves.values().stream()
					.filter(v -> v.rate > 0)
					.map(Valve::name)
					.collect(Collectors.toSet());
			List<GameState> states = List.of(new GameState(valvesLeft, workers, 0));
			
			while (time > 0) {
				time--;
				int _time = time;
				states = states.stream()
						.flatMap(state -> step(state, _time))
						.sorted(Comparator.comparing((GameState g) -> g.releasedPressure).reversed())
						.limit(50_000)
						.toList();
			}
			return states.stream().mapToInt(g -> g.releasedPressure).max().orElse(0);
		}
		
		Stream<GameState> step(GameState state, int timeLeft) {
			if (state.workers.isEmpty())
				return Stream.of(state);
			
			Optional<Worker> workerWithNoTarget = state.workers.stream()
					.filter(w -> w.target == null).findAny();
			if (workerWithNoTarget.isPresent())
				return nextStates(workerWithNoTarget.get(), state).stream().flatMap(s -> step(s, timeLeft));
			
			for (Worker worker : state.workers) {
				if (worker.remainingDistance > 0) {
					worker.remainingDistance = worker.remainingDistance - 1;
					continue;
				}
				state.openValve(allValves.get(worker.target), timeLeft);
				worker.position = worker.target;
				worker.target = null;
			}
			return Stream.of(state);
		}
		
		List<GameState> nextStates(Worker worker, GameState state) {
			Set<String> possibleTargets = state.valvesLeft;
			if (possibleTargets.isEmpty()) {
				List<Worker> workers;
				if (state.workers.size() == 1) {
					workers = List.of();
				} else {
					workers = state.workers.stream().filter(w -> w != worker).map(Worker::copy).toList();
				}
				return List.of(new GameState(new HashSet<>(state.valvesLeft), workers, state.releasedPressure));
			}
			List<GameState> nextStates = new ArrayList<>();
			for (String target : possibleTargets) {
				int remainingDistance = pathService.path(worker.position, target);
				Worker nextWorker = new Worker(worker.position, target, remainingDistance);
				List<Worker> workers;
				if (state.workers.size() == 1) {
					workers = List.of(nextWorker);
				} else {
					workers = state.workers.stream().map(w -> w == worker ? nextWorker : w.copy()).toList();
				}
				Set<String> nextValvesLeft = new HashSet<>(state.valvesLeft);
				nextValvesLeft.remove(target);
				nextStates.add(new GameState(nextValvesLeft, workers, state.releasedPressure));
			}
			return nextStates;
		}
	}
}