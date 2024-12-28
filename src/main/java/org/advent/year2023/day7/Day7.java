package org.advent.year2023.day7;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day7 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day7()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 6440, 5905),
				new ExpectedAnswers("input.txt", 248217452, 245576185)
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
		return solve(lines, null);
	}
	
	@Override
	public Object part2() {
		return solve(lines, 'J');
	}
	
	static long solve(List<String> data, Character joker) {
		List<Round> rounds = data.stream().map(value -> Round.parse(value, joker)).sorted(Comparator.comparing(Round::hand)).toList();
		long result = 0;
		long index = 1;
		for (Round round : rounds) {
			result += index * round.bid();
			index++;
		}
		return result;
	}
	
	record Round(Hand hand, int bid) {
		static Round parse(String value, Character joker) {
			String[] split = value.split(" ");
			return new Round(Hand.parse(split[0], joker), Integer.parseInt(split[1]));
		}
	}
	
	record Hand(int[] cards, int rank) implements Comparable<Hand> {
		
		@Override
		public int compareTo(Hand other) {
			int compareRank = Integer.compare(rank, other.rank);
			if (compareRank != 0)
				return compareRank;
			for (int i = 0; i < cards.length; i++) {
				int compare = Integer.compare(cards[i], other.cards[i]);
				if (compare != 0)
					return compare;
			}
			return 0;
		}
		
		static Map<Integer, Integer> cardsMapping = Map.of(
				'A', 14,
				'K', 13,
				'Q', 12,
				'J', 11,
				'T', 10
		).entrySet().stream().collect(Collectors.toMap(e -> (int) e.getKey(), Map.Entry::getValue));
		
		static Hand parse(String value, Character joker) {
			int jokerCode = joker == null ? -1 : cardsMapping.getOrDefault((int) joker, joker - '0');
			if (value.length() != 5)
				throw new IllegalArgumentException();
			int[] cards = value.chars().map(c -> cardsMapping.getOrDefault(c, c - '0')).toArray();
			int[] cardsCountingJoker = jokerCode < 0 ? cards : Arrays.stream(cards).map(c -> c == jokerCode ? 1 : c).toArray();
			return new Hand(cardsCountingJoker, computeRank(cards, jokerCode));
		}
		
		static int computeRank(int[] cards, int jokerCode) {
			Map<Integer, Long> counts = Arrays.stream(cards).boxed().collect(Collectors.groupingBy(c -> c, Collectors.counting()));
			counts = new HashMap<>(counts);
			Long jokersCount = counts.remove(jokerCode);
			if (jokersCount != null) {
				if (jokersCount == 5)
					return 7;
				Map.Entry<Integer, Long> maxEntry = counts.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow();
				counts.put(maxEntry.getKey(), maxEntry.getValue() + jokersCount);
			}
			
			return switch (counts.size()) {
				case 1 -> 7;
				case 2 -> counts.containsValue(4L) ? 6 : 5;
				case 3 -> counts.containsValue(3L) ? 4 : 3;
				default -> counts.containsValue(2L) ? 2 : 1;
			};
		}
	}
}