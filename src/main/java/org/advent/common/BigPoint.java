package org.advent.common;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public record BigPoint(BigInteger x, BigInteger y) {
	
	public BigPoint shift(Direction d, BigInteger amount) {
		return new BigPoint(
				d == Direction.LEFT ? x.subtract(amount) : d == Direction.RIGHT ? x.add(amount) : x,
				d == Direction.UP ? y.subtract(amount) : d == Direction.DOWN ? y.add(amount) : y);
	}
	
	public BigInteger manhattanDistance(BigPoint p) {
		return p.x.subtract(x).abs().add(p.y.subtract(y).abs());
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	public static BigInteger maxX(Collection<BigPoint> points) {
		return points.stream().map(BigPoint::x).max(Comparator.naturalOrder()).orElseThrow();
	}
	
	public static BigInteger maxY(Collection<BigPoint> points) {
		return points.stream().map(BigPoint::y).max(Comparator.naturalOrder()).orElseThrow();
	}
	
	public static void printField(Set<BigPoint> field, char filled, char empty) {
		printField(field, point -> field.contains(point) ? filled : empty);
	}
	
	public static void printField(Set<BigPoint> field, Function<BigPoint, Character> symbol) {
		if (field.isEmpty())
			return;
		
		List<BigInteger> xs = field.stream().map(BigPoint::x).sorted().toList();
		BigInteger minX = xs.getFirst();
		BigInteger maxX = xs.getLast();
		List<BigInteger> ys = field.stream().map(BigPoint::y).sorted().toList();
		BigInteger minY = ys.getFirst();
		BigInteger maxY = ys.getLast();
		for (BigInteger y = minY; y.compareTo(maxY) <= 0; y = y.add(BigInteger.ONE)) {
			for (BigInteger x = minX; x.compareTo(maxX) <= 0; x = x.add(BigInteger.ONE))
				System.out.print(symbol.apply(new BigPoint(x, y)));
			System.out.println();
		}
	}
}