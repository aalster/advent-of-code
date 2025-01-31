package org.advent.year2019.day3;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Day3 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day3()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 6, 30),
				new ExpectedAnswers("example2.txt", 159, 610),
				new ExpectedAnswers("example3.txt", 135, 410),
				new ExpectedAnswers("input.txt", 352, 43848)
		);
	}
	
	List<Line> leftWire;
	List<Line> rightWire;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		leftWire = Line.parse(input.nextLine());
		rightWire = Line.parse(input.nextLine());
	}
	
	@Override
	public Object part1() {
		return leftWire.stream()
				.flatMap(left -> rightWire.stream().map(left::intersection))
				.filter(Objects::nonNull)
				.filter(l -> l.common != 0 || l.from != 0 || l.to != 0)
				.mapToInt(Line::distanceToZeroPoint)
				.min()
				.orElse(0);
	}
	
	@Override
	public Object part2() {
		int minSteps = Integer.MAX_VALUE;
		int leftSteps = 0;
		for (Line left : leftWire) {
			int rightSteps = 0;
			for (Line right : rightWire) {
				Line intersection = left.intersection(right);
				if (intersection != null) {
					Point intersectionPoint = intersection.startingPoint();
					if (!Point.ZERO.equals(intersectionPoint)) {
						int steps = leftSteps + left.stepsTo(intersectionPoint) + rightSteps + right.stepsTo(intersectionPoint);
						minSteps = Math.min(minSteps, steps);
					}
				}
				rightSteps += right.length();
				if (minSteps < leftSteps + rightSteps)
					break;
			}
			leftSteps += left.length();
			if (minSteps < leftSteps)
				break;
		}
		return minSteps;
	}
	
	record Line(int common, int from, int to, Direction direction) {
		
		int min() {
			return Math.min(from, to);
		}
		
		int max() {
			return Math.max(from, to);
		}
		
		int length() {
			return Math.abs(from - to);
		}
		
		int distanceToZeroPoint() {
			return Math.abs(common) + ((Integer.signum(from) != Integer.signum(to) ? 0 : Math.min(Math.abs(from), Math.abs(to))));
		}
		
		Point startingPoint() {
			return switch (direction) {
				case UP, DOWN -> new Point(common, from);
				case LEFT, RIGHT -> new Point(from, common);
			};
		}
		
		int stepsTo(Point point) {
			return startingPoint().distanceTo(point);
		}
		
		Line intersection(Line other) {
			if (direction == other.direction || direction == other.direction.reverse()) {
				if (common != other.common || to < other.min() || other.max() < from)
					return null;
				return new Line(common, Math.max(from, other.min()), Math.min(to, other.max()), direction);
			}
			Line ver = direction.isVertical() ? this : other;
			Line hor = direction.isVertical() ? other : this;
			if (ver.common < hor.min() || hor.max() < ver.common)
				return null;
			if (hor.common < ver.min() || ver.max() < hor.common)
				return null;
			Point intersection = new Point(ver.common, hor.common);
			return Line.of(intersection, intersection, Direction.RIGHT);
		}
		
		static Line of(Point start, Point end, Direction direction) {
			if (direction.isVertical())
				return new Line(start.x(), start.y(), end.y(), direction);
			else
				return new Line(start.y(), start.x(), end.x(), direction);
		}
		
		static List<Line> parse(String line) {
			List<Line> lines = new ArrayList<>();
			Point current = Point.ZERO;
			for (String step : line.split(",")) {
				Direction direction = Direction.parseLetter(step.charAt(0));
				Point next = current.move(direction, Integer.parseInt(step.substring(1)));
				lines.add(Line.of(current, next, direction));
				current = next;
			}
			return lines;
		}
	}
}