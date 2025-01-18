package org.advent.year2017.day25;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Day25 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day25()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 3, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 3099, ExpectedAnswers.IGNORE)
		);
	}
	
	State[] states;
	int startingState;
	int steps;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		startingState = input.nextLine().split(" ")[3].charAt(0) - 'A';
		steps = Integer.parseInt(input.nextLine().split(" ")[5]);
		input.nextLine();
		states = State.parseStates(Utils.readLines(input));
	}
	
	@Override
	public Object part1() {
		int[] tape = new int[20000];
		int cursor = tape.length / 2;
		int currentState = startingState;
		for (int i = 0; i < steps; i++) {
			State state = states[currentState * 2 + tape[cursor]];
			tape[cursor] = state.value;
			cursor += state.move;
			currentState = state.nextState;
		}
		return Arrays.stream(tape).sum();
	}
	
	@Override
	public Object part2() {
		return null;
	}
	
	record State(int value, int move, int nextState) {
		
		static State parse(Iterator<String> iterator) {
			int value = Integer.parseInt(iterator.next().split(" ")[8].replace(".", ""));
			int move = "left".equals(iterator.next().split(" ")[10].replace(".", "")) ? -1 : 1;
			int nextState = iterator.next().split(" ")[8].charAt(0) - 'A';
			return new State(value, move, nextState);
		}
		
		static State[] parseStates(List<String> lines) {
			List<List<String>> lists = Utils.splitByEmptyLine(lines);
			State[] states = new State[lists.size() * 2];
			for (List<String> stateLines : lists) {
				Iterator<String> iterator = stateLines.iterator();
				char name = iterator.next().split(" ")[2].charAt(0);
				for (int i = 0; i < 2; i++) {
					int current = Integer.parseInt(iterator.next().split(" ")[7].replace(":", ""));
					states[(name - 'A') * 2 + current] = parse(iterator);
				}
			}
			return states;
		}
	}
}