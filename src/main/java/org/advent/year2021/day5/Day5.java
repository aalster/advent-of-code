package org.advent.year2021.day5;

import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Day5 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day5()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 5, 12),
				new ExpectedAnswers("input.txt", 7468, 22364)
		);
	}
	
	List<Line> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input).stream().map(Line::parse).toList();
	}
	
	@Override
	public Object part1() {
		return solve(lines.stream().filter(Line::horizontalOrVertical).toList());
	}
	
	@Override
	public Object part2() {
		return solve(lines);
	}
	
	private int solve(List<Line> lines) {
		Set<Point> overlappedPoints = new HashSet<>();
		for (int i = 0; i < lines.size(); i++) {
			Line line = lines.get(i);
			for (int j = i + 1; j < lines.size(); j++)
				overlappedPoints.addAll(line.overlap(lines.get(j)));
		}
		return overlappedPoints.size();
	}
	
	record Line(Point from, Point to, int a, int b, int c) {
		
		Set<Point> overlap(Line other) {
			int determinant = a() * other.b() - other.a() * b();
			
			if (determinant == 0) {
				if (contains(other.from) || contains(other.to) || other.contains(from) || other.contains(to)) {
					Point start = maxPoint(minPoint(from, to), minPoint(other.from, other.to));
					Point end = minPoint(maxPoint(from, to), maxPoint(other.from, other.to));
					return Line.of(start, end).allPoints();
				}
				return Set.of();
			}
			
			int xNumerator = other.b() * c() - b() * other.c();
			int yNumerator = a() * other.c() - other.a() * c();
			if (xNumerator % determinant != 0 || yNumerator % determinant != 0)
				return Set.of();
			
			Point overlap = new Point(xNumerator / determinant, yNumerator / determinant);
			if (inRect(overlap) && other.inRect(overlap))
				return Set.of(overlap);
			return Set.of();
		}
		
		boolean contains(Point p) {
			return inRect(p) && a * p.x() + b * p.y() == c;
		}
		
		boolean inRect(Point p) {
			return Math.min(from.x(), to.x()) <= p.x() && p.x() <= Math.max(from.x(), to.x()) &&
					Math.min(from.y(), to.y()) <= p.y() && p.y() <= Math.max(from.y(), to.y());
		}
		
		boolean horizontalOrVertical() {
			return from.x() == to.x() || from.y() == to.y();
		}
		
		Set<Point> allPoints() {
			Set<Point> result = new HashSet<>();
			Point dir = directionTo(from, to);
			Point current = from;
			while (!current.equals(to)) {
				result.add(current);
				current = current.shift(dir);
			}
			result.add(to);
			return result;
		}
		
		static Line of(Point from, Point to) {
			int a = to.y() - from.y();
			int b = from.x() - to.x();
			int c = a * from.x() + b * from.y();
			return new Line(from, to, a, b, c);
		}
		
		static Line parse(String line) {
			String[] split = line.split(" -> ");
			return Line.of(Point.parse(split[0]), Point.parse(split[1]));
		}
	}
	
	static Point minPoint(Point left, Point right) {
		if (left.x() < right.x() || (left.x() == right.x() && left.y() < right.y()))
			return left;
		return right;
	}
	
	static Point maxPoint(Point left, Point right) {
		if (left.x() > right.x() || (left.x() == right.x() && left.y() > right.y()))
			return left;
		return right;
	}
	
	static Point directionTo(Point from, Point to) {
		Point sub = to.subtract(from);
		return new Point(Integer.signum(sub.x()), Integer.signum(sub.y()));
	}
}