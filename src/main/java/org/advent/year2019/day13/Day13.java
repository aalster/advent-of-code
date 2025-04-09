package org.advent.year2019.day13;

import lombok.Data;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day13 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day13()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 286, 14538)
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
		return play().objects().values().stream().filter(v -> v == 2).count();
	}
	
	@Override
	public Object part2() {
		computer.set(0, 0, 2);
		return play().score();
	}
	
	private GameState play() {
		Map<Point, Integer> objects = new HashMap<>();
		int paddleX = 0;
		int ballX = 0;
		int score = 0;
		
		PaddleInput inputProvider = new PaddleInput();
		while (computer.getState() != IntcodeComputer.State.HALTED) {
			Long x = computer.runUntilOutput(inputProvider);
			Long y = computer.runUntilOutput(inputProvider);
			Long type = computer.runUntilOutput(inputProvider);
			if (x == null || y == null || type == null)
				break;
			Point position = new Point(Math.toIntExact(x), Math.toIntExact(y));
			if (position.equals(new Point(-1, 0))) {
				score = Math.toIntExact(type);
			} else {
				objects.put(position, Math.toIntExact(type));
				if (type == 3 || type == 4) {
					if (type == 3)
						paddleX = position.x();
					if (type == 4)
						ballX = position.x();
					inputProvider.setDirection(Integer.compare(ballX, paddleX));
//					new GameState(objects, score).print();
				}
			}
		}
		return new GameState(objects, score);
	}
	
	record GameState(Map<Point, Integer> objects, int score) {
		void print() {
			System.out.println("\nScore: " + score);
			Point.printField(objects.keySet(), p -> switch (objects.getOrDefault(p, -1)) {
				case 1 -> '#';
				case 2 -> 'b';
				case 3 -> '_';
				case 4 -> 'O';
				default -> '.';
			});
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@Data
	static class PaddleInput implements InputProvider {
		int direction;
		
		@Override
		public boolean hasNext() {
			return true;
		}
		
		@Override
		public long nextInput() {
			return direction;
		}
	}
}