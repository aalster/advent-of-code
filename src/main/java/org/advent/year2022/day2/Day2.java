package org.advent.year2022.day2;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day2 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day2()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 15, 12),
				new ExpectedAnswers("input.txt", 11873, 12014)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
	}
	
	@Override
	public Object part1() {
		long score = 0;
		for (String line : lines) {
			String[] split = line.split(" ");
			Shape opponent = Shape.parse(split[0]);
			Shape me = Shape.parse(split[1]);
			score += me.roundOutcome(opponent).score() + me.score();
		}
		return score;
	}
	
	@Override
	public Object part2() {
		long score = 0;
		for (String line : lines) {
			String[] split = line.split(" ");
			Shape opponent = Shape.parse(split[0]);
			Outcome outcome = Outcome.parse(split[1]);
			score += outcome.score() + outcome.against(opponent).score();
		}
		return score;
	}
	
	enum Shape {
		ROCK, PAPER, SCISSORS;
		
		Outcome roundOutcome(Shape shape) {
			int win = (score() - shape.score() + 3) % 3;
			return switch (win) {
				case 0 -> Outcome.DRAW;
				case 1 -> Outcome.WIN;
				default -> Outcome.LOOSE;
			};
		}
		
		int score() {
			return ordinal() + 1;
		}
		
		static Shape parse(String name) {
			return switch (name) {
				case "A", "X" -> ROCK;
				case "B", "Y" -> PAPER;
				default -> SCISSORS;
			};
		}
	}
	
	enum Outcome {
		LOOSE {
			@Override
			public Shape against(Shape shape) {
				return switch (shape) {
					case ROCK -> Shape.SCISSORS;
					case PAPER -> Shape.ROCK;
					case SCISSORS -> Shape.PAPER;
				};
			}
		},
		DRAW {
			@Override
			public Shape against(Shape shape) {
				return shape;
			}
		},
		WIN {
			@Override
			public Shape against(Shape shape) {
				return switch (shape) {
					case ROCK -> Shape.PAPER;
					case PAPER -> Shape.SCISSORS;
					case SCISSORS -> Shape.ROCK;
				};
			}
		};
		
		Shape against(Shape shape) {
			throw new UnsupportedOperationException();
		}
		
		int score() {
			return ordinal() * 3;
		}
		
		static Outcome parse(String name) {
			return switch (name) {
				case "X" -> Outcome.LOOSE;
				case "Y" -> Outcome.DRAW;
				default -> Outcome.WIN;
			};
		}
	}
}