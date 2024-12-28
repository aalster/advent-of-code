package org.advent.year2023.day21;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day21 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day21()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 16, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 3858, 636350496972143L)
		);
	}
	
	List<Point> field;
	Point start;
	int part1Steps;
	int part2Steps;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		Map<Character, List<Point>> allPoints = Point.readField(Utils.readLines(input));
		field = allPoints.get('#');
		start = allPoints.get('S').getFirst();
		part1Steps = switch (file) {
			case "example.txt" -> 6;
			case "input.txt" -> 64;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
		part2Steps = switch (file) {
			case "example.txt" -> 5000;
			case "input.txt" -> 26501365;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		List<PointVisit> visits = allVisitedPoints(field, start);
		int remainder = (part1Steps + start.x() + start.y()) % 2;
		return visits.stream()
				.filter(v -> v.steps() <= part1Steps)
				.filter(v -> (v.p().x() + v.p().y()) % 2 == remainder).count();
	}
	
	@Override
	public Object part2() {
		Point pointMin = Point.minBound(field).shift(-1, -1);
		Point pointMax = Point.maxBound(field).shift(1, 1);
		
		int width = pointMax.x() - pointMin.x() + 1;
		if (width != pointMax.y() - pointMin.y() + 1)
			throw new RuntimeException("Works only for squares");
		if (width % 2 != 1)
			throw new RuntimeException("Works only for odd width");
		if (start.x() != start.y() || start.x() != width / 2)
			throw new RuntimeException("Works only for start in center");
		if (field.stream().anyMatch(p -> p.x() == start.x() || p.y() == start.y()))
			throw new RuntimeException("Works only for empty start lines");
		
		// https://github.com/villuna/aoc23/wiki/A-Geometric-solution-to-advent-of-code-2023,-day-21
		// Старт всегда в центре и центральные строка и колонка без камней.
		// Кратчайший путь к соседним полям всегда идет через центральные дороги. Точки входа всегда посередине стороны.
		// Крайние поля затрагиваются совсем немного, периметр состоит только из 1 слоя полей которые достигаемы только частично.
		// Нужно посчитать кол-во полей, достигаемых полностью (учесть, что между полями нечетное кол-во
		// шагов, поэтому в каждом поле разное кол-во точек).
		// Крайние 4 поля, находящиеся на одном уровне с начальным имеют только одну точку входа.
		// Остальные крайние поля имеют 2 точки входа.
		
		long fullFieldsRadius = part2Steps / width; // Кол-во полностью достигаемых полей от центра до края, не включая центральное поле
		int remainingSteps = part2Steps % width; // Кол-во шагов после достижения центра крайнего полностью достигаемого поля
		if (remainingSteps < width / 2 || width < remainingSteps)
			throw new RuntimeException("Works only for 1 layer of perimeter field");
//		System.out.println("Width: " + width + ", full fields radius: " + fullFieldsRadius + ", remaining steps: " + remainingSteps);
		
		List<PointVisit> visits = allVisitedPoints(field, start);
		
		long fullEvenCount = visits.stream().filter(v -> v.steps() % 2 == 0).count();
		long fullOddCount = visits.stream().filter(v -> v.steps() % 2 == 1).count();
		// для крайних четных полей нужно считать углы поля, но вместо этого можно отнять от всего поля центральную часть
		long cornerEvenCount = visits.stream().filter(v -> v.steps() > remainingSteps && v.steps() % 2 == 0).count();
		long cornerOddCount = visits.stream().filter(v -> v.steps() > remainingSteps && v.steps() % 2 == 1).count();
		
		return
				(fullFieldsRadius + 1) * (fullFieldsRadius + 1) * fullOddCount // полные и неполные нечетные
				+ fullFieldsRadius * fullFieldsRadius * fullEvenCount // полные четные
				- (fullFieldsRadius + 1) * cornerOddCount // вычитаем лишние углы у крайних нечетных
				+ fullFieldsRadius * cornerEvenCount; // добавляем углы крайних четных
	}
	
	List<PointVisit> allVisitedPoints(List<Point> rocks, Point start) {
		Rect bounds = new Rect(Point.minBound(rocks).shift(-1, -1), Point.maxBound(rocks).shift(1, 1));
		Set<Point> visited = new HashSet<>();
		Set<Point> current = Set.of(start);
		List<PointVisit> visits = new ArrayList<>();
		int steps = 1;
		while (!current.isEmpty()) {
			current = current.stream()
					.flatMap(p -> Direction.stream().map(p::move))
					.filter(bounds::containsInclusive)
					.filter(p -> !rocks.contains(p))
					.filter(p -> !visited.contains(p))
					.collect(Collectors.toSet());
			visited.addAll(current);
			int _steps = steps;
			current.stream().map(p -> new PointVisit(p, _steps)).forEach(visits::add);
			steps++;
		}
		return visits;
	}
	
	record PointVisit(Point p, int steps) {
	}
}