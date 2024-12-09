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
		Map<Character, List<Point>> points = Point.readField(Utils.readLines(input));
		
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
		State state = new State(amphipods, 0);
		
		long start = System.currentTimeMillis();
		System.out.println("Answer 1: " + part1(field, state));
		System.out.println("Answer 2: " + part2());
		System.out.println(System.currentTimeMillis() - start + " ms");
	}
	
	private static long part1(Field field, State initialState) {
		SequencedSet<State> states = new LinkedHashSet<>(List.of(initialState));
		int minEnergy = Integer.MAX_VALUE;
		while (!states.isEmpty()) {
			State current = states.removeFirst();
			if (minEnergy < current.energy)
				continue;
			
			List<State> nextStates = current.nextStates(field);
			if (nextStates.isEmpty()) {
				if (current.allAtHome(field) && current.energy < minEnergy)
					minEnergy = current.energy;
				continue;
			}
			states.addAll(nextStates);
//			System.out.println(states.size() + " - " + minEnergy);
		}
		return minEnergy;
	}
	
	private static long part2() {
		return 0;
	}
	
	record State(Map<Point, Character> amphipods, int energy) {
		
		List<State> nextStates(Field field) {
			List<Map.Entry<Point, Character>> candidates = amphipods.entrySet().stream()
					.filter(e -> field.canMove(e.getKey(), e.getValue(), amphipods))
					.toList();
			
			for (Map.Entry<Point, Character> candidate : candidates) {
				Pair<Integer, Point> pathToHome = field.pathToHome(candidate.getKey(), candidate.getValue(), amphipods);
				if (pathToHome != null)
					return List.of(nextState(field, pathToHome, candidate.getKey(), candidate.getValue()));
			}
			
			return candidates.stream()
					.filter(e -> e.getKey().y() != field.hallwayY)
					.flatMap(e -> field.allHallwayPaths(e.getKey(), amphipods).stream()
							.map(pair -> nextState(field, pair, e.getKey(), e.getValue())))
					.toList();
		}
		
		State nextState(Field field, Pair<Integer, Point> pathToHome, Point position, Character type) {
			Map<Point, Character> nextAmphipods = new HashMap<>(amphipods);
			nextAmphipods.remove(position);
			nextAmphipods.put(pathToHome.right(), type);
			int nextEnergy = energy + pathToHome.left() * field.energyCosts.get(type);
			return new State(nextAmphipods, nextEnergy);
		}
		
		boolean allAtHome(Field field) {
			for (Map.Entry<Point, Character> entry : amphipods.entrySet()) {
				Point position = entry.getKey();
				if (position.y() == field.hallwayY || position.x() != field.homes.get(entry.getValue()).x())
					return false;
			}
			return true;
		}
		
		void print(Field field) {
			System.out.println("\nState energy: " + energy);
			Set<Point> points = new HashSet<>(field.homes.values());
			points.addAll(List.of(new Point(field.hallwayMinX, field.hallwayY), new Point(field.hallwayMaxX, field.hallwayY)));
			Point.printField(points, p -> {
				Character amp = amphipods.get(p);
				if (amp != null)
					return amp;
				return p.y() == field.hallwayY || field.restricted.contains(new Point(p.x(), field.hallwayY)) ? '.' : ' ';
			});
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
			
			int stepsUp = 0;
			while (position.y() > hallwayY) {
				position = position.shift(Direction.UP);
				stepsUp++;
			}
			
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
		
		boolean settled(Point position, Character type, Map<Point, Character> amphipods) {
			Point home = homes.get(type);
			if (home.x() != position.x())
				return false;
			while (position.y() < home.y()) {
				position = position.shift(Direction.DOWN);
				if (amphipods.get(position) != type)
					return false;
			}
			return true;
		}
		
		private boolean canMove(Point position, Character type, Map<Point, Character> amphipods) {
			if (settled(position, type, amphipods))
				return false;
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