package org.advent.year2022.day24;

import org.advent.common.Direction;
import org.advent.common.FieldBounds;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day24()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 18, 54),
				new ExpectedAnswers("input.txt", 314, 896)
		);
	}
	
	Map<Direction, Set<Point>> blizzards;
	Point start;
	Point exit;
	FieldBounds bounds;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		Set<Point> field = new HashSet<>();
		blizzards = Direction.stream().collect(Collectors.toMap(d -> d, d -> new HashSet<>()));
		int y = 0;
		while (input.hasNext()) {
			char[] charArray = input.nextLine().toCharArray();
			for (int x = 0; x < charArray.length; x++) {
				char c = charArray[x];
				if (c == '#')
					continue;
				Point point = new Point(x, y);
				field.add(point);
				if (c != '.')
					blizzards.get(Direction.parseSymbol(c)).add(point);
			}
			y++;
		}
		
		start = field.stream().reduce((l, r) -> l.y() < r.y() ? l : r).orElseThrow();
		exit = field.stream().reduce((l, r) -> l.y() > r.y() ? l : r).orElseThrow();
		bounds = FieldBounds.ofField(field);
	}
	
	@Override
	public Object part1() {
		return trip(new State(0, blizzards, Set.of(start)), exit, bounds).steps();
	}
	
	@Override
	public Object part2() {
		State trip = trip(new State(0, blizzards, Set.of(start)), exit, bounds);
		trip = trip(trip.resetBranches(Set.of(exit)), start, bounds);
		trip = trip(trip.resetBranches(Set.of(start)), exit, bounds);
		return trip.steps();
	}
	
	State trip(State state, Point target, FieldBounds bounds) {
		while (!state.branches.contains(target))
			state = state.next(bounds);
		return state;
	}
	
	record State(int steps, Map<Direction, Set<Point>> blizzards, Set<Point> branches) {
		State next(FieldBounds bounds) {
			Map<Direction, Set<Point>> nextBlizzards = Direction.stream().collect(Collectors.toMap(
					direction -> direction,
					direction -> blizzards.get(direction).stream().map(b -> bounds.moveWrappingAround(b, direction)).collect(Collectors.toSet())
			));
			
			Set<Point> allBlizzards = nextBlizzards.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
			
			Set<Point> nextBranches = branches.stream()
					.flatMap(p -> Stream.concat(Stream.of(p), Direction.stream().map(p::move)))
					.filter(bounds::contains)
					.filter(p -> !allBlizzards.contains(p))
					.collect(Collectors.toSet());
			
			return new State(steps + 1, nextBlizzards, nextBranches);
		}
		
		State resetBranches(Set<Point> branches) {
			return new State(steps, blizzards, branches);
		}
	}
}