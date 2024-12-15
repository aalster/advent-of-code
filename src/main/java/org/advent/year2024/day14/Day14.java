package org.advent.year2024.day14;

import lombok.SneakyThrows;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day14 {
	static final Data example = new Data("example.txt", 11, 7);
	static final Data input = new Data("input.txt", 101, 103);
	static final Data data = input;
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day14.class, data.file);
		List<Robot> robots = Utils.readLines(input).stream().map(Robot::parse).map(Robot::makePositive).toList();
		
		System.out.println("Answer 1: " + part1(robots));
		System.out.println("Answer 2: " + part2(robots));
	}
	
	private static long part1(List<Robot> robots) {
		return safetyFactor(robots.stream().map(r -> r.move(100)).toList());
	}
	
	@SneakyThrows
	private static long part2(List<Robot> robots) {
		int seconds = 0;
		// Подбором коэффициента пересматриваем все ситуации с наименьшим safetyFactor и находим визуально
		int interestingSafetyFactor = (int) (minSafetyFactor(robots) * 1.12);
		while (seconds < data.width * data.height) {
			seconds++;
			robots = robots.stream().map(Robot::move).toList();
			int safetyFactor = safetyFactor(robots);
			if (safetyFactor < interestingSafetyFactor) {
				Set<Point> positions = robots.stream().map(Robot::p).collect(Collectors.toSet());
				System.out.println("\nSeconds: " + seconds + ", safety factor: " + safetyFactor);
				Point.printField(positions, p -> positions.contains(p) ? '#' : '.');
				Thread.sleep(500);
			}
		}
		return 0;
	}
	
	static int minSafetyFactor(List<Robot> robots) {
		int seconds = 0;
		int minSafetyFactor = Integer.MAX_VALUE;
		while (seconds < data.width * data.height) {
			seconds++;
			robots = robots.stream().map(Robot::move).toList();
			minSafetyFactor = Math.min(minSafetyFactor, safetyFactor(robots));
		}
		return minSafetyFactor;
	}
	
	static int safetyFactor(List<Robot> robots) {
		int quadrantWidth = data.width / 2;
		int quadrantHeight = data.height / 2;
		Map<String, List<Robot>> quadrants = robots.stream().collect(Collectors.groupingBy(
				r -> (r.p.y() < quadrantHeight ? "T" : (r.p.y() >= data.height - quadrantHeight ? "B" : "C"))
						+ (r.p.x() < quadrantWidth ? "L" : (r.p.x() >= data.width - quadrantWidth ? "R" : "C"))));
		return Stream.of("TL", "TR", "BL", "BR")
				.map(q -> quadrants.getOrDefault(q, List.of()))
				.mapToInt(List::size)
				.reduce(1, (l, r) -> l * r);
	}
	
	record Data(String file, int width, int height) {
	}
	
	record Robot(Point p, Point v) {
		
		Robot move() {
			return new Robot(new Point((p.x() + v.x()) % data.width, (p.y() + v.y()) % data.height), v);
		}
		
		Robot move(int seconds) {
			return new Robot(new Point((p.x() + v.x() * seconds) % data.width, (p.y() + v.y() * seconds) % data.height), v);
		}
		
		Robot makePositive() {
			return new Robot(p, new Point((v.x() + data.width) % data.width, (v.y() + data.height) % data.height));
		}
		
		static Robot parse(String line) {
			String[] split = line.replace("p=", "").split(" v=");
			return new Robot(Point.parse(split[0]), Point.parse(split[1]));
		}
	}
}