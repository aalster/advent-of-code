package org.advent.year2018.day15;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	public static void main(String[] args) {
		new DayRunner(new Day15()).runAll();
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
					units.put(position, new Unit(position, Unit.TYPE_ELF));
				if (c == 'G')
					units.put(position, new Unit(position, Unit.TYPE_GOBLIN));
				emptyPoints.add(position);
			}
			y++;
		}
	}
	
	@Override
	public Object part1() {
		return new Battleground(units, emptyPoints, 3, false).play();
	}
	
	@Override
	public Object part2() {
		int minDamage = 4;
		int maxDamage = 200;
		int bestOutcome = 0;
		while (minDamage < maxDamage) {
			int middleDamage = (minDamage + maxDamage) / 2;
			int outcome = new Battleground(units, emptyPoints, middleDamage, true).play();
			if (outcome <= 0) {
				minDamage = middleDamage + 1;
			} else {
				maxDamage = middleDamage;
				bestOutcome = outcome;
			}
		}
		return bestOutcome;
	}
	
	record Battleground(SortedMap<Point, Unit> units, Set<Point> emptyPoints, int elfDamage, boolean noElfDeaths) {
		
		static final Direction[] sortedDirections = Direction.stream()
				.sorted(Comparator.comparing(Direction::getP, Point.COMPARATOR)).toArray(Direction[]::new);
		
		Battleground(SortedMap<Point, Unit> units, Set<Point> emptyPoints, int elfDamage, boolean noElfDeaths) {
			TreeMap<Point, Unit> copy = new TreeMap<>(Point.COMPARATOR);
			for (Map.Entry<Point, Unit> entry : units.entrySet())
				copy.put(entry.getKey(), entry.getValue().copy());
			this.units = copy;
			this.emptyPoints = emptyPoints;
			this.elfDamage = elfDamage;
			this.noElfDeaths = noElfDeaths;
		}
		
		void move(Point nextPosition, Unit unit) {
			units.remove(unit.position);
			unit.position = nextPosition;
			units.put(nextPosition, unit);
		}
		
		int play() {
			int steps = 0;
			main: while (true) {
				for (Point position : new ArrayList<>(units.keySet())) {
					Unit unit = units.get(position);
					if (unit == null)
						continue;
					
					if (units.values().stream().noneMatch(u -> u.type != unit.type))
						break main;
					
					Point nextPosition = nextStep(unit);
					if (nextPosition != null)
						move(nextPosition, unit);
					
					Unit target = attackTarget(unit);
					if (target != null) {
						target.hp -= unit.type == Unit.TYPE_ELF ? elfDamage : 3;
						if (target.hp <= 0) {
							if (noElfDeaths && target.type == Unit.TYPE_ELF)
								return 0;
							units.remove(target.position);
						}
					}
				}
				steps++;
			}
			return steps * units.values().stream().mapToInt(u -> u.hp).sum();
		}
		
		Point nextStep(Unit unit) {
			List<Point> firstSteps = Arrays.stream(sortedDirections).map(unit.position::shift).filter(emptyPoints::contains).toList();
			for (Point firstStep : firstSteps) {
				Unit possibleTarget = units.get(firstStep);
				if (possibleTarget != null && possibleTarget.type != unit.type)
					return null;
			}
			
			Set<Point> targetPositions = units.values().stream()
					.filter(u -> u.type != unit.type)
					.map(u -> u.position)
					.flatMap(p -> Direction.stream().map(p::shift))
					.filter(emptyPoints::contains)
					.filter(p -> !units.containsKey(p))
					.collect(Collectors.toSet());
			
			Set<Path> paths = firstSteps.stream().filter(s -> !units.containsKey(s)).map(Path::new).collect(Collectors.toSet());
			Set<Point> visited = new HashSet<>(units.keySet());
			
			while (!paths.isEmpty()) {
				Optional<Point> targetStep = paths.stream().filter(p -> targetPositions.contains(p.current))
						.min(Comparator.comparing(Path::current, Point.COMPARATOR).thenComparing(Path::firstStep, Point.COMPARATOR))
						.map(Path::firstStep);
				if (targetStep.isPresent())
					return targetStep.get();
				
				for (Path path : paths)
					visited.add(path.current);
				paths = paths.stream().flatMap(p -> p.next(emptyPoints, visited)).collect(Collectors.toSet());
			}
			return null;
		}
		
		Unit attackTarget(Unit unit) {
			Unit target = null;
			for (Direction direction : sortedDirections) {
				Unit enemy = units.get(direction.shift(unit.position));
				if (enemy != null && enemy.type != unit.type && (target == null || enemy.hp < target.hp))
					target = enemy;
			}
			return target;
		}
	}
	
	static class Unit {
		static final int TYPE_ELF = 0;
		static final int TYPE_GOBLIN = 1;
		
		Point position;
		int type;
		int hp = 200;
		
		Unit(Point position, int type) {
			this.position = position;
			this.type = type;
		}
		
		Unit copy() {
			return new Unit(position, type);
		}
	}
	
	record Path(Point firstStep, Point current) {
		
		Path(Point firstStep) {
			this(firstStep, firstStep);
		}
		
		Stream<Path> next(Set<Point> emptyPoints, Set<Point> visited) {
			return Direction.stream().map(current::shift)
					.filter(emptyPoints::contains)
					.filter(next -> !visited.contains(next))
					.map(next -> new Path(firstStep, next));
		}
	}
}