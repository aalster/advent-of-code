package org.advent.year2024.day14;

import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day14 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day14()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 12, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 223020000, 7338)
		);
	}
	
	static Data data;
	List<Robot> robots;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		data = switch (file) {
			case "example.txt" -> new Data(11, 7);
			case "input.txt" -> new Data(101, 103);
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
		robots = Utils.readLines(input).stream().map(Robot::parse).map(Robot::makePositive).toList();
	}
	
	@Override
	public Object part1() {
		return safetyFactor(robots.stream().map(r -> r.move(100)).toList(), 1);
	}
	
	@Override
	public Object part2() {
		List<Robot> currentRobots = robots;
		int seconds = 0;
		BigInteger minSafetyFactor = BigInteger.valueOf(Long.MAX_VALUE).pow(4);
		int minSafetyFactorSeconds = 0;
		while (seconds < data.width * data.height) {
			seconds++;
			currentRobots = currentRobots.stream().map(Robot::move).toList();
			BigInteger safetyFactor = safetyFactor(currentRobots, 2);
			if (safetyFactor.compareTo(minSafetyFactor) < 0) {
				minSafetyFactor = safetyFactor;
				minSafetyFactorSeconds = seconds;
			}
		}
		
//		int _minSafetyFactorSeconds = minSafetyFactorSeconds;
//		Set<Point> positions = robots.stream()
//				.map(r -> r.move(_minSafetyFactorSeconds))
//				.map(Robot::p)
//				.collect(Collectors.toSet());
//		Point.printField(positions, p -> positions.contains(p) ? '#' : '.');
		
		return minSafetyFactorSeconds;
	}
	
	BigInteger safetyFactor(List<Robot> robots, int level) {
		return safetyFactorRecursive(robots.stream().map(Robot::p).toList(), data.width, data.height, level);
	}
	
	BigInteger safetyFactorRecursive(List<Point> positions, int width, int height, int level) {
		int quadrantWidth = width / 2;
		int quadrantHeight = height / 2;
		Map<String, List<Point>> quadrants = positions.stream().collect(Collectors.groupingBy(
				p -> (p.y() < quadrantHeight ? "T" : (p.y() == quadrantHeight ? "C" : "B"))
						+ (p.x() < quadrantWidth ? "L" : (p.x() == quadrantWidth ? "C" : "R"))));
		if (level <= 1)
			return Stream.of("TL", "TR", "BL", "BR")
					.map(q -> quadrants.getOrDefault(q, List.of()))
					.map(p -> BigInteger.valueOf(p.size()))
					.reduce(BigInteger.ONE, BigInteger::multiply);
		return Stream.of(
				Pair.of("TL", new Point(0, 0)),
				Pair.of("TR", new Point(-quadrantWidth, 0)),
				Pair.of("BL", new Point(0, -quadrantHeight)),
				Pair.of("BR", new Point(-quadrantWidth, -quadrantHeight)))
				.map(p -> quadrants.getOrDefault(p.left(), List.of()).stream().map(p.right()::shift).toList())
				.map(p -> safetyFactorRecursive(p, quadrantWidth, quadrantHeight, level - 1))
				.reduce(BigInteger.ONE, BigInteger::multiply);
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
	
	record Data(int width, int height) {
	}
}