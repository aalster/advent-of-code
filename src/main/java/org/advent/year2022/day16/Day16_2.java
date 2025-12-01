package org.advent.year2022.day16;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day16_2 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day16_2()).runAll();
//		new DayRunner(new Day16_2()).run("input.txt", 1);
//		new DayRunner(new Day16_2()).run("example.txt", 2);
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
//				new ExpectedAnswers("example.txt", 1651, 1707),
//				new ExpectedAnswers("input.txt", 2077, 2741)
		);
	}
	
	Map<String, Valve> valves;
	PathService pathService;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		valves = Utils.readLines(input).stream()
				.map(Valve::parse)
				.collect(Collectors.toMap(Valve::name, v -> v));
		pathService = PathService.computeAllPaths(valves);
	}
	
	@Override
	public Object part1() {
		return solve(1, 30);
	}
	
	@Override
	public Object part2() {
		return solve(2, 26);
	}
	
	int solve(int workersCount, int time) {
		Set<Worker> workers = IntStream.range(0, workersCount)
				.mapToObj(i -> new Worker("AA", 0))
				.collect(Collectors.toSet());
		Set<String> valvesLeft = valves.values().stream().filter(v -> v.rate > 0).map(Valve::name).collect(Collectors.toSet());
		
		Queue<State> queue = new PriorityQueue<>((left, right) -> {
			int timeCompare = Integer.compare(left.timeLeft, right.timeLeft);
//			if (timeCompare != 0)
				return timeCompare;
//			return -Integer.compare(left.releasedPressure, right.releasedPressure);
		});
		
		Set<State> history = new HashSet<>();
		queue.add(new State(workers, valvesLeft, 0, 0, time));
		
		int maxPressure = 0;
		
		while (!queue.isEmpty()) {
			State state = queue.poll();
			for (State next : state.next(pathService, valves)) {
				if (next.timeLeft == 0) {
					maxPressure = Math.max(maxPressure, next.releasedPressure);
				} else {
					queue.add(next);
				}
			}
		}
		return maxPressure;
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
		
		int path(Valve from, Valve to) {
			return paths.get(from.name).get(to.name) + 1;
		}
		
		int path(String from, String to) {
			return paths.get(from).get(to) + 1;
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
	
	record Worker(String targetValve, int stepsLeft) {
		static final Worker EMPTY = new Worker(null, 0);
		
		Worker move(int steps) {
			if (stepsLeft <= steps)
				return EMPTY;
			return new Worker(targetValve, stepsLeft - steps);
		}
	}
	
	record State(Set<Worker> workers, Set<String> valvesLeft, int releasedPressure, int pressureRate, int timeLeft) {
		
		List<State> next(PathService pathService, Map<String, Valve> valves) {
			if (workers.isEmpty())
				throw new RuntimeException("EMPTY asdasasdas");
			int exception;
			
			int steps = workers.stream().mapToInt(Worker::stepsLeft).min().orElse(0);
			if (steps >= timeLeft) {
				int nextPressure = releasedPressure + pressureRate * timeLeft;
				return List.of(new State(Set.of(), Set.of(), nextPressure, pressureRate, 0));
			}
			int nextTimeLeft = timeLeft - steps;
			int nextPressure = releasedPressure + pressureRate * steps;
			int nextPressureRate = pressureRate;
			
			List<Set<Worker>> nextWorkers = List.of(new HashSet<>());
			for (Worker worker : workers) {
				if (steps < worker.stepsLeft) {
					nextWorkers.forEach(s -> s.add(worker.move(steps)));
				} else {
					nextPressureRate += valves.get(worker.targetValve).rate;
					List<Set<Worker>> finalNextWorkers = nextWorkers;
					nextWorkers = valvesLeft.stream()
							.map(target -> new Worker(target, pathService.path(worker.targetValve, target)))
							.filter(w -> w.stepsLeft <= timeLeft)
							.flatMap(w -> finalNextWorkers.stream()
									.filter(nw -> nw.stream().noneMatch(t -> t.targetValve.equals(w.targetValve)))
									.map(nw -> Utils.combineToSet(nw, w)))
							.toList();
				}
			}
			
			if (nextWorkers.isEmpty())
				return java.util.List.of(new State(Set.of(), Set.of(), nextPressure + nextPressureRate * nextTimeLeft, nextPressureRate, 0));
			
			int finalNextPressureRate = nextPressureRate;
			return nextWorkers.stream()
					.map(nw -> {
						Set<String> nextValvesLeft = new HashSet<>(valvesLeft);
						nw.forEach(w -> nextValvesLeft.remove(w.targetValve));
						return new State(nw, nextValvesLeft, nextPressure, finalNextPressureRate, nextTimeLeft);
					})
					.toList();
		}
	}
	
	@Data
	@AllArgsConstructor
	static class GameState {
		private final Set<Valve> openValves;
		private final Map<Integer, Worker> workers;
		private int releasedPressure;
		
		void incPressure() {
			releasedPressure += openValves.stream().mapToInt(Valve::rate).sum();
		}
		
		void openValve(Valve valve) {
			openValves.add(valve);
		}
	}
}