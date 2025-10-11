package org.advent.year2019.day20;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SequencedMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day20 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day20()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 23, 26),
				new ExpectedAnswers("example2.txt", 58, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", ExpectedAnswers.IGNORE, 396),
				new ExpectedAnswers("input.txt", 600, 6666)
		);
	}
	
	Field field;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		field = Field.parse(Utils.readLines(input));
	}
	
	@Override
	public Object part1() {
		return field.findPath(false);
	}
	
	@Override
	public Object part2() {
		return field.findPath(true);
	}
	
	record RecursiveLocation(Point point, int depth) {
		RecursiveLocation shift(Direction direction) {
			return new RecursiveLocation(point.shift(direction), depth);
		}
	}
	
	record Field(Set<Point> empty, Map<Point, Point> portals, Point start, Point end) {
		
		int findPath(boolean recursive) {
			Rect bounds = Point.bounds(empty);
			bounds = new Rect(bounds.topLeft().shift(1, 1), bounds.bottomRight().shift(-1, -1));
			
			Set<RecursiveLocation> visited = new HashSet<>();
			RecursiveLocation endLocation = new RecursiveLocation(end, 0);
			Set<RecursiveLocation> current = Set.of(new RecursiveLocation(start, 0));
			int step = 0;
			while (!current.isEmpty()) {
				List<RecursiveLocation> next = new ArrayList<>(current.size());
				visited.addAll(current);
				
				for (RecursiveLocation c : current) {
					Direction.stream().map(c::shift).forEach(next::add);
					
					Point targetPortal = portals.get(c.point);
					if (targetPortal == null)
						continue;
					
					if (recursive) {
						boolean inside = bounds.containsInclusive(c.point);
						if (inside || c.depth > 0)
							next.add(new RecursiveLocation(targetPortal, c.depth + (inside ? 1 : -1)));
					} else {
						next.add(new RecursiveLocation(targetPortal, c.depth));
					}
				}
				current = next.stream()
						.filter(n -> empty.contains(n.point))
						.filter(n -> !visited.contains(n))
						.collect(Collectors.toSet());
				step++;
				if (current.contains(endLocation))
					return step;
			}
			return -1;
		}
		
		static Field parse(List<String> lines) {
			Map<Character, List<Point>> field = Point.readField(lines);
			Set<Point> empty = new HashSet<>(field.get('.'));
			
			SequencedMap<Point, Character> letters = new LinkedHashMap<>();
			field.entrySet().stream()
					.filter(e -> 'A' <= e.getKey() && e.getKey() <= 'Z')
					.forEach(e -> e.getValue().forEach(p -> letters.put(p, e.getKey())));
			
			Map<String, List<Point>> portalsByNames = new HashMap<>();
			while (!letters.isEmpty()) {
				Map.Entry<Point, Character> entry = letters.firstEntry();
				Point point = entry.getKey();
				Character letter = entry.getValue();
				letters.remove(point);
				
				List<Point> nearest = Direction.stream().map(point::shift).filter(letters::containsKey).toList();
				if (nearest.size() != 1)
					throw new RuntimeException("Letter without pair: " + letter + " " + point);
				
				Point pairPoint = nearest.getFirst();
				Character pairLetter = letters.remove(pairPoint);
				String name = Point.COMPARATOR.compare(point, pairPoint) < 0 ? "" + letter + pairLetter : "" + pairLetter + letter;
				
				List<Point> nearestEmpty = Stream.of(point, pairPoint)
						.flatMap(p -> Direction.stream().map(p::shift))
						.filter(empty::contains)
						.toList();
				if (nearestEmpty.size() != 1)
					throw new RuntimeException("Bad portal position: " + name + " " + nearestEmpty);
				portalsByNames.computeIfAbsent(name, k -> new ArrayList<>()).add(nearestEmpty.getFirst());
			}
			
			Point start = portalsByNames.remove("AA").getFirst();
			Point end = portalsByNames.remove("ZZ").getFirst();
			Map<Point, Point> portals = new HashMap<>();
			for (List<Point> links : portalsByNames.values()) {
				portals.put(links.getFirst(), links.getLast());
				portals.put(links.getLast(), links.getFirst());
			}
			
			return new Field(empty, portals, start, end);
		}
	}
}