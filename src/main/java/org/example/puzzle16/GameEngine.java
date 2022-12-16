package org.example.puzzle16;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class GameEngine {
	private final Map<String, Valve> allValves;
	private final PathService pathService;
	private final Valve start;
	private List<GameState> states;
	
	public int maxPressure(int workersCount, int time) {
		Map<Integer, Worker> workers = IntStream.range(0, workersCount)
				.mapToObj(id -> new Worker(id, start, null, 0))
				.collect(Collectors.toMap(Worker::getId, w -> w));
		states = List.of(new GameState(new HashSet<>(), workers, 0));
		
		while (time > 0) {
			System.out.println(time + ": " + states.size());
			int timeRemaining = time;
			states = states.stream()
					.flatMap(state -> step(state, timeRemaining))
					.sorted(Comparator.comparing(GameState::getReleasedPressure).reversed())
					.limit(50_000)
					.toList();
			time--;
		}
		System.out.println("states: " + states.size());
		GameState result = states.stream().max(Comparator.comparing(GameState::getReleasedPressure)).orElse(null);
		if (result == null)
			return 0;
		System.out.println(result);
		return result.getReleasedPressure();
	}
	
	private Stream<GameState> step(GameState state, int remainingTime) {
		if (state.getWorkers().isEmpty()) {
			state.incPressure(remainingTime);
			return Stream.of(state);
		}
		
		Worker workerWithNoTarget = state.getWorkers().values().stream().filter(w -> w.getTarget() == null).findAny().orElse(null);
		if (workerWithNoTarget != null)
			return nextStates(workerWithNoTarget, state, remainingTime).stream().flatMap(s -> step(s, remainingTime));
		
		state.incPressure(remainingTime);
		for (Worker worker : state.getWorkers().values()) {
			if (worker.getRemainingDistance() > 0) {
				worker.setRemainingDistance(worker.getRemainingDistance() - 1);
				continue;
			}
			state.openValve(worker.getTarget(), worker.getId());
			worker.setPosition(worker.getTarget());
			worker.setTarget(null);
		}
		return Stream.of(state);
	}
	
	private List<GameState> nextStates(Worker worker, GameState state, int remainingTime) {
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
				workers = new HashMap<>(state.getWorkers().values().stream().map(Worker::cloneWorker)
						.collect(Collectors.toMap(Worker::getId, w -> w)));
				workers.put(nextWorker.getId(), nextWorker);
			}
			GameState nextState = new GameState(new HashSet<>(state.getOpenValves()), workers, state.getReleasedPressure());
			nextStates.add(nextState);
		}
		return nextStates;
	}
	
	public List<Valve> nextTargets(GameState state, Valve from, int remainingTime) {
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