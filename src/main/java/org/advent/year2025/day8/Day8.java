package org.advent.year2025.day8;

import org.advent.common.Pair;
import org.advent.common.Point3D;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Day8 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day8()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 40, 25272),
				new ExpectedAnswers("input.txt", 67488, 3767453340L)
		);
	}
	
	int connectionsLimit;
	Point3D[] boxes;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		boxes = Utils.readLines(input).stream().map(Point3D::parse).toArray(Point3D[]::new);
		connectionsLimit = switch (file) {
			case "example.txt" -> 10;
			case "input.txt" -> 1000;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		Collection<Pair<Point3D, Point3D>> closestPoints = closestPoints(boxes);
		closestPoints = new ArrayList<>(closestPoints).subList(0, Math.min(closestPoints.size(), connectionsLimit));
		Map<Point3D, Set<Point3D>> circuits = Arrays.stream(boxes)
				.collect(Collectors.toMap(p -> p, p -> new HashSet<>(List.of(p))));
		
		for (Pair<Point3D, Point3D> closest : closestPoints) {
			Point3D left = closest.left();
			Point3D right = closest.right();
			Set<Point3D> leftCircuit = circuits.get(left);
			if (!leftCircuit.contains(right)) {
				leftCircuit.addAll(circuits.get(right));
				leftCircuit.forEach(p -> circuits.put(p, leftCircuit));
			}
		}
		return circuits.values().stream()
				.distinct()
				.map(Set::size)
				.sorted(Comparator.<Integer>naturalOrder().reversed())
				.limit(3)
				.reduce(1, (a, b) -> a * b);
	}
	
	@Override
	public Object part2() {
		Collection<Pair<Point3D, Point3D>> closestPoints = closestPoints(boxes);
		Map<Point3D, Set<Point3D>> circuits = Arrays.stream(boxes)
				.collect(Collectors.toMap(p -> p, p -> new HashSet<>(List.of(p))));
		
		for (Pair<Point3D, Point3D> closest : closestPoints) {
			Point3D left = closest.left();
			Point3D right = closest.right();
			Set<Point3D> leftCircuit = circuits.get(left);
			if (!leftCircuit.contains(right)) {
				leftCircuit.addAll(circuits.get(right));
				
				if (leftCircuit.size() >= boxes.length)
					return (long) left.x() * right.x();
				
				leftCircuit.forEach(p -> circuits.put(p, leftCircuit));
			}
		}
		return null;
	}
	
	private Collection<Pair<Point3D, Point3D>> closestPoints(Point3D[] points) {
		SortedMap<Long, Pair<Point3D, Point3D>> distances = new TreeMap<>();
		for (int l = 0; l < points.length; l++) {
			Point3D left = points[l];
			for (int r = l + 1; r < points.length; r++) {
				Point3D right = points[r];
				distances.put(distanceSquared(left, right), Pair.of(left, right));
			}
		}
		return distances.values();
	}
	
	private long distanceSquared(Point3D left, Point3D right) {
		long dx = Math.abs(left.x() - right.x());
		long dy = Math.abs(left.y() - right.y());
		long dz = Math.abs(left.z() - right.z());
		return dx * dx + dy * dy + dz * dz;
	}
}