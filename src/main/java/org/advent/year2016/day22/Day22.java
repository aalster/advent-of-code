package org.advent.year2016.day22;

import org.advent.common.MazeUtils;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day22 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day22()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", ExpectedAnswers.IGNORE, 7),
				new ExpectedAnswers("input.txt", 941, 249)
		);
	}
	
	List<Node> nodes;
	int movableSize;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		nodes = Utils.readLines(input).stream().map(Node::parse).filter(Objects::nonNull).toList();
		movableSize = switch (file) {
			case "example.txt" -> 20;
			case "input.txt" -> 110;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		return nodes.stream().mapToLong(n -> nodes.stream().filter(n::viablePair).count()).sum();
	}
	
	@Override
	public Object part2() {
		Point empty = nodes.stream().filter(n -> n.used == 0).map(Node::position).findAny().orElseThrow();
		Set<Point> movable = nodes.stream().filter(n -> n.used < movableSize).map(Node::position).collect(Collectors.toSet());
		Point current = nodes.stream().map(Node::position).filter(p -> p.y() == 0).max(Comparator.comparing(Point::x)).orElseThrow();
		Point end = new Point(0, 0);
		
		int totalSteps = 0;
		List<Point> path = new ArrayList<>(MazeUtils.findPath(current, end, movable::contains));
		while (!current.equals(end)) {
			Point next = path.removeFirst();
			Set<Point> emptyMovable = new HashSet<>(movable);
			emptyMovable.remove(current);
			totalSteps += MazeUtils.stepsMap(empty, next, emptyMovable::contains).get(next) + 1;
			empty = current;
			current = next;
		}
		return totalSteps;
	}
	
	record Node(Point position, int size, int used, int avail) {
		
		boolean viablePair(Node other) {
			return used > 0 && !position.equals(other.position()) && used <= other.avail;
		}
		
		static Pattern pattern = Pattern.compile(".+x(\\d+)-y(\\d+) +(\\d+)T +(\\d+)T +(\\d+)T +(\\d+)%");
		static Node parse(String line) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches())
				return null;
			int x = Integer.parseInt(matcher.group(1));
			int y = Integer.parseInt(matcher.group(2));
			int size = Integer.parseInt(matcher.group(3));
			int used = Integer.parseInt(matcher.group(4));
			int avail = Integer.parseInt(matcher.group(5));
			return new Node(new Point(x, y), size, used, avail);
		}
	}
}