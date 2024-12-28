package org.advent.year2023.day22;

import org.advent.common.Point3D;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day22 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day22()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 5, 7),
				new ExpectedAnswers("input.txt", 512, 98167)
		);
	}
	
	List<Brick> bricks;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		bricks = Utils.readLines(input).stream().map(Brick::parse).toList();
	}
	
	@Override
	public Object part1() {
		BrickRelations relations = brickRelations(bricks);
		
		Set<String> singleSupports = relations.supportedBy().values().stream()
				.filter(sups -> sups.size() == 1)
				.map(List::getFirst)
				.collect(Collectors.toSet());
		return bricks.stream().filter(brick -> !singleSupports.contains(brick.name())).count();
	}
	
	@Override
	public Object part2() {
		BrickRelations relations = brickRelations(bricks);
		
		Set<String> singleSupports = relations.supportedBy().values().stream()
				.filter(sups -> sups.size() == 1)
				.map(List::getFirst)
				.collect(Collectors.toSet());
		return bricks.stream()
				.filter(brick -> singleSupports.contains(brick.name()))
				.mapToInt(brick -> countFallen(brick.name(), relations))
				.sum();
	}
	
	BrickRelations brickRelations(List<Brick> bricks) {
		List<Brick> flying = new ArrayList<>(bricks);
		List<Brick> settled = new ArrayList<>();
		Map<String, List<String>> supports = new HashMap<>();
		Map<String, List<String>> supportedBy = new HashMap<>();
		
		while (!flying.isEmpty()) {
			for (Brick lowest : lowest(flying)) {
				List<Brick> lowestSupports = findSupports(settled, lowest);
				int settleZ = lowestSupports.isEmpty() ? 1 : lowestSupports.getFirst().topZ() + 1;
				flying.remove(lowest);
				settled.add(lowest.shift(settleZ - lowest.bottomZ()));
				
				for (Brick support : lowestSupports) {
					supports.computeIfAbsent(support.name(), k -> new ArrayList<>()).add(lowest.name());
					supportedBy.computeIfAbsent(lowest.name(), k -> new ArrayList<>()).add(support.name());
				}
			}
		}
		return new BrickRelations(supports, supportedBy);
	}
	
	int countFallen(String brick, BrickRelations relations) {
		Set<String> fallen = new HashSet<>();
		Set<String> current = Set.of(brick);
		
		while (!current.isEmpty()) {
			fallen.addAll(current);
			current = current.stream()
					.flatMap(c -> relations.supports.getOrDefault(c, List.of()).stream())
					.filter(next -> fallen.containsAll(relations.supportedBy.get(next)))
					.collect(Collectors.toSet());
		}
		return fallen.size() - 1;
	}
	
	List<Brick> findSupports(List<Brick> settled, Brick lowest) {
		Rect shadow = lowest.shadow();
		List<Brick> intersections = settled.stream().filter(b -> b.shadow().intersectsInclusive(shadow)).toList();
		int maxZ = intersections.stream().mapToInt(Brick::topZ).max().orElse(0);
		return intersections.stream().filter(b -> b.topZ() == maxZ).toList();
	}
	
	List<Brick> lowest(List<Brick> bricks) {
		List<Brick> lowest = new ArrayList<>();
		int minZ = Integer.MAX_VALUE;
		for (Brick brick : bricks) {
			int bottom = brick.bottomZ();
			if (bottom < minZ) {
				minZ = bottom;
				lowest.clear();
				lowest.add(brick);
			} else if (bottom == minZ) {
				lowest.add(brick);
			}
		}
		return lowest;
	}
	
	record BrickRelations(Map<String, List<String>> supports, Map<String, List<String>> supportedBy) {
	}
	
	record Brick(String name, Point3D from, Point3D to) {
		
		int bottomZ() {
			return from().z();
		}
		
		int topZ() {
			return to().z();
		}
		
		Rect shadow() {
			return new Rect(from.x(), to.x(), from.y(), to.y());
		}
		
		Brick shift(int dz) {
			return new Brick(name, from.shift(0, 0, dz), to.shift(0, 0, dz));
		}
		
		static int nameCounter = 0;
		static String nextName() {
			int count = nameCounter;
			int maxSymbolCode = 'Z' - 'A' + 1;
			
			StringBuilder name = new StringBuilder();
			while (count >= 0) {
				name.insert(0, (char) ('A' + (count % maxSymbolCode)));
				count = count / maxSymbolCode - 1;
			}
			nameCounter++;
			return name.toString();
		}
		
		static Brick parse(String line) {
			String[] split = line.split("~");
			Point3D a = Point3D.parse(split[0]);
			Point3D b = Point3D.parse(split[1]);
			return new Brick(
					nextName(),
					new Point3D(Math.min(a.x(), b.x()), Math.min(a.y(), b.y()), Math.min(a.z(), b.z())),
					new Point3D(Math.max(a.x(), b.x()), Math.max(a.y(), b.y()), Math.max(a.z(), b.z())));
		}
	}
}