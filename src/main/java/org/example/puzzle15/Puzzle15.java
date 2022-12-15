package org.example.puzzle15;

import org.example.Utils;

import java.awt.*;
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

public class Puzzle15 {
	static final int target1Y = 2000000;
	static final int target2MaxX = 4000000;
	static final int target2MaxY = 4000000;
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Puzzle15.class,"input.txt");
		Pattern pattern = Pattern.compile("Sensor at x=(.+), y=(.+): closest beacon is at x=(.+), y=(.+)");
		List<Sensor> sensors = new ArrayList<>();
		Set<Point> beacons = new HashSet<>();
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
		System.out.println("Answer 1: " + solve1(sensors, beacons));
		System.out.println("Answer 2: " + solve2(sensors, beacons));
	}
	
	static long solve1(List<Sensor> sensors, Set<Point> beacons) {
		RangeList rangeList = new RangeList(sensors.stream()
				.map(s -> s.rowCoverRange(target1Y))
				.filter(Objects::nonNull)
				.toList());
		
		Set<Integer> targetBeacons = beacons.stream()
				.filter(b -> b.y() == target1Y)
				.mapToInt(Point::x)
				.boxed()
				.collect(Collectors.toSet());
		return IntStream.rangeClosed(rangeList.min(), rangeList.max())
				.filter(rangeList::contains)
				.filter(x -> !targetBeacons.contains(x))
				.count();
	}
	
	static long solve2(List<Sensor> sensors, Set<Point> beacons) {
		for (int y = 0; y <= target2MaxY; y++) {
			if (y % 100 == 0)
				System.out.println("row " + y);
			
			final int targetY = y;
			RangeList rangeList = new RangeList(sensors.stream()
					.map(s -> s.rowCoverRange(targetY))
					.filter(Objects::nonNull)
					.toList());
			
			Set<Integer> targetBeacons = beacons.stream()
					.filter(b -> b.y() == targetY)
					.mapToInt(Point::x)
					.boxed()
					.collect(Collectors.toSet());
			int targetX = IntStream.rangeClosed(rangeList.min(), rangeList.max())
					.filter(value -> !rangeList.contains(value))
					.filter(x -> !targetBeacons.contains(x))
					.findAny()
					.orElse(-1);
			if (targetX >= 0)
				return (long) targetX * target2MaxX + targetY;
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
	
	record Sensor(Point location, Point closestBeacon) {
		int distanceToBeacon() {
			return Math.abs(location.x - closestBeacon.x) + Math.abs(location.y - closestBeacon.y);
		}
		int distanceToRow(int y) {
			return Math.abs(location.y - y);
		}
		Range rowCoverRange(int y) {
			int delta = distanceToBeacon() - distanceToRow(y);
			return delta <= 0 ? null : new Range(location.x - delta, location.x + delta);
		}
	}
	
	record Point(int x, int y) {
	
	}
}