package org.advent.year2024.day12;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day12 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day12.class, "input.txt");
		Map<Point, Character> field = Point.readFieldMap(Utils.readLines(input));
		
		System.out.println("Answer 1: " + part1(field));
		System.out.println("Answer 2: " + part2(field));
	}
	
	private static long part1(Map<Point, Character> field) {
		return findRegions(field).stream().mapToLong(r -> r.size() * perimeter(r)).sum();
	}
	
	private static long part2(Map<Point, Character> field) {
		return findRegions(field).stream().mapToLong(r -> r.size() * sides(r)).sum();
	}
	
	static List<Set<Point>> findRegions(Map<Point, Character> field) {
		field = new HashMap<>(field);
		List<Set<Point>> regions = new ArrayList<>();
		
		while (!field.isEmpty()) {
			Set<Point> region = getRegion(field);
			for (Point point : region)
				field.remove(point);
			regions.add(region);
		}
		return regions;
	}
	
	static Set<Point> getRegion(Map<Point, Character> field) {
		Set<Point> region = new HashSet<>();
		Point start = field.keySet().iterator().next();
		Character type = field.get(start);
		
		Set<Point> current = Set.of(start);
		while (!current.isEmpty()) {
			region.addAll(current);
			current = current.stream()
					.flatMap(c -> Direction.stream().map(c::shift))
					.filter(n -> !region.contains(n))
					.filter(n -> field.get(n) == type)
					.collect(Collectors.toSet());
		}
		return region;
	}
	
	static long perimeter(Set<Point> region) {
		return region.stream().flatMap(p -> Direction.stream().map(p::shift)).filter(n -> !region.contains(n)).count();
	}
	
	static long sides(Set<Point> region) {
		return horizontalSides(region) + horizontalSides(rotate(region));
	}
	
	static Set<Point> rotate(Set<Point> region) {
		return region.stream().map(p -> new Point(-p.y(), p.x())).collect(Collectors.toSet());
	}
	
	static long horizontalSides(Set<Point> region) {
		Map<Integer, Map<Integer, List<HorizontalFence>>> fences = new HashMap<>();
		region.stream()
				.flatMap(p -> Stream.of(Direction.UP, Direction.DOWN)
						.map(p::shift)
						.filter(n -> !region.contains(n))
						.map(n -> new HorizontalFence(p.x(), p.x(), p.y(), n.y())))
				.forEach(fence ->
						fences.computeIfAbsent(fence.inY, k -> new HashMap<>())
								.computeIfAbsent(fence.outY, k -> new ArrayList<>())
								.add(fence));
		
		List<HorizontalFence> mergedFences = new ArrayList<>();
		for (Map<Integer, List<HorizontalFence>> nested : fences.values()) {
			for (List<HorizontalFence> sameYFences : nested.values()) {
				mergeFences: while (!sameYFences.isEmpty()) {
					HorizontalFence fence = sameYFences.removeFirst();
					for (Iterator<HorizontalFence> iterator = sameYFences.iterator(); iterator.hasNext(); ) {
						HorizontalFence merged = fence.merge(iterator.next());
						if (merged != null) {
							iterator.remove();
							sameYFences.addFirst(merged);
							continue mergeFences;
						}
					}
					mergedFences.add(fence);
				}
			}
		}
		return mergedFences.size();
	}
	
	record HorizontalFence(int minX, int maxX, int inY, int outY) {
		
		HorizontalFence merge(HorizontalFence other) {
			if (other.maxX + 1 == minX)
				return new HorizontalFence(other.minX, maxX, inY, outY);
			if (maxX + 1 == other.minX)
				return new HorizontalFence(minX, other.maxX, inY, outY);
			return null;
		}
	}
}