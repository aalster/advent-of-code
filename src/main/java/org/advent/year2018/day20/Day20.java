package org.advent.year2018.day20;

import org.advent.common.Direction;
import org.advent.common.Pair;
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
	
	interface PathElement {
		
		void fillStepsMap(TakenPath path);
		
		static PathElement parse(String line) {
			return Sequence.parse(line, 1).left();
		}
	}
	
	record Path(String directions) implements PathElement {
		
		@Override
		public void fillStepsMap(TakenPath path) {
			for (char d : directions.toCharArray())
				path.move(Direction.parseCompassLetter(d));
		}
		
		static Pair<PathElement, Integer> parse(String line, int from) {
			StringBuilder directions = new StringBuilder();
			int index = from;
			for (; index < line.length(); index++) {
				char c = line.charAt(index);
				if (Character.isLetter(c))
					directions.append(c);
				else
					break;
			}
			return Pair.of(new Path(directions.toString()), index);
		}
	}
	
	record Sequence(List<PathElement> elements) implements PathElement {
		
		@Override
		public void fillStepsMap(TakenPath path) {
			for (PathElement element : elements)
				element.fillStepsMap(path);
		}
		
		static Pair<PathElement, Integer> parse(String line, int from) {
			List<PathElement> elements = new ArrayList<>();
			int index = from;
			for (; index < line.length(); index++) {
				char c = line.charAt(index);
				if (Character.isLetter(c)) {
					Pair<PathElement, Integer> path = Path.parse(line, index);
					elements.add(path.left());
					index = path.right() - 1;
					continue;
				}
				if (c == '(') {
					Pair<PathElement, Integer> branch = Branch.parse(line, index);
					elements.add(branch.left());
					index = branch.right() - 1;
					continue;
				}
				break;
			}
			return Pair.of(elements.size() == 1 ? elements.getFirst() : new Sequence(elements), index);
		}
	}
	
	record Branch(List<PathElement> elements) implements PathElement {
		
		@Override
		public void fillStepsMap(TakenPath path) {
			for (PathElement element : elements)
				element.fillStepsMap(path.branch());
		}
		
		static Pair<PathElement, Integer> parse(String line, int from) {
			List<PathElement> elements = new ArrayList<>();
			int index = from + 1;
			for (; index < line.length(); index++) {
				char c = line.charAt(index);
				if (c == ')')
					break;
				if (c == '|') {
					if (line.charAt(index + 1) == ')')
						elements.add(new Path(""));
					continue;
				}
				Pair<PathElement, Integer> sequence = Sequence.parse(line, index);
				elements.add(sequence.left());
				index = sequence.right() - 1;
			}
			return Pair.of(new Branch(elements), index + 1);
		}
	}
}