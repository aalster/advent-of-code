package org.advent.year2023.day11;

import org.advent.common.BigPoint;
import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day11 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day11.class, "input.txt");
		Set<BigPoint> galaxies = new HashSet<>();
		int y = 0;
		while (input.hasNext()) {
			String line = input.nextLine();
			for (int x = 0; x < line.length(); x++) {
				if (line.charAt(x) == '#')
					galaxies.add(new BigPoint(BigInteger.valueOf(x), BigInteger.valueOf(y)));
			}
			y++;
		}
		
		System.out.println("Answer 1: " + part1(galaxies));
		System.out.println("Answer 2: " + part2(galaxies));
	}
	
	private static BigInteger part1(Set<BigPoint> galaxies) {
		return solve(expand(galaxies, BigInteger.valueOf(2)));
	}
	
	private static BigInteger part2(Set<BigPoint> galaxies) {
		return solve(expand(galaxies, BigInteger.valueOf(1_000_000)));
	}
	
	private static BigInteger solve(Set<BigPoint> galaxies) {
		galaxies = new HashSet<>(galaxies);
		List<Pair<BigPoint, BigPoint>> pairs = new ArrayList<>();
		while (!galaxies.isEmpty()) {
			BigPoint current = galaxies.iterator().next();
			galaxies.remove(current);
			galaxies.stream().map(p -> Pair.of(current, p)).forEach(pairs::add);
		}
		BigInteger result = BigInteger.ZERO;
		for (Pair<BigPoint, BigPoint> pair : pairs)
			result = result.add(pair.left().manhattanDistance(pair.right()));
		return result;
	}
	
	private static Set<BigPoint> expand(Set<BigPoint> galaxies, BigInteger expansion) {
		BigInteger expansionDelta = expansion.subtract(BigInteger.ONE);
		
		BigInteger x = BigPoint.maxX(galaxies);
		while (x.compareTo(BigInteger.ZERO) > 0) {
			BigInteger x_ = x;
			if (galaxies.stream().allMatch(p -> p.x().compareTo(x_) != 0)) {
				galaxies = galaxies.stream()
						.map(p -> p.x().compareTo(x_) > 0 ? p.shift(Direction.RIGHT, expansionDelta) : p)
						.collect(Collectors.toSet());
			}
			x = x.subtract(BigInteger.ONE);
		}
		BigInteger y = BigPoint.maxY(galaxies);
		while (y.compareTo(BigInteger.ZERO) > 0) {
			BigInteger y_ = y;
			if (galaxies.stream().allMatch(p -> p.y().compareTo(y_) != 0)) {
				galaxies = galaxies.stream()
						.map(p -> p.y().compareTo(y_) > 0 ? p.shift(Direction.DOWN, expansionDelta) : p)
						.collect(Collectors.toSet());
			}
			y = y.subtract(BigInteger.ONE);
		}
		return galaxies;
	}
	
}