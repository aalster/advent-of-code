package org.advent.year2019.day17;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
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
		String pathInstructions = pathInstructions(readField(computer.copy()));
		return walk(computer, compressPathInstructions(pathInstructions));
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
		while (computer.getState() != IntcodeComputer.State.HALTED) {
			Long type = computer.runUntilOutput(input);
			if (type == null)
				break;
			if (type < 256)
				System.out.print((char) (long) type);
			else
				return type;
		}
		return 0;
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
	
	String pathInstructions(Field field) {
		Point robot = field.robot;
		Direction direction = field.direction;
		Set<Point> scaffolds = field.scaffolds;
		
		StringBuilder pathInstructions = new StringBuilder();
		while (true) {
			Direction turn = turnToScaffold(scaffolds, robot, direction);
			if (turn == null)
				break;
			direction = direction.rotate(turn);
			pathInstructions.append(turn.presentationLetter()).append(",");
			
			int distance = 0;
			while (true) {
				Point next = robot.shift(direction);
				if (!scaffolds.contains(next))
					break;
				robot = next;
				distance++;
			}
			pathInstructions.append(distance).append(",");
		}
		pathInstructions.setLength(pathInstructions.length() - 1);
		return pathInstructions.toString();
	}
	
	String compressPathInstructions(String pathInstructions) {
		System.out.println(pathInstructions);
		// R,8,L,12,R,8,R,12,L,8,R,10,R,12,L,8,R,10,R,8,L,12,R,8,R,8,L,8,L,8,R,8,R,10,R,8,L,12,R,8,R,8,L,12,R,8,R,8,L,8,L,8,R,8,R,10,R,12,L,8,R,10,R,8,L,8,L,8,R,8,R,10
		String input = """
				A,B,B,A,C,A,A,C,B,C
				R,8,L,12,R,8
				R,12,L,8,R,10
				R,8,L,8,L,8,R,8,R,10
				y
				""";
		return input;
	}
	
	record Field(Point robot, Direction direction, Set<Point> scaffolds) {
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