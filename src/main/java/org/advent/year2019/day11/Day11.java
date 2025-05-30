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

import java.util.Collection;
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
		return draw(false).panels.size();
	}
	
	@Override
	public Object part2() {
		return AsciiLetters.parse(draw(true).whitePanels());
	}
	
	private Grid draw(boolean initialColorWhite) {
		Grid grid = new Grid();
		grid.draw(initialColorWhite);
		
		while (true) {
			Long color = computer.runUntilOutput(grid);
			Long turn = computer.runUntilOutput(grid);
			if (computer.getState() == IntcodeComputer.State.HALTED)
				break;
			
			grid.draw(color == 1);
			grid.turn(turn == 0 ? Direction.LEFT : Direction.RIGHT);
		}
		return grid;
	}
	
	
	static class Grid implements InputProvider {
		Map<Point, Boolean> panels = new HashMap<>();
		Point position = Point.ZERO;
		Direction direction = Direction.UP;
		
		void draw(boolean white) {
			panels.put(position, white);
		}
		
		void turn(Direction turn) {
			direction = direction.rotate(turn);
			position = position.shift(direction);
		}
		
		Collection<Point> whitePanels() {
			return panels.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();
		}
		
		@Override
		public boolean hasNext() {
			return true;
		}
		
		@Override
		public long nextInput() {
			return panels.getOrDefault(position, false) ? 1 : 0;
		}
	}
}