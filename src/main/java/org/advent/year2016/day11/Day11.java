package org.advent.year2016.day11;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day11()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 11, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 31, 55)
		);
	}
	
	State initialState;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		initialState = State.parse(Utils.readLines(input));
	}
	
	@Override
	public Object part1() {
		return solve(initialState);
	}
	
	@Override
	public Object part2() {
		Containment[] floors = ArrayUtils.clone(initialState.floors);
		floors[0] = floors[0].putItems(new Containment(Set.of("elerium", "dilithium"), Set.of("elerium", "dilithium")));
		State expandedState = new State(floors, 0);
		return solve(expandedState);
	}
	
	private int solve(State initialState) {
		Set<State> prevStates = new HashSet<>();
		Collection<State> states = Set.of(initialState);
		int step = 0;
		while (!states.isEmpty()) {
			prevStates.addAll(states);
			states = states.stream().flatMap(State::nextStates).filter(s -> !prevStates.contains(s)).collect(Collectors.toSet());
			step++;
			if (states.stream().anyMatch(State::completed))
				return step;
		}
		return 0;
	}
	
	record State(Containment[] floors, int elevator) {
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof State(Containment[] otherFloors, int otherElevator)))
				return false;
			return Arrays.equals(floors, otherFloors) && elevator == otherElevator;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(Arrays.hashCode(floors), elevator);
		}
		
		boolean completed() {
			for (int i = 0; i < floors.length - 1; i++)
				if (!floors[i].isEmpty())
					return false;
			return true;
		}
		
		Stream<State> nextStates() {
			return floors[elevator].takeItemsPossibilities().stream()
					.flatMap(takenItems -> Stream.of(elevator - 1, elevator + 1)
							.filter(nextElevator -> 0 <= nextElevator && nextElevator < floors.length)
							.flatMap(nextElevator -> nextState(nextElevator, takenItems)));
		}
		
		Stream<State> nextState(int nextElevator, Containment takenItems) {
			// Не идти вниз с двумя предметами
			if (nextElevator < elevator && takenItems.size() == 2)
				return Stream.empty();
			// Не идти вверх с одним предметом. Не работает для примера
//			if (nextElevator > elevator && takenItems.size() == 1)
//				return Stream.empty();
			
			Containment[] nextFloors = ArrayUtils.clone(floors);
			nextFloors[nextElevator] = floors[nextElevator].putItems(takenItems);
			if (nextFloors[nextElevator].willFry())
				return Stream.empty();
			nextFloors[elevator] = floors[elevator].takeItems(takenItems);
			return Stream.of(new State(nextFloors, nextElevator));
		}
		
		static State parse(List<String> lines) {
			return new State(lines.stream().map(Containment::parse).toArray(Containment[]::new), 0);
		}
	}
	
	record Containment(Set<String> generators, Set<String> microchips) {
		
		Containment takeItems(Containment other) {
			return new Containment(removed(generators, other.generators), removed(microchips, other.microchips));
		}
		
		Containment putItems(Containment other) {
			return new Containment(added(generators, other.generators), added(microchips, other.microchips));
		}
		
		Collection<Containment> takeItemsPossibilities() {
			Map<Boolean, List<String>> groups = microchips.stream().collect(Collectors.groupingBy(generators::contains));
			List<String> symmetric = groups.getOrDefault(true, List.of());
			List<String> loneMicrochips = groups.getOrDefault(false, List.of());
			List<String> loneGenerators = generators.stream().filter(g -> !microchips.contains(g)).toList();
			
			Set<Containment> taken = new HashSet<>();
			if (!symmetric.isEmpty()) {
				// Между симметричными вариантами нет разницы, выбираем всегда только один такой вариант
				String first = symmetric.stream().sorted().limit(1).findFirst().orElseThrow();
				taken.add(new Containment(Set.of(), Set.of(first)));
				taken.add(new Containment(Set.of(first), Set.of(first)));
				if (symmetric.size() == 1) {
					if (loneGenerators.isEmpty())
						taken.add(new Containment(Set.of(first), Set.of()));
					else if (loneGenerators.size() == 1)
						taken.add(new Containment(Set.of(first, loneGenerators.getFirst()), Set.of()));
				}
			}
			if (!loneGenerators.isEmpty()) {
				for (String first : loneGenerators) {
					taken.add(new Containment(Set.of(first), Set.of()));
					for (String second : loneGenerators)
						if (!first.equals(second))
							taken.add(new Containment(Set.of(first, second), Set.of()));
				}
			} else {
				for (String first : loneMicrochips) {
					taken.add(new Containment(Set.of(), Set.of(first)));
					for (String second : loneMicrochips)
						if (!first.equals(second))
							taken.add(new Containment(Set.of(), Set.of(first, second)));
				}
			}
			return taken;
		}
		
		boolean willFry() {
			return !generators.isEmpty() && !generators.containsAll(microchips);
		}
		
		int size() {
			return generators.size() + microchips.size();
		}
		
		boolean isEmpty() {
			return generators.isEmpty() && microchips.isEmpty();
		}
		
		static Containment parse(String line) {
			if (line.contains("nothing"))
				return new Containment(Set.of(), Set.of());
			Set<String> generators = new HashSet<>();
			Set<String> microchips = new HashSet<>();
			line = Utils.removeEach(line.split("contains a ")[1], "-compatible", ".");
			Arrays.stream(Utils.replaceEach(line, new String[]{", a ", ", and a ", " and a "}, ",").split(","))
					.map(s -> s.split(" "))
					.forEach(s -> ("generator".equals(s[1]) ? generators : microchips).add(s[0]));
			return new Containment(generators, microchips);
		}
	}
	
	static <T> Set<T> added(Set<T> a, Set<T> b) {
		if (b.isEmpty())
			return a;
		a = new HashSet<>(a);
		a.addAll(b);
		return a;
	}
	
	static <T> Set<T> removed(Set<T> a, Set<T> b) {
		if (b.isEmpty())
			return a;
		a = new HashSet<>(a);
		a.removeAll(b);
		return a;
	}
}