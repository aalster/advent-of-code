package org.advent.year2023.day2;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day2 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day2()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 8, 2286),
				new ExpectedAnswers("input.txt", 2265, 64097)
		);
	}
	
	List<Game> games;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		games = Utils.readLines(input).stream().map(Game::parse).toList();
	}
	
	@Override
	public Object part1() {
		int[] limits = new int[] {12, 13, 14};
		return games.stream().filter(game -> game.possible(limits)).mapToInt(Game::id).sum();
	}
	
	@Override
	public Object part2() {
		return games.stream()
				.mapToInt(game -> Arrays.stream(game.minPossible()).reduce(1, (l, r) -> l * r))
				.sum();
	}
	
	record Game(int id, List<Round> rounds) {
		
		boolean possible(int[] limits) {
			return rounds.stream().allMatch(r -> r.possible(limits));
		}
		
		int[] minPossible() {
			return rounds.stream().map(Round::values)
					.reduce(new int[] {0, 0, 0}, (l, r) -> new int[] {
							Math.max(l[0], r[0]),
							Math.max(l[1], r[1]),
							Math.max(l[2], r[2])
					});
		}
		
		static Game parse(String line) {
			String[] split = line.split(":");
			int id = Integer.parseInt(StringUtils.removeStart(split[0], "Game "));
			List<Round> rounds = Arrays.stream(split[1].split(";")).map(Round::parse).toList();
			return new Game(id, rounds);
		}
	}
	
	record Round(int[] values) {
		
		boolean possible(int[] limits) {
			for (int i = 0; i < limits.length; i++) {
				if (values[i] > limits[i])
					return false;
			}
			return true;
		}
		
		static Round parse(String round) {
			Map<String, Integer> values = Arrays.stream(round.split(", "))
					.map(String::trim)
					.map(v -> v.split(" "))
					.collect(Collectors.toMap(s -> s[1], s -> Integer.parseInt(s[0])));
			return new Round(new int[] {
					values.getOrDefault("red", 0),
					values.getOrDefault("green", 0),
					values.getOrDefault("blue", 0),
			});
		}
	}
}