package org.advent.year2023.day14;

import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day14 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day14()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 136, 64),
				new ExpectedAnswers("input.txt", 110779, 86069)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		lines = Utils.readLines(Utils.scanFileNearClass(getClass(), file));
	}
	
	@Override
	public Object part1() {
		return Field.parse(lines).tiltUp().countLoadUp();
	}
	
	@Override
	public Object part2() {
		Field field = Field.parse(lines);
		int totalCycles = 1_000_000_000;
		Map<Field, Integer> cyclesInfo = new LinkedHashMap<>();
		for (int i = 0; i < totalCycles; i++) {
			field = field.cycle();
			Integer prev = cyclesInfo.put(field, i);
			if (prev != null && i < totalCycles / 2) {
				int repeatDistance = i - prev;
				i += repeatDistance * ((totalCycles - i) / repeatDistance - 1);
			}
		}
		return field.countLoadUp();
	}
	
	record Field(Set<Point> squares, Set<Point> spheres) {
		
		Field cycle() {
			return tiltUp().rotateRight()
					.tiltUp().rotateRight()
					.tiltUp().rotateRight()
					.tiltUp().rotateRight();
		}
		
		Field rotateRight() {
			int maxX = Stream.of(squares, spheres).flatMap(Collection::stream).mapToInt(Point::x).max().orElseThrow();
			UnaryOperator<Set<Point>> rotation = points -> points.stream().map(p -> new Point(maxX - p.y(), p.x())).collect(Collectors.toSet());
			return new Field(rotation.apply(squares), rotation.apply(spheres));
		}
		
		Field tiltUp() {
			Set<Point> tilted = spheres.stream()
					.map(sphere -> {
						int countSpheres = 0;
						int finalPosition = 0;
						for (int y = sphere.y() - 1; y >= 0; y--) {
							Point current = new Point(sphere.x(), y);
							if (spheres.contains(current)) {
								countSpheres++;
								continue;
							}
							if (squares.contains(current)) {
								finalPosition = y + 1;
								break;
							}
						}
						return new Point(sphere.x(), finalPosition + countSpheres);
					})
					.collect(Collectors.toSet());
			return new Field(squares, tilted);
		}
		
		long countLoadUp() {
			int maxY = Stream.of(squares, spheres).flatMap(Collection::stream).mapToInt(Point::y).max().orElseThrow();
			return spheres.stream()
					.mapToLong(Point::y)
					.map(y -> maxY - y + 1)
					.sum();
		}
		
		static Field parse(List<String> lines) {
			Set<Point> squares = new HashSet<>();
			Set<Point> spheres = new HashSet<>();
			int y = 0;
			for (String line : lines) {
				for (int x = 0; x < line.length(); x++) {
					char c = line.charAt(x);
					switch (c) {
						case '#' -> squares.add(new Point(x, y));
						case 'O' -> spheres.add(new Point(x, y));
						default -> {}
					}
				}
				y++;
			}
			return new Field(squares, spheres);
		}
	}
}