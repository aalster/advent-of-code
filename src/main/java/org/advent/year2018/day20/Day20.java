package org.advent.year2018.day20;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day20 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day20()).runAll();
//		new DayRunner(new Day20()).run("example3.txt", 1);
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 3, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 10, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 18, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example4.txt", 23, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example5.txt", 31, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 4778, 8459)
		);
	}
	
	PathElement path;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		path = PathElement.parse(input.nextLine());
	}
	
	@Override
	public Object part1() {
		Map<Point, Integer> stepsMap = new HashMap<>();
		path.fillStepsMap(new TakenPath(stepsMap, new Point(0, 0), 0));
		return stepsMap.values().stream().mapToInt(i -> i).max().orElse(0);
	}
	
	@Override
	public Object part2() {
		Map<Point, Integer> stepsMap = new HashMap<>();
		path.fillStepsMap(new TakenPath(stepsMap, new Point(0, 0), 0));
		return stepsMap.values().stream().mapToInt(i -> i).filter(i -> i >= 1000).count();
	}
	
	static class TakenPath {
		final Map<Point, Integer> stepsMap;
		Point position;
		int steps;
		
		TakenPath(Map<Point, Integer> stepsMap, Point position, int steps) {
			this.stepsMap = stepsMap;
			this.position = position;
			this.steps = steps;
			stepsMap.putIfAbsent(position, steps);
		}
		
		void move(Direction direction) {
			position = position.shift(direction);
			steps++;
			stepsMap.putIfAbsent(position, steps);
		}
		
		TakenPath branch() {
			return new TakenPath(stepsMap, position, steps);
		}
	}
	
	static class PathIterator {
		final char[] chars;
		int index = 0;
		
		PathIterator(String path) {
			chars = path.toCharArray();
		}
		
		boolean hasNext() {
			return index < chars.length;
		}
		
		char peekNext() {
			return chars[index];
		}
		
		void indexInc() {
			index++;
		}
	}
	
	interface PathElement {
		
		void fillStepsMap(TakenPath path);
		
		static PathElement parse(String line) {
			return Sequence.parse(new PathIterator(line.substring(1, line.length() - 1)));
		}
	}
	
	record Path(String directions) implements PathElement {
		
		@Override
		public void fillStepsMap(TakenPath path) {
			for (char d : directions.toCharArray())
				path.move(Direction.parseCompassLetter(d));
		}
		
		static PathElement parse(PathIterator iterator) {
			StringBuilder directions = new StringBuilder();
			while (iterator.hasNext()) {
				char c = iterator.peekNext();
				if (!Character.isLetter(c))
					break;
				directions.append(c);
				iterator.indexInc();
			}
			return new Path(directions.toString());
		}
	}
	
	record Sequence(List<PathElement> elements) implements PathElement {
		
		@Override
		public void fillStepsMap(TakenPath path) {
			for (PathElement element : elements)
				element.fillStepsMap(path);
		}
		
		static PathElement parse(PathIterator iterator) {
			List<PathElement> elements = new ArrayList<>();
			while (iterator.hasNext()) {
				char c = iterator.peekNext();
				
				if (Character.isLetter(c))
					elements.add(Path.parse(iterator));
				else if (c == '(')
					elements.add(Branch.parse(iterator));
				else
					break;
			}
			return elements.isEmpty() ? new Path("")
					: elements.size() == 1 ? elements.getFirst() : new Sequence(elements);
		}
	}
	
	record Branch(List<PathElement> elements) implements PathElement {
		
		@Override
		public void fillStepsMap(TakenPath path) {
			for (PathElement element : elements)
				element.fillStepsMap(path.branch());
		}
		
		static PathElement parse(PathIterator iterator) {
			List<PathElement> elements = new ArrayList<>();
			iterator.indexInc();
			while (iterator.hasNext()) {
				char c = iterator.peekNext();
				if (c == ')')
					break;
				if (c == '|')
					iterator.indexInc();
				
				elements.add(Sequence.parse(iterator));
			}
			iterator.indexInc();
			return new Branch(elements);
		}
	}
}