package org.advent.year2023.day18;

import org.advent.common.BigPoint;
import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day18 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day18.class, "input.txt");
		List<String> lines = new ArrayList<>();
		while (input.hasNext()) {
			lines.add(input.nextLine());
		}
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static long part1(List<String> lines) {
		return solveNaive(lines.stream().map(Move::parse1).toList());
	}
	
	private static BigInteger part2(List<String> lines) {
		return solveFast(lines.stream().map(Move::parse2).toList());
	}
	
	private static int solveNaive(List<Move> moves) {
		Set<Point> path = new HashSet<>();
		Point current = new Point(0, 0);
		path.add(current);
		for (Move move : moves) {
			for (long i = 0; i < move.meters(); i++) {
				current = current.shift(move.direction());
				path.add(current);
			}
		}
		Set<Point> middle = path.stream()
				.collect(Collectors.groupingBy(Point::y))
				.values().stream()
				.filter(row -> row.size() == 2)
				.map(row -> Pair.of(row.getFirst(), row.getLast()))
				.filter(p -> Math.abs(p.left().x() - p.right().x()) > 2)
				.map(p -> new Point((p.left().x() + p.right().x()) / 2, p.left().y()))
				.collect(Collectors.toSet());
		
		Set<Point> inside = new HashSet<>();
		
		while (!middle.isEmpty()) {
			middle = middle.stream()
					.flatMap(p -> Direction.stream().map(p::shift))
					.filter(p -> !inside.contains(p))
					.filter(p -> !path.contains(p))
					.collect(Collectors.toSet());
			inside.addAll(middle);
		}

		return path.size() + inside.size();
	}
	
	private static BigInteger solveFast(List<Move> moves) {
		// https://en.wikipedia.org/wiki/Shoelace_formula
		List<BigPoint> edges = new ArrayList<>();
		BigInteger perimeter = BigInteger.ZERO;
		{
			BigPoint current = new BigPoint(BigInteger.ZERO, BigInteger.ZERO);
			for (Move move : moves) {
				edges.add(current);
				perimeter = perimeter.add(BigInteger.valueOf(move.meters()));
				current = current.shift(move.direction(), BigInteger.valueOf(move.meters()));
			}
		}
		
		BigInteger area = BigInteger.ZERO;
		for (int i = 0; i < edges.size(); i++) {
			BigPoint current = edges.get(i);
			BigPoint next = edges.get((i + 1) % edges.size());
			area = area.add(current.y().add(next.y()).multiply(current.x().subtract(next.x())));
		}
		return area.add(perimeter).divide(BigInteger.TWO).add(BigInteger.ONE);
	}
	
	record Move(Direction direction, long meters) {
		static final Map<String, Direction> directionsByLetter = Direction.stream()
				.collect(Collectors.toMap(d -> d.name().substring(0, 1), d -> d));
		static final Direction[] directionsByIndex = {Direction.RIGHT, Direction.DOWN, Direction.LEFT, Direction.UP};
		
		static Move parse1(String line) {
			String[] split = line.split(" ");
			return new Move(directionsByLetter.get(split[0]), Long.parseLong(split[1]));
		}
		
		static Move parse2(String line) {
			String hex = StringUtils.substringBefore(StringUtils.substringAfter(line, "#"), ")");
			long value = Long.parseLong(hex, 16);
			return new Move(directionsByIndex[(int) (value % 16)], value / 16);
		}
	}
}