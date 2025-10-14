package org.advent.year2019.day13;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer;
import org.advent.year2019.intcode_computer.OutputConsumer;

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
	
	static final boolean silent = true;
	long[] program;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		program = IntcodeComputer.parseProgram(input.nextLine());
	}
	
	@Override
	public Object part1() {
		return play().objects.values().stream().filter(v -> v == 2).count();
	}
	
	@Override
	public Object part2() {
		program[0] = 2;
		return play().score;
	}
	
	GameState play() {
		GameState gameState = new GameState();
		PaddleInput inputProvider = new PaddleInput(gameState);
		GameOutputConsumer outputConsumer = new GameOutputConsumer(gameState);
		new IntcodeComputer(program, inputProvider, outputConsumer).run();
		return gameState;
	}
	
	static final class GameState {
		final Map<Point, Integer> objects = new HashMap<>();
		Point ball = Point.ZERO;
		Point paddle = Point.ZERO;
		int score = 0;
		
		void update(Point position, int type) {
			if (position.equals(new Point(-1, 0))) {
				score = type;
				return;
			}
			objects.put(position, type);
			if (type == 3)
				paddle = position;
			if (type == 4)
				ball = position;
		}
		
		@SneakyThrows
		void print() {
			System.out.println("\nScore: " + score);
			Point.printField(objects.keySet(), p -> switch (objects.getOrDefault(p, -1)) {
				case 1 -> '#';
				case 2 -> 'b';
				case 3 -> '_';
				case 4 -> 'O';
				default -> '.';
			});
			Thread.sleep(200);
		}
	}
	
	@RequiredArgsConstructor
	static class GameOutputConsumer extends OutputConsumer.BufferingOutputConsumer {
		final GameState gameState;
		
		@Override
		public void accept(long output) {
			super.accept(output);
			if (buffer.size() >= 3) {
				Point position = new Point(Math.toIntExact(readNext()), Math.toIntExact(readNext()));
				int type = Math.toIntExact(readNext());
				gameState.update(position, type);
			}
		}
	}
	
	@RequiredArgsConstructor
	static class PaddleInput implements InputProvider {
		final GameState gameState;
		
		@Override
		public boolean hasNext() {
			return true;
		}
		
		@Override
		public long nextInput() {
			if (!silent)
				gameState.print();
			return Integer.compare(gameState.ball.x(), gameState.paddle.x());
		}
	}
}