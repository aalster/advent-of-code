package org.advent.year2022.day15;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day15 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day15()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 26, 56000011),
				new ExpectedAnswers("input.txt", 5403290, 10291582906626L)
		);
	}
	
	List<Sensor> sensors;
	int part1TargetY;
	Rect part2Region;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		sensors = Utils.readLines(input).stream().map(Sensor::parse).toList();
		part1TargetY = switch (file) {
			case "example.txt" -> 10;
			case "input.txt" -> 2000000;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
		part2Region = switch (file) {
			case "example.txt" -> new Rect(0, 20, 0, 20);
			case "input.txt" -> new Rect(0, 4000000, 0, 4000000);
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		List<Range> ranges = Range.combine(sensors.stream()
				.map(s -> s.rowCoverRange(part1TargetY))
				.filter(Objects::nonNull)
				.sorted(Comparator.comparing(Range::from))
				.toList());
		
		long coveredBeacons = sensors.stream()
				.map(Sensor::closestBeacon)
				.filter(b -> b.y() == part1TargetY)
				.map(Point::x)
				.distinct()
				.filter(x -> ranges.stream().anyMatch(r -> r.contains(x)))
				.count();
		
		return ranges.stream().mapToInt(Range::size).sum() - coveredBeacons;
	}
	
	@Override
	public Object part2() {
		Set<Point> beacons = sensors.stream().map(Sensor::closestBeacon).collect(Collectors.toSet());
		List<Diagonal> diagonals = sensors.stream().flatMap(s -> s.unreachablePerimeterDiagonals().stream()).toList();
		return diagonals.stream()
				.flatMap(d -> diagonals.stream().map(d::intersection))
				.filter(Objects::nonNull)
				.filter(part2Region::containsInclusive)
				.filter(p -> sensors.stream().noneMatch(s -> s.covers(p)))
				.filter(p -> !beacons.contains(p))
				.distinct()
				.map(p -> p.x() * 4000000L + p.y())
				.findFirst()
				.orElse(0L);
	}
	
	record Range(int from, int to) {
		
		boolean contains(int value) {
			return from <= value && value <= to;
		}
		
		int size() {
			return to - from + 1;
		}
		
		static List<Range> combine(List<Range> ranges) {
			if (ranges.isEmpty())
				return List.of();
			
			ranges = new ArrayList<>(ranges);
			List<Range> nextRanges = new ArrayList<>();
			Range prev = ranges.removeFirst();
			while (!ranges.isEmpty()) {
				Range current = ranges.removeFirst();
				if (current.from <= prev.to) {
					prev = new Range(prev.from, Math.max(prev.to, current.to));
				} else {
					nextRanges.add(prev);
					prev = current;
				}
			}
			nextRanges.add(prev);
			return nextRanges;
		}
	}
	
	record Diagonal(Point from, Point to, boolean positive) {
		
		Point intersection(Diagonal other) {
			if (positive == other.positive)
				return null;
			if (!positive)
				return other.intersection(this);
			
			int negativeDiagSum = other.from().x() + other.from().y();
			int positiveDiagSum = from().x() + from().y();
			if (negativeDiagSum - positiveDiagSum % 2 == 0)
				return null;
			int delta = (negativeDiagSum - positiveDiagSum) / 2;
			return from.shift(delta, delta);
		}
		
		static Diagonal of(Point from, Point to) {
			if (to.x() < from.x()) {
				Point temp = from;
				from = to;
				to = temp;
			}
			return new Diagonal(from, to, from.x() < to.x() == from.y() < to.y());
		}
	}
	
	record Sensor(Point location, Point closestBeacon, int distanceToBeacon) {
		
		Sensor(Point location, Point closestBeacon) {
			this(location, closestBeacon, location.distanceTo(closestBeacon));
		}
		
		int distanceToRow(int y) {
			return Math.abs(location.y() - y);
		}
		
		boolean covers(Point p) {
			return location.distanceTo(p) <= distanceToBeacon;
		}
		
		Range rowCoverRange(int y) {
			int delta = distanceToBeacon - distanceToRow(y);
			return delta <= 0 ? null : new Range(location.x() - delta, location.x() + delta);
		}
		
		List<Diagonal> unreachablePerimeterDiagonals() {
			Point top = location.move(Direction.UP, distanceToBeacon + 1);
			Point bot = location.move(Direction.DOWN, distanceToBeacon + 1);
			Point left = location.move(Direction.LEFT, distanceToBeacon + 1);
			Point right = location.move(Direction.RIGHT, distanceToBeacon + 1);
			return List.of(Diagonal.of(top, right), Diagonal.of(right, bot), Diagonal.of(bot, left), Diagonal.of(left, top));
		}
		
		static final Pattern pattern = Pattern.compile("Sensor at x=(.+), y=(.+): closest beacon is at x=(.+), y=(.+)");
		
		static Sensor parse(String line) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.find())
				throw new RuntimeException("Invalid sensor: " + line);
			int x = Integer.parseInt(matcher.group(1));
			int y = Integer.parseInt(matcher.group(2));
			int bx = Integer.parseInt(matcher.group(3));
			int by = Integer.parseInt(matcher.group(4));
			Point beacon = new Point(bx, by);
			return new Sensor(new Point(x, y), beacon);
		}
	}
}