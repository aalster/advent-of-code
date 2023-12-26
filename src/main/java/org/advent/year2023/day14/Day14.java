package org.advent.year2023.day14;

import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day14 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day14.class, "input.txt");
		List<String> lines = new ArrayList<>();
		while (input.hasNext()) {
			lines.add(input.nextLine());
		}
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static long part1(List<String> lines) {
		return Field.parse(lines).tiltUp().countLoadUp();
	}
	
	private static long part2(List<String> lines) {
		Field field = Field.parse(lines);
		int totalCycles = 1_000_000_000;
		Map<Field, Integer> cyclesInfo = new LinkedHashMap<>();
		for (int i = 0; i < totalCycles; i++) {
			field = field.cycle();
			Integer prev = cyclesInfo.put(field, i);
			if (prev != null && i < totalCycles / 2) {
				System.out.println("Repeating pattern: " + prev + " = " + i);
				int repeatDistance = i - prev;
				i += repeatDistance * ((totalCycles - i) / repeatDistance - 1);
			}
		}
		return field.countLoadUp();
	}
	
	private record Field(Set<Point> squares, Set<Point> spheres) {
		
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
		
		void print() {
			IntSummaryStatistics xStats = Stream.of(squares, spheres).flatMap(Collection::stream).mapToInt(Point::x).summaryStatistics();
			IntSummaryStatistics yStats = Stream.of(squares, spheres).flatMap(Collection::stream).mapToInt(Point::y).summaryStatistics();
			System.out.println();
			for (int y = yStats.getMin(); y <= yStats.getMax(); y++) {
				for (int x = xStats.getMin(); x <= xStats.getMax(); x++) {
					Point point = new Point(x, y);
					System.out.print(squares.contains(point) ? '#' : spheres.contains(point) ? 'O' : '.');
				}
				System.out.println();
			}
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