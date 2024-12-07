package org.advent.year2021.day21;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.advent.common.Utils;

import java.util.List;
import java.util.Scanner;

public class Day21 {
	static final int totalPositions = 10;
	static final int winningScore = 1000;
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day21.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2());
	}
	
	private static int part1(List<String> lines) {
		List<Player> players = lines.stream().map(Player::parse).toList();
		DeterministicDice dice = new DeterministicDice(100);
		main: while (true) {
			for (Player player : players) {
				player.move(dice);
				if (player.score >= winningScore)
					break main;
			}
		}
		return dice.rolls * players.stream().mapToInt(p -> p.score).filter(s -> s < winningScore).sum();
	}
	
	private static long part2() {
		return 0;
	}
	
	@AllArgsConstructor
	static class Player {
		int number;
		int position;
		int score;
		
		void move(DeterministicDice dice) {
			position = (position + dice.roll() + dice.roll() + dice.roll()) % totalPositions;
			score += position + 1;
		}
		
		static Player parse(String line) {
			String[] split = line.replace("Player ", "").split(" starting position: ");
			return new Player(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, 0);
		}
	}
	
	@Data
	static class DeterministicDice {
		final int sides;
		int current = -1;
		int rolls = 0;
		
		int roll() {
			rolls++;
			current = (current + 1) % sides;
			return current + 1;
		}
	}
}