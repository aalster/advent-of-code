package org.advent.year2023.day21;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day21 {
	record TestCase(String file, int steps1, int steps2) {}
	private static final TestCase example = new TestCase("example.txt", 6, 5000);
	private static final TestCase input = new TestCase("input.txt", 64, 26501365);
	private static final TestCase test = input;
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day21.class, test.file());
		Map<Character, List<Point>> allPoints = Point.readField(Utils.readLines(input));
		List<Point> field = allPoints.get('#');
		Point start = allPoints.get('S').getFirst();
		
		System.out.println("Answer 1: " + part1(field, start, test.steps1()));
		System.out.println("Answer 2: " + part2(field, start, test.steps2()));
	}
	
	private static long part1(List<Point> rocks, Point start, int steps) {
		List<Point> visited = allVisitedPoints(rocks, start, steps);
		int remainder = (steps + start.x() + start.y()) % 2;
		return visited.stream().filter(p -> (p.x() + p.y()) % 2 == remainder).count();
	}
	
	private static List<Point> allVisitedPoints(List<Point> rocks, Point start, int steps) {
		Rect bounds = new Rect(Point.minBound(rocks).shift(-1, -1), Point.maxBound(rocks).shift(1, 1));
		List<Point> visited = new ArrayList<>();
		Set<Point> current = Set.of(start);
		for (int i = 0; i < steps; i++) {
			current = current.stream()
					.flatMap(p -> Direction.stream().map(p::move))
					.filter(bounds::containsInclusive)
					.filter(p -> !rocks.contains(p))
					.filter(p -> !visited.contains(p))
					.collect(Collectors.toSet());
			if (current.isEmpty())
				break;
			visited.addAll(current);
		}
		return visited;
	}
	
	private static BigInteger part2(List<Point> rocks, Point start, int steps) {
		Point pointMin = Point.minBound(rocks).shift(-1, -1);
		Point pointMax = Point.maxBound(rocks).shift(1, 1);
		
		int width = pointMax.x() - pointMin.x() + 1;
		if (width != pointMax.y() - pointMin.y() + 1)
			throw new RuntimeException("Works only for squares");
		if (width % 2 != 1)
			throw new RuntimeException("Works only for odd width");
		if (start.x() != start.y() || start.x() != width / 2)
			throw new RuntimeException("Works only for start in center");
		if (rocks.stream().anyMatch(p -> p.x() == start.x() || p.y() == start.y()))
			throw new RuntimeException("Works only for empty start lines");
		
		// https://github.com/villuna/aoc23/wiki/A-Geometric-solution-to-advent-of-code-2023,-day-21
		// Старт всегда в центре и центральные строка и колонка без камней.
		// Кратчайший путь к соседним полям всегда идет через центральные дороги. Точки входа всегда посередине стороны.
		// Крайние поля затрагиваются совсем немного, периметр состоит только из 1 слоя полей которые достигаемы только частично.
		// Нужно посчитать кол-во полей, достигаемых полностью (учесть, что между полями нечетное кол-во
		// шагов, поэтому в каждом поле разное кол-во точек).
		// Крайние 4 поля, находящиеся на одном уровне с начальным имеют только одну точку входа.
		// Остальные крайние поля имеют 2 точки входа.
		
		long fullFieldsRadius = steps / width; // Кол-во полностью достигаемых полей от центра до края, не включая центральное поле
		int remainingSteps = steps % width; // Кол-во шагов после достижения центра крайнего полностью достигаемого поля
		if (remainingSteps < width / 2 || width < remainingSteps)
			throw new RuntimeException("Works only for 1 layer of perimeter field");
		System.out.println("Width: " + width + ", full fields radius: " + fullFieldsRadius + ", remaining steps: " + remainingSteps);
		
		List<Point> fullPoints = allVisitedPoints(rocks, start, Integer.MAX_VALUE);
		// для крайних четных полей нужно считать углы поля, но вместо этого можно отнять от всего поля центральную часть
		List<Point> cornerPoints = allVisitedPoints(rocks, start, remainingSteps);
		
		BigInteger fullEvenCount = BigInteger.valueOf(fullPoints.stream().filter(p -> (p.x() + p.y()) % 2 == 0).count());
		BigInteger fullOddCount = BigInteger.valueOf(fullPoints.stream().filter(p -> (p.x() + p.y()) % 2 == 1).count());
		
		BigInteger cornerEvenCount = fullEvenCount.subtract(BigInteger.valueOf(cornerPoints.stream().filter(p -> (p.x() + p.y()) % 2 == 0).count()));
		BigInteger cornerOddCount = fullOddCount.subtract(BigInteger.valueOf(cornerPoints.stream().filter(p -> (p.x() + p.y()) % 2 == 1).count()));
		
		return Stream.of(
						BigInteger.valueOf(fullFieldsRadius + 1).pow(2).multiply(fullOddCount), // полные и неполные нечетные
						BigInteger.valueOf(fullFieldsRadius).pow(2).multiply(fullEvenCount), // полные четные
						fullEvenCount.add(BigInteger.ONE).multiply(cornerOddCount).negate(), // вычитаем лишние углы у крайних нечетных
						fullEvenCount.multiply(cornerEvenCount)) // добавляем углы крайних четных
				.reduce(BigInteger.ZERO, BigInteger::add);
	}
	
	record PointVisit(Point p, int steps) {
	
	}
}
//636350476889926
//636350478298223