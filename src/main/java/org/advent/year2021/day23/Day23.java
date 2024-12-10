package org.advent.year2021.day23;

import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SequencedSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Day23 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day23.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		Pair<Field, State> part1 = parse(lines);
		
		List<String> lines2 = new ArrayList<>(lines);
		lines2.add(3, "  #D#C#B#A#");
		lines2.add(4, "  #D#B#A#C#");
		Pair<Field, State> part2 = parse(lines2);
		
		System.out.println("Answer 1: " + solve(part1.left(), part1.right()));
		System.out.println("Answer 2: " + solve(part2.left(), part2.right()));
	}
	
	private static long solve(Field field, State initialState) {
		SequencedSet<State> states = new LinkedHashSet<>(List.of(initialState));
		int minEnergy = Integer.MAX_VALUE;
		while (!states.isEmpty()) {
			State current = states.removeFirst();
			if (minEnergy < current.energy)
				continue;
			
			List<State> nextStates = current.nextStates(field);
			if (nextStates.isEmpty()) {
				if (current.allAtHome() && current.energy < minEnergy)
					minEnergy = current.energy;
				continue;
			}
			states.addAll(nextStates);
		}
		return minEnergy;
	}
	
	static Pair<Field, State> parse(List<String> lines) {
		Map<Character, List<Point>> points = Point.readField(lines);
		
		Map<Point, Character> amphipods = new HashMap<>();
		for (Map.Entry<Character, List<Point>> entry : points.entrySet()) {
			Character type = entry.getKey();
			if ('A' <= type && type <= 'Z')
				for (Point point : entry.getValue())
					amphipods.put(point, type);
		}
		Set<Point> allEmpty = new HashSet<>(points.get('.'));
		allEmpty.addAll(amphipods.keySet());
		Field field = Field.fromEmptyPoints(allEmpty);
		State state = State.init(amphipods, field);
		return Pair.of(field, state);
	}
	
	record State(Map<Point, Character> amphipods, Set<Point> settled, int energy) {
		
		List<State> nextStates(Field field) {
			List<Map.Entry<Point, Character>> candidates = amphipods.entrySet().stream()
					.filter(e -> !settled.contains(e.getKey()))
					.filter(e -> field.canMove(e.getKey(), e.getValue(), amphipods))
					.toList();
			
			for (Map.Entry<Point, Character> candidate : candidates) {
				Pair<Integer, Point> pathToHome = field.pathToHome(candidate.getKey(), candidate.getValue(), amphipods);
				if (pathToHome != null)
					return List.of(nextState(field, pathToHome, candidate.getKey(), candidate.getValue(), true));
			}
			
			return candidates.stream()
					.filter(e -> e.getKey().y() != field.hallwayY)
					.flatMap(e -> field.allHallwayPaths(e.getKey(), amphipods).stream()
							.map(pair -> nextState(field, pair, e.getKey(), e.getValue(), false)))
					.toList();
		}
		
		State nextState(Field field, Pair<Integer, Point> pathToHome, Point position, Character type, boolean isSettled) {
			Map<Point, Character> nextAmphipods = new HashMap<>(amphipods);
			nextAmphipods.remove(position);
			nextAmphipods.put(pathToHome.right(), type);
			int nextEnergy = energy + pathToHome.left() * field.energyCosts.get(type);
			Set<Point> nextSettled;
			if (isSettled) {
				nextSettled = new HashSet<>(settled);
				nextSettled.add(pathToHome.right());
			} else {
				nextSettled = settled;
			}
			return new State(nextAmphipods, nextSettled, nextEnergy);
		}
		
		boolean allAtHome() {
			return settled.size() == amphipods.size();
		}
		
		static State init(Map<Point, Character> amphipods, Field field) {
			Set<Point> settled = new HashSet<>();
			for (Map.Entry<Character, Point> entry : field.homes.entrySet()) {
				Point home = entry.getValue();
				while (amphipods.get(home) == entry.getKey()) {
					settled.add(home);
					home = home.shift(Direction.UP);
				}
			}
			return new State(amphipods, settled, 0);
		}
	}
	
	record Field(
			Set<Point> restricted,
			int hallwayMinX,
			int hallwayMaxX,
			int hallwayY,
			Map<Character, Point> homes,
			Map<Character, Integer> energyCosts) {
		
		List<Pair<Integer, Point>> allHallwayPaths(Point position, Map<Point, Character> amphipods) {
			List<Pair<Integer, Point>> paths = new ArrayList<>();
			
			int stepsUp = position.y() - hallwayY;
			position = new Point(position.x(), hallwayY);
			
			{
				int steps = 0;
				Point leftPath = position;
				while (hallwayMinX < leftPath.x()) {
					leftPath = leftPath.shift(Direction.LEFT);
					steps++;
					if (amphipods.containsKey(leftPath))
						break;
					if (!restricted.contains(leftPath))
						paths.add(Pair.of(steps + stepsUp, leftPath));
				}
			}
			{
				int steps = 0;
				Point rightPath = position;
				while (rightPath.x() < hallwayMaxX) {
					rightPath = rightPath.shift(Direction.RIGHT);
					steps++;
					if (amphipods.containsKey(rightPath))
						break;
					if (!restricted.contains(rightPath))
						paths.add(Pair.of(steps + stepsUp, rightPath));
				}
			}
			return paths;
		}
		
		Pair<Integer, Point> pathToHome(Point position, Character type, Map<Point, Character> amphipods) {
			Point home = homes.get(type);
			
			while (true) {
				Character homeAmphipod = amphipods.get(home);
				if (homeAmphipod == null)
					break;
				if (homeAmphipod != type)
					return null;
				home = home.shift(Direction.UP);
			}
			
			int steps = 0;
			while (position.y() > hallwayY) {
				position = position.shift(Direction.UP);
				steps++;
			}
			while (position.x() < home.x()) {
				position = position.shift(Direction.RIGHT);
				if (amphipods.containsKey(position))
					return null;
				steps++;
			}
			while (position.x() > home.x()) {
				position = position.shift(Direction.LEFT);
				if (amphipods.containsKey(position))
					return null;
				steps++;
			}
			while (position.y() < home.y()) {
				position = position.shift(Direction.DOWN);
				steps++;
			}
			return Pair.of(steps, position);
		}
		
		private boolean canMove(Point position, Character type, Map<Point, Character> amphipods) {
			while (position.y() > hallwayY + 1) {
				position = position.shift(Direction.UP);
				if (amphipods.containsKey(position))
					return false;
			}
			return true;
		}
		
		static Field fromEmptyPoints(Set<Point> allEmpty) {
			int hallwayY = allEmpty.stream().mapToInt(Point::y).min().orElseThrow();
			int homeY = allEmpty.stream().mapToInt(Point::y).max().orElseThrow();
			
			Map<Character, Point> homes = new HashMap<>();
			Map<Character, Integer> energyCosts = new HashMap<>();
			Character homeType = 'A';
			int energyCost = 1;
			for (Point home : allEmpty.stream().filter(p -> p.y() == homeY).sorted(Comparator.comparing(Point::x)).toList()) {
				homes.put(homeType, home);
				energyCosts.put(homeType, energyCost);
				homeType++;
				energyCost *= 10;
			}
			Set<Point> restricted = homes.values().stream().map(p -> new Point(p.x(), hallwayY)).collect(Collectors.toSet());
			return new Field(restricted, Point.minX(allEmpty), Point.maxX(allEmpty), hallwayY, homes, energyCosts);
		}
	}
}