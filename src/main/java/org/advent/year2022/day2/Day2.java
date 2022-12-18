package org.advent.year2022.day2;

import org.advent.common.Utils;

import java.util.Scanner;

public class Day2 {
	
	public static void main1(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day2.class,"input.txt");
		int score = 0;
		while (input.hasNext()) {
			String[] line = input.nextLine().split(" ");
			Shape opponent = Shape.parse(line[0]);
			Shape me = Shape.parse(line[1]);
			score += me.roundOutcome(opponent).score() + me.score();
		}
		System.out.println(score);
	}
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day2.class,"input.txt");
		int score = 0;
		while (input.hasNext()) {
			String[] line = input.nextLine().split(" ");
			Shape opponent = Shape.parse(line[0]);
			Outcome outcome = Outcome.parse(line[1]);
			score += outcome.score() + outcome.against(opponent).score();
		}
		System.out.println(score);
	}
	
	enum Shape {
		ROCK, PAPER, SCISSORS;
		
		public Outcome roundOutcome(Shape shape) {
			int win = (score() - shape.score() + 3) % 3;
			return switch (win) {
				case 0 -> Outcome.DRAW;
				case 1 -> Outcome.WIN;
				default -> Outcome.LOOSE;
			};
		}
		
		public int score() {
			return ordinal() + 1;
		}
		
		public static Shape parse(String name) {
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
		
		public Shape against(Shape shape) {
			throw new UnsupportedOperationException();
		}
		
		public int score() {
			return ordinal() * 3;
		}
		
		public static Outcome parse(String name) {
			return switch (name) {
				case "X" -> Outcome.LOOSE;
				case "Y" -> Outcome.DRAW;
				default -> Outcome.WIN;
			};
		}
	}
}