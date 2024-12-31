package org.advent.year2022.day15;

import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
	Set<Point> beacons;
	int part1TargetY;
	int part2TargetMax;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		Pattern pattern = Pattern.compile("Sensor at x=(.+), y=(.+): closest beacon is at x=(.+), y=(.+)");
		sensors = new ArrayList<>();
		beacons = new HashSet<>();
		while (input.hasNext()) {
			Matcher matcher = pattern.matcher(input.nextLine());
			if (!matcher.find())
				continue;
			int x = Integer.parseInt(matcher.group(1));
			int y = Integer.parseInt(matcher.group(2));
			int bx = Integer.parseInt(matcher.group(3));
			int by = Integer.parseInt(matcher.group(4));
			Point beacon = new Point(bx, by);
			beacons.add(beacon);
			sensors.add(new Sensor(new Point(x, y), beacon));
		}
		
		part1TargetY = switch (file) {
			case "example.txt" -> 10;
			case "input.txt" -> 2000000;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
		part2TargetMax = switch (file) {
			case "example.txt" -> 20;
			case "input.txt" -> 4000000;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		RangeList rangeList = new RangeList(sensors.stream()
				.map(s -> s.rowCoverRange(part1TargetY))
				.filter(Objects::nonNull)
				.toList());
		
		Set<Integer> targetBeacons = beacons.stream()
				.filter(b -> b.y() == part1TargetY)
				.mapToInt(Point::x)
				.boxed()
				.collect(Collectors.toSet());
		return IntStream.rangeClosed(rangeList.min(), rangeList.max())
				.filter(rangeList::contains)
				.filter(x -> !targetBeacons.contains(x))
				.count();
	}
	
	@Override
	public Object part2() {
		Set<Point> unreachablePoints = sensors.parallelStream()
				.flatMap(s -> s.unreachablePerimeter().stream())
				.filter(p -> 0 <= p.x() && p.x() <= part2TargetMax)
				.filter(p -> 0 <= p.y() && p.y() <= part2TargetMax)
				.filter(p -> sensors.stream().noneMatch(s -> s.covers(p)))
				.filter(p -> !beacons.contains(p))
				.collect(Collectors.toSet());
		if (!unreachablePoints.isEmpty()) {
			Point point = unreachablePoints.iterator().next();
			return (long) point.x() * 4000000 + point.y();
		}
		return 0;
	}
	
	record RangeList(List<Range> ranges) {
		boolean contains(int value) {
			return ranges.stream().anyMatch(r -> r.contains(value));
		}
		int min() {
			return ranges.stream().mapToInt(Range::from).min().orElse(0);
		}
		int max() {
			return ranges.stream().mapToInt(Range::to).max().orElse(0);
		}
	}
	
	record Range(int from, int to) {
		boolean contains(int value) {
			return from <= value && value <= to;
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
			return location.distanceTo(p) <= distanceToBeacon();
		}
		Range rowCoverRange(int y) {
			int delta = distanceToBeacon() - distanceToRow(y);
			return delta <= 0 ? null : new Range(location.x() - delta, location.x() + delta);
		}
		List<Point> unreachablePerimeter() {
			int fromX = location.x();
			int toX = location.x() + distanceToBeacon + 1;
			int fromY = location.y() - distanceToBeacon - 1;
			int toY = location.y();
			int x = fromX;
			int y = fromY;
			List<Point> points = new ArrayList<>();
			while (x <= toX && y <= toY) {
				points.add(new Point(x, y));
				points.add(new Point(2 * location.x() - x, y));
				points.add(new Point(x, 2 * location.y() - y));
				points.add(new Point(2 * location.x() - x, 2 * location.y() - y));
				x++;
				y++;
			}
			return points;
		}
	}
}