package org.advent.year2019.day17;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day17 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day17()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 4408, 862452)
		);
	}
	
	IntcodeComputer computer;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		computer = IntcodeComputer.parse(input.nextLine());
	}
	
	@Override
	public Object part1() {
		return findIntersections(readField(computer)).stream().mapToInt(p -> p.x() * p.y()).sum();
	}
	
	@Override
	public Object part2() {
		List<PathInstruction> pathInstructions = pathInstructions(readField(computer.copy()));
		List<String> functions = PathInstruction.extractFunctions(pathInstructions).stream().map(PathInstruction::toString).toList();
		String input = PathInstruction.toString(pathInstructions) + "\n" + String.join("\n", functions) + "\nn\n";
		return walk(computer, input);
	}
	
	Field readField(IntcodeComputer computer) {
		StringBuilder output = new StringBuilder(1000);
		while (computer.getState() != IntcodeComputer.State.HALTED) {
			Long type = computer.runUntilOutput(InputProvider.constant(0));
			if (type == null)
				break;
			output.append((char) (long) type);
		}
//		System.out.println(output);
		Map<Character, List<Point>> field = Point.readField(List.of(output.toString().split("\n")));
		Character robotSymbol = Stream.of('<', '>', '^', 'v').filter(field::containsKey).findAny().orElseThrow();
		return new Field(field.get(robotSymbol).getFirst(), Direction.parseSymbol(robotSymbol), new HashSet<>(field.get('#')));
	}
	
	long walk(IntcodeComputer computer, String logic) {
		computer.set(0, 0, 2);
		InputProvider input = new StringInputProvider(logic);
		long lastOutput = 0;
		while (computer.getState() != IntcodeComputer.State.HALTED) {
			Long output = computer.runUntilOutput(input);
			if (output == null)
				break;
			lastOutput = output;
//			if (output < 256)
//				System.out.print((char) (long) output);
		}
		return lastOutput;
	}
	
	Set<Point> findIntersections(Field field) {
		Point robot = field.robot;
		Direction direction = field.direction;
		Set<Point> scaffolds = field.scaffolds;
		
		Set<Point> intersections = new HashSet<>();
		while (true) {
			Direction turn = turnToScaffold(scaffolds, robot, direction);
			if (turn == null)
				break;
			direction = direction.rotate(turn);
			while (true) {
				Point next = robot.shift(direction);
				if (!scaffolds.contains(next))
					break;
				robot = next;
				if (scaffolds.contains(robot.shift(direction.rotate(Direction.LEFT))) &&
						scaffolds.contains(robot.shift(direction.rotate(Direction.RIGHT))))
					intersections.add(robot);
			}
		}
		return intersections;
	}
	
	Direction turnToScaffold(Set<Point> scaffolds, Point robot, Direction direction) {
		return Stream.of(Direction.LEFT, Direction.RIGHT)
				.filter(d -> scaffolds.contains(direction.rotate(d).shift(robot)))
				.findAny()
				.orElse(null);
	}
	
	List<PathInstruction> pathInstructions(Field field) {
		Point robot = field.robot;
		Direction direction = field.direction;
		Set<Point> scaffolds = field.scaffolds;
		
		List<PathInstruction> pathInstructions = new ArrayList<>();
		while (true) {
			Direction turn = turnToScaffold(scaffolds, robot, direction);
			if (turn == null)
				break;
			direction = direction.rotate(turn);
			
			int distance = 0;
			while (true) {
				Point next = robot.shift(direction);
				if (!scaffolds.contains(next))
					break;
				robot = next;
				distance++;
			}
			pathInstructions.add(new PathInstruction(turn.presentationLetter(), distance));
		}
		return pathInstructions;
	}
	
	record Field(Point robot, Direction direction, Set<Point> scaffolds) {
	}
	
	record PathInstruction(String name, int distance) {
		@Override
		public String toString() {
			return name + (distance > 0 ? "," + distance : "");
		}
		
		static String toString(List<PathInstruction> pathInstructions) {
			return pathInstructions.stream().map(PathInstruction::toString).collect(Collectors.joining(","));
		}
		
		// Переделанный вариант отсюда
		// https://gitlab.com/krystian.slesik/advent-of-code-2019/blob/master/src/main/java/pl/kslesik/adventofcode/Day17.java
		static List<List<PathInstruction>> extractFunctions(List<PathInstruction> pathInstructions) {
			List<List<PathInstruction>> functions = new LinkedList<>();
			while (true) {
				Range range = PathInstruction.findFirstNotReplacedRange(pathInstructions);
				if (range == null)
					break;
				
				List<PathInstruction> function = IntStream.rangeClosed(2, range.to - range.from)
						.mapToObj(i -> pathInstructions.subList(range.from, range.from + i))
						.filter(s -> countOccurrences(pathInstructions, s) > 1)
						.max(Comparator.comparing((List<PathInstruction> sub) -> sub.size()).thenComparing(s -> countOccurrences(pathInstructions, s)))
						.map(ArrayList::new)
						.orElseThrow();
				
				replaceFunction(pathInstructions, function, "" + (char) ('A' + functions.size()));
				functions.add(function);
			}
			return functions;
		}
		
		static void replaceFunction(List<PathInstruction> pathInstructions, List<PathInstruction> function, String name) {
			PathInstruction replacement = new PathInstruction(name, 0);
			int index;
			while ((index = Collections.indexOfSubList(pathInstructions, function)) >= 0) {
				for (int i = 0; i < function.size() - 1; i++)
					pathInstructions.remove(index);
				pathInstructions.set(index, replacement);
			}
		}
		
		static int countOccurrences(List<?> tokens, List<?> pattern) {
			int count = 0;
			for (int i = 0; i <= tokens.size() - pattern.size(); i++) {
				if (tokens.subList(i, i + pattern.size()).equals(pattern)) {
					count++;
					i += pattern.size() - 1;
				}
			}
			return count;
		}
		
		static Range findFirstNotReplacedRange(List<PathInstruction> pathInstructions) {
			int from = -1;
			int index = 0;
			for (PathInstruction pathInstruction : pathInstructions) {
				if (from < 0) {
					if (pathInstruction.distance > 0)
						from = index;
				} else {
					if (pathInstruction.distance == 0)
						return new Range(from, index);
				}
				index++;
			}
			if (from < 0)
				return null;
			return new Range(from, pathInstructions.size());
		}
	}
	
	record Range(int from, int to) {
	}
	
	static class StringInputProvider implements InputProvider {
		final char[] input;
		int index = 0;
		
		StringInputProvider(String input) {
			this.input = input.toCharArray();
		}
		
		@Override
		public boolean hasNext() {
			return index < input.length;
		}
		
		@Override
		public long nextInput() {
			return input[index++];
		}
	}
}