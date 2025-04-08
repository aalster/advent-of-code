package org.advent.year2019.day11;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.common.ascii.AsciiLetters;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day11 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day11()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 2141, "RPJCFZKF")
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
		return draw(false).size();
	}
	
	@Override
	public Object part2() {
		List<Point> whitePanels = draw(true).entrySet().stream()
				.filter(Map.Entry::getValue)
				.map(Map.Entry::getKey)
				.toList();
		return AsciiLetters.parse(whitePanels);
	}
	
	private Map<Point, Boolean> draw(boolean initialColorWhite) {
		Point position = Point.ZERO;
		Direction direction = Direction.UP;
		Map<Point, Boolean> panels = new HashMap<>();
		panels.put(position, initialColorWhite);
		while (computer.getState() != IntcodeComputer.State.HALTED) {
			InputProvider input = InputProvider.constant(panels.getOrDefault(position, false) ? 1 : 0);
			Long color = computer.runUntilOutput(input);
			Long turn = computer.runUntilOutput(input);
			if (color == null || turn == null)
				break;
			panels.put(position, color == 1);
			direction = direction.rotate(turn == 0 ? Direction.LEFT : Direction.RIGHT);
			position = position.shift(direction);
		}
		return panels;
	}
}