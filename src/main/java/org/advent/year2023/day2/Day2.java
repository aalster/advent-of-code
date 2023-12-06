package org.advent.year2023.day2;

import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day2 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day2.class, "input.txt");
		List<Game> games = new ArrayList<>();
		while (input.hasNext()) {
			games.add(Game.parse(input.nextLine()));
		}
		
		System.out.println("Answer 1: " + part1(games));
		System.out.println("Answer 2: " + part2(games));
	}
	
	private static int part1(List<Game> games) {
		int[] limits = new int[] {12, 13, 14};
		int result = 0;
		for (Game game : games) {
			if (game.possible(limits))
				result += game.id();
		}
		return result;
	}
	
	private static int part2(List<Game> games) {
		int result = 0;
		for (Game game : games) {
			result += Arrays.stream(game.minPossible()).reduce(1, (l, r) -> l * r);
		}
		return result;
	}
	
	private record Game(int id, List<Round> rounds) {
		
		public boolean possible(int[] limits) {
			return rounds.stream().allMatch(r -> r.possible(limits));
		}
		
		public int[] minPossible() {
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
	
	private record Round(int[] values) {
		
		public boolean possible(int[] limits) {
			for (int i = 0; i < limits.length; i++) {
				if (values[i] > limits[i])
					return false;
			}
			return true;
		}
		
		@Override
		public String toString() {
			return "Round[values=" + Arrays.toString(values) + "]";
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