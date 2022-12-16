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
	static final int example1Y = 10;
	static final int target1Y = 2000000;
	static final int example2MaxX = 20;
	static final int example2MaxY = 20;
	static final int target2MaxX = 4000000;
	static final int target2MaxY = 4000000;
	
	public static void main(String[] args) {
		Data example = Data.parse(Utils.scanFileNearClass(Puzzle15.class, "example.txt"));
		Data input = Data.parse(Utils.scanFileNearClass(Puzzle15.class, "input.txt"));
		System.out.println("Example 1: " + solve1(example, example1Y));
		System.out.println("Answer 1: " + solve1(input, target1Y));
		printField(example, example2MaxX, example2MaxY);
		System.out.println("Example 2: " + solve2(example, example2MaxX, example2MaxY));
//		System.out.println("Answer 2: " + solve2(input, target2MaxX, target2MaxY));
		System.out.println("Answer 2: " + solve2PerimeterLogic(input, target2MaxX, target2MaxY));
	}
	
	static long solve1(Data data, int targetY) {
		RangeList rangeList = new RangeList(data.sensors().stream()
				.map(s -> s.rowCoverRange(targetY))
				.filter(Objects::nonNull)
				.toList());
		
		Set<Integer> targetBeacons = data.beacons().stream()
				.filter(b -> b.y() == targetY)
				.mapToInt(Point::x)
				.boxed()
				.collect(Collectors.toSet());
		return IntStream.rangeClosed(rangeList.min(), rangeList.max())
				.filter(rangeList::contains)
				.filter(x -> !targetBeacons.contains(x))
				.count();
	}
	
	static long solve2(Data data, int targetMaxX, int targetMaxY) {
		for (int y = 0; y <= targetMaxY; y++) {
			if (y % 100 == 0)
				System.out.println("row " + y);
			for (int x = 0; x <= targetMaxX; x++) {
				Point p = new Point(x, y);
				if (data.beacons().contains(p))
					continue;
				
				boolean covers = false;
				for (Sensor sensor : data.sensors()) {
					if (sensor.covers(p)) {
						covers = true;
						break;
					}
				}
				if (!covers)
					return (long) x * target2MaxX + y;
			}
		}
		return 0;
	}
	
	static long solve2PerimeterLogic(Data data, int targetMaxX, int targetMaxY) {
		Set<Point> unreachablePoints = data.sensors().parallelStream()
				.flatMap(s -> s.unreachablePerimeter().stream())
				.filter(p -> 0 <= p.x && p.x <= targetMaxX)
				.filter(p -> 0 <= p.y && p.y <= targetMaxY)
				.filter(p -> data.sensors().stream().noneMatch(s -> s.covers(p)))
				.filter(p -> !data.beacons().contains(p))
				.collect(Collectors.toSet());
		System.out.println(unreachablePoints);
		if (!unreachablePoints.isEmpty()) {
			Point point = unreachablePoints.iterator().next();
			return (long) point.x * target2MaxX + point.y;
		}
		return 0;
	}
	
	static void printField(Data data, int maxX, int maxY) {
		for (int y = 0; y <= maxY; y++) {
			for (int x = 0; x <= maxX; x++) {
				Point p = new Point(x, y);
				if (data.beacons().contains(p)) {
					System.out.print('B');
					continue;
				}
				if (data.sensors().stream().map(Sensor::location).anyMatch(p::equals)) {
					System.out.print('S');
					continue;
				}
				System.out.print(data.sensors().stream().anyMatch(s -> s.covers(p)) ? '#' : ' ');
			}
			System.out.println();
		}
	}
	
	record Data(List<Sensor> sensors, Set<Point> beacons) {
		static Data parse(Scanner input) {
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
			return new Data(sensors, beacons);
		}
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
			return Math.abs(location.y - y);
		}
		boolean covers(Point p) {
			return location.distanceTo(p) <= distanceToBeacon();
		}
		Range rowCoverRange(int y) {
			int delta = distanceToBeacon() - distanceToRow(y);
			return delta <= 0 ? null : new Range(location.x - delta, location.x + delta);
		}
		List<Point> unreachablePerimeter() {
			int fromX = location.x;
			int toX = location.x + distanceToBeacon + 1;
			int fromY = location.y - distanceToBeacon - 1;
			int toY = location.y;
			int x = fromX;
			int y = fromY;
			List<Point> points = new ArrayList<>();
			while (x <= toX && y <= toY) {
				points.add(new Point(x, y));
				points.add(new Point(2 * location.x - x, y));
				points.add(new Point(x, 2 * location.y - y));
				points.add(new Point(2 * location.x - x, 2 * location.y - y));
				x++;
				y++;
			}
			System.out.println("perimeter: " + points.size());
			return points;
		}
	}
	
	record Point(int x, int y) {
		int distanceTo(Point p) {
			return Math.abs(x - p.x) + Math.abs(y - p.y);
		}
	}
}