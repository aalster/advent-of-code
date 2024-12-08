package org.advent.year2021.day21;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.advent.common.Pair;
import org.advent.common.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day21 {
	static final int totalPositions = 10;
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day21.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static int part1(List<String> lines) {
		List<Player> players = lines.stream().map(Player::parse).toList();
		int winningScore = 1000;
		DeterministicDice dice = new DeterministicDice(100);
		main: while (true) {
			for (Player player : players) {
				player.move(dice.roll3Times());
				if (player.score >= winningScore)
					break main;
			}
		}
		return dice.rolls * players.stream().mapToInt(p -> p.score).filter(s -> s < winningScore).sum();
	}
	
	private static long part2(List<String> lines) {
		List<Player> players = lines.stream().map(Player::parse).toList();
		int winningScore = 21;
		if (players.size() != 2)
			throw new IllegalArgumentException("Wrong number of players: " + players.size());
		
		QuantumDice dice = new QuantumDice(3);
		List<Pair<GameState, Long>> gameStates = List.of(Pair.of(new GameState(players.getFirst(), players.get(1)), 1L));
		Map<Integer, Long> winsByPlayer = new HashMap<>();
		
		int move = 0;
		while (!gameStates.isEmpty()) {
			int _move = move;
			Map<GameState, Long> stateCopies = new HashMap<>();
			gameStates.stream()
					.flatMap(pair -> pair.left().next(dice, _move)
							.map(next -> Pair.of(next.left(), next.right() * pair.right())))
					.forEach(pair -> {
						GameState state = pair.left();
						long copies = pair.right();
						Integer winner = state.getWinner(winningScore);
						if (winner != null)
							winsByPlayer.compute(winner, (k, v) -> (v == null ? 0 : v) + copies);
						else
							stateCopies.compute(state, (k, v) -> (v == null ? 0 : v) + copies);
					});
			gameStates = stateCopies.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())).toList();
			move++;
		}
		return winsByPlayer.values().stream().mapToLong(l -> l).max().orElse(0);
	}
	
	@AllArgsConstructor
	static class Player {
		int number;
		int position;
		int score;
		
		void move(int diceTripleScore) {
			position = (position + diceTripleScore) % totalPositions;
			score += position + 1;
		}
		
		Player copy() {
			return new Player(number, position, score);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Player other))
				return false;
			return number == other.number && position == other.position && score == other.score;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(number, position, score);
		}
		
		static Player parse(String line) {
			String[] split = line.replace("Player ", "").split(" starting position: ");
			return new Player(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, 0);
		}
	}
	
	record GameState(Player first, Player second) {
		
		Integer getWinner(int winningScore) {
			if (first.score >= winningScore)
				return first.number;
			if (second.score >= winningScore)
				return second.number;
			return null;
		}
	
		Stream<Pair<GameState, Long>> next(QuantumDice dice, int move) {
			return dice.roll3Times()
					.map(pair -> {
						Player player = (move % 2 == 0 ? first : second).copy();
						player.move(pair.left());
						GameState nextState = move % 2 == 0 ? new GameState(player, second) : new GameState(first, player);
						return Pair.of(nextState, pair.right());
					});
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
		
		int roll3Times() {
			return roll() + roll() + roll();
		}
	}
	
	@Data
	static class QuantumDice {
		final int sides;
		List<Pair<Integer, Long>> roll3TimesCache = null;
		
		IntStream roll() {
			return IntStream.rangeClosed(1, sides);
		}
		
		// Возвращает пару с кол-вом очков и кол-вом копий
		Stream<Pair<Integer, Long>> roll3Times() {
			if (roll3TimesCache == null) {
				roll3TimesCache = roll()
						.flatMap(n -> roll().map(i -> i + n))
						.flatMap(n -> roll().map(i -> i + n))
						.boxed()
						.collect(Collectors.groupingBy(n -> n, Collectors.counting())).entrySet().stream()
						.map(e -> Pair.of(e.getKey(), e.getValue()))
						.toList();
			}
			return roll3TimesCache.stream();
		}
	}
}