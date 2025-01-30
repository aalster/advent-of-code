package org.advent.year2018.day23;

import org.advent.common.Axis3D;
import org.advent.common.Pair;
import org.advent.common.Point3D;
import org.advent.common.Region3D;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day23 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day23()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 7, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 36),
				new ExpectedAnswers("input.txt", 240, 116547949)
		);
	}
	
	List<Nanobot> nanobots;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		nanobots = Utils.readLines(input).stream().map(Nanobot::parse).toList();
	}
	
	@Override
	public Object part1() {
		Nanobot strongest = nanobots.stream().max(Comparator.comparing(Nanobot::radius)).orElseThrow();
		return nanobots.stream().filter(n -> n.position.distanceTo(strongest.position) <= strongest.radius).count();
	}
	
	@Override
	public Object part2() {
		int maxCoordinate = nanobots.stream()
				.flatMapToInt(n ->
						IntStream.of(n.position.x(), n.position.y(), n.position.z()).map(Math::abs).map(p -> p + n.radius))
				.max()
				.orElseThrow();
		
		// Приоритет для секций с наибольшим кол-вом наноботов, наибольшим размером
		// и ближайших к началу (проверяем расстояние до ближайшего угла)
		Comparator<Section> gridComparator = Comparator.comparing((Section g) -> -g.nanobots.size())
				// Учитывая что все регионы - кубы, вместо объема можно сравнивать длину ребра
				.thenComparing(g -> -g.sideLength())
				.thenComparing(g -> g.region.allCorners().stream().mapToInt(c -> c.distanceTo(Point3D.ZERO)).min().orElseThrow());
		Queue<Section> sections = new PriorityQueue<>(gridComparator);
		sections.add(new Section(Region3D.fromCenter(Point3D.ZERO, Integer.highestOneBit(maxCoordinate) << 1), nanobots));
		
		while (!sections.isEmpty()) {
			Section current = sections.poll();
			if (current.sideLength() > 0) {
				current.split().forEach(sections::add);
				continue;
			}
			Region3D region = current.region;
			return new Point3D(region.minX(), region.minY(), region.minZ()).distanceTo(Point3D.ZERO);
		}
		return null;
	}
	
	record Section(Region3D region, List<Nanobot> nanobots) {
		
		int sideLength() {
			return region.maxX() - region.minX();
		}
		
		Stream<Section> split() {
			return halves(region, Axis3D.X)
					.flatMap(r -> halves(r, Axis3D.Y))
					.flatMap(r -> halves(r, Axis3D.Z))
					.map(r -> new Section(r, nanobots.stream().filter(n -> inRange(r, n)).toList()))
					.filter(s -> !s.nanobots.isEmpty());
		}
		
		Stream<Region3D> halves(Region3D region, Axis3D axis) {
			return Pair.stream(region.cut(axis, (axis.minOfRegion(region) + axis.maxOfRegion(region)) / 2 + 1));
		}
		
		boolean inRange(Region3D region, Nanobot nanobot) {
			// https://www.reddit.com/r/adventofcode/comments/a8s17l/comment/ecl4emt/
			int distance = 0;
			for (Axis3D axis : Axis3D.values()) {
				int min = axis.minOfRegion(region);
				int max = axis.maxOfRegion(region);
				int position = axis.ofPoint(nanobot.position);
				distance += Math.abs(position - min) + Math.abs(position - max) - (max - min);
			}
			return distance / 2 <= nanobot.radius;
		}
	}
	
	record Nanobot(Point3D position, int radius) {
		
		static Nanobot parse(String line) {
			String[] split = Utils.removeEach(line, "<", ">").replace(", ", "=").split("=");
			return new Nanobot(Point3D.parse(split[1]), Integer.parseInt(split[3]));
		}
	}
}