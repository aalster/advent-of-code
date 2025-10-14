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
import org.advent.year2019.intcode_computer.OutputConsumer;

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
	
	long[] program;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		program = IntcodeComputer.parseProgram(input.nextLine());
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
		new IntcodeComputer(program, grid, grid).run();
		return grid;
	}
	
	
	static class Grid extends OutputConsumer.BufferingOutputConsumer implements InputProvider {
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
		public void accept(long output) {
			super.accept(output);
			if (buffer.size() >= 2) {
				draw(readNext() == 1);
				turn(readNext() == 0 ? Direction.LEFT : Direction.RIGHT);
			}
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