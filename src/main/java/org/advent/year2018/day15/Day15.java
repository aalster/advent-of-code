package org.advent.year2018.day15;

import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day15 extends AdventDay {
	static final boolean print = false;
	
	public static void main(String[] args) {
		new DayRunner(new Day15()).runAll();
//		new DayRunner(new Day15()).run("example.txt", 1);
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 27730, 4988),
				new ExpectedAnswers("example2.txt", 36334, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 39514, 31284),
				new ExpectedAnswers("example4.txt", 27755, 3478),
				new ExpectedAnswers("example5.txt", 28944, 6474),
				new ExpectedAnswers("example6.txt", 18740, 1140),
				new ExpectedAnswers("input.txt", 250594, 52133)
		);
	}
	
	Set<Point> emptyPoints;
	SortedMap<Point, Unit> units;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<String> lines = Utils.readLines(input);
		int y = 0;
		emptyPoints = new HashSet<>();
		units = new TreeMap<>(Point.COMPARATOR);
		for (String line : lines) {
			char[] chars = line.toCharArray();
			for (int x = 0; x < chars.length; x++) {
				char c = chars[x];
				if (c == '#')
					continue;
				Point position = new Point(x, y);
				if (c == 'E')
					units.put(position, new Unit(Unit.TYPE_ELF));
				if (c == 'G')
					units.put(position, new Unit(Unit.TYPE_GOBLIN));
				emptyPoints.add(position);
			}
			y++;
		}
	}
	
	@Override
	public Object part1() {
		return solve(emptyPoints, units, 3, false);
	}
	
	@Override
	public Object part2() {
		for (int elfDamage = 4; elfDamage <= 200; elfDamage++) {
			int outcome = solve(emptyPoints, units, elfDamage, true);
			if (outcome > 0)
				return outcome;
		}
		return 0;
	}
	
	int solve(Set<Point> emptyPoints, SortedMap<Point, Unit> units, int elfDamage, boolean noElfDeaths) {
		TreeMap<Point, Unit> copy = new TreeMap<>(Point.COMPARATOR);
		for (Map.Entry<Point, Unit> entry : units.entrySet())
			copy.put(entry.getKey(), entry.getValue().copy());
		units = copy;
		
		print(units, emptyPoints);
		int steps = 0;
		main: while (true) {
			for (Point position : new ArrayList<>(units.keySet())) {
				Unit unit = units.get(position);
				if (unit == null)
					continue;
				
				if (units.values().stream().noneMatch(u -> u.type != unit.type))
					break main;
				
				Pair<Point, Unit> attack = unit.attackTarget(position, units);
				if (attack != null) {
					Unit target = attack.right();
					if (print)
						System.out.println(unit.type + " " + position + " attacks " + attack.left());
					target.hp -= unit.type == Unit.TYPE_ELF ? elfDamage : 3;
					if (target.hp <= 0) {
						if (noElfDeaths && target.type == Unit.TYPE_ELF)
							return 0;
						units.remove(attack.left());
					}
				} else {
					Point nextPosition = unit.move(position, emptyPoints, units);
					if (nextPosition != null) {
						units.remove(position);
						units.put(nextPosition, unit);
						if (print)
							System.out.println(unit.type + " " + position + " moves to " + nextPosition);
						
						attack = unit.attackTarget(nextPosition, units);
						if (attack != null) {
							Unit target = attack.right();
							if (print)
								System.out.println(unit.type + " " + nextPosition + " attacks " + attack.left());
							target.hp -= unit.type == Unit.TYPE_ELF ? elfDamage : 3;
							if (target.hp <= 0) {
								if (noElfDeaths && target.type == Unit.TYPE_ELF)
									return 0;
								units.remove(attack.left());
							}
						}
					}
				}
			}
			steps++;
			if (print)
				System.out.println("\nSTEP: " + steps);
			print(units, emptyPoints);
		}
		print(units, emptyPoints);
		if (print)
			System.out.println("TOTAL STEPS: " + (steps + 1));
		return steps * units.values().stream().mapToInt(u -> u.hp).sum();
	}
	
	static void print(Map<Point, Unit> units, Set<Point> emptyPoints) {
		print(units, emptyPoints, false);
	}
	
	static void print(Map<Point, Unit> units, Set<Point> emptyPoints, boolean force) {
		if (!force && !print)
			return;
		System.out.println();
		List<Point> bounds = List.of(new Point(0, 0), Point.maxBound(emptyPoints).shift(1, 1));
		Point.printField(bounds, p -> Optional.ofNullable(units.get(p))
				.map(u -> u.type == Unit.TYPE_ELF ? 'E' : 'G')
				.orElseGet(() -> emptyPoints.contains(p) ? '.' : '#'));
		System.out.println(units.values().stream()
				.map(u -> (u.type == Unit.TYPE_ELF ? "E(" : "G(") + u.hp + ")")
				.collect(Collectors.joining(", ")));
		System.out.println();
	}
	
	static class Unit {
		static final Direction[] sortedDirections = Direction.stream()
				.sorted(Comparator.comparing(Direction::getP, Point.COMPARATOR)).toArray(Direction[]::new);
		static final int TYPE_ELF = 0;
		static final int TYPE_GOBLIN = 1;
		
		int type;
		int hp = 200;
		
		Unit(int type) {
			this.type = type;
		}
		
		Unit copy() {
			return new Unit(type);
		}
		
		Pair<Point, Unit> attackTarget(Point position, Map<Point, Unit> units) {
			Point targetPosition = null;
			Unit target = null;
			for (Direction direction : sortedDirections) {
				Point next = direction.shift(position);
				Unit enemy = units.get(next);
				if (enemy != null && enemy.type != type && (target == null || enemy.hp < target.hp)) {
					targetPosition = next;
					target = enemy;
				}
			}
			return target != null ? Pair.of(targetPosition, target) : null;
		}
		
		Point move(Point position, Set<Point> emptyPoints, Map<Point, Unit> units) {
			Set<Point> targetPositions = units.entrySet().stream()
					.filter(e -> e.getValue().type != type)
					.map(Map.Entry::getKey)
					.flatMap(p -> Direction.stream().map(p::shift))
					.filter(emptyPoints::contains)
					.filter(p -> !units.containsKey(p))
					.collect(Collectors.toSet());
			if (targetPositions.isEmpty())
				return null;
			
			Set<Point> visited = new HashSet<>();
			Set<Path> paths = Set.of(new Path(position, List.of()));
			
			Path targetPath = null;
			while (targetPath == null && !paths.isEmpty()) {
				for (Path path : paths)
					visited.add(path.current);
				
				Set<Path> nextPaths = paths.stream()
						.flatMap(p -> p.next(emptyPoints, visited, units.keySet()))
						.collect(Collectors.toSet());
				
				for (Path nextPath : nextPaths) {
					if (targetPositions.contains(nextPath.current)) {
						if (targetPath == null
								|| Point.COMPARATOR.compare(nextPath.current, targetPath.current) < 0
								|| Point.COMPARATOR.compare(nextPath.secondStep(), targetPath.secondStep()) < 0) {
							targetPath = nextPath;
						}
					}
				}
				paths = nextPaths;
			}
			return targetPath == null ? null : targetPath.secondStep();
		}
	}
	
	record Path(Point current, List<Point> firstTwoSteps) {
		
		Point secondStep() {
			return firstTwoSteps.size() == 2 ? firstTwoSteps.getLast() : firstTwoSteps.size() == 1 ? current : null;
		}
		
		Stream<Path> next(Set<Point> emptyPoints, Set<Point> visited, Set<Point> occupied) {
			List<Point> nextSteps;
			if (firstTwoSteps.size() < 2) {
				nextSteps = new ArrayList<>(firstTwoSteps);
				nextSteps.add(current);
			} else {
				nextSteps = firstTwoSteps;
			}
			return Direction.stream().map(current::shift)
					.filter(emptyPoints::contains)
					.filter(n -> !visited.contains(n))
					.filter(n -> !occupied.contains(n))
					.map(n -> new Path(n, nextSteps));
		}
	}
	
	record Path2(Point firstStep, Point current) {
		
		Path2(Point firstStep) {
			this(firstStep, firstStep);
		}
		
		Stream<Path2> next(Set<Point> emptyPoints, Set<Point> visited, Set<Point> occupied) {
			return Direction.stream().map(current::shift)
					.filter(emptyPoints::contains)
					.filter(next -> !visited.contains(next))
					.filter(next -> !occupied.contains(next))
					.map(next -> new Path2(firstStep, next));
		}
	}
}