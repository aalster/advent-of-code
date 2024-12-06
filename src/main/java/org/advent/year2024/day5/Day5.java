package org.advent.year2024.day5;

import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Day5 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day5.class, "input.txt");
		List<Rule> rules = new ArrayList<>();
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty())
				break;
			rules.add(Rule.parse(line));
		}
		List<Update> updates = new ArrayList<>();
		while (input.hasNext())
			updates.add(Update.parse(input.nextLine()));
		
		System.out.println("Answer 1: " + part1(rules, updates));
		System.out.println("Answer 2: " + part2(rules, updates));
	}
	
	private static int part1(List<Rule> rules, List<Update> updates) {
		Map<Integer, Set<Integer>> rulesMap = new HashMap<>();
		for (Rule rule : rules)
			rulesMap.computeIfAbsent(rule.left(), k -> new HashSet<>()).add(rule.right());
		
		return updates.stream()
				.filter(update -> update.inCorrectOrder(rulesMap))
				.mapToInt(update -> update.pages()[update.pages().length / 2])
				.sum();
	}
	
	private static int part2(List<Rule> rules, List<Update> updates) {
		Map<Integer, Set<Integer>> rulesMap = new HashMap<>();
		for (Rule rule : rules)
			rulesMap.computeIfAbsent(rule.left(), k -> new HashSet<>()).add(rule.right());
		
		return updates.stream()
				.filter(update -> !update.inCorrectOrder(rulesMap))
				.map(update -> update.correct(rulesMap))
				.mapToInt(update -> update.pages()[update.pages().length / 2])
				.sum();
	}
	
	record Rule(int left, int right) {
		static Rule parse(String line) {
			String[] split = StringUtils.split(line, "|");
			return new Rule(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
		}
	}
	
	record Update(int[] pages) {
		
		boolean inCorrectOrder(Map<Integer, Set<Integer>> rulesMap) {
			for (int i = 0; i < pages.length; i++) {
				int left = pages[i];
				for (int k = i + 1; k < pages.length; k++) {
					int right = pages[k];
					if (rulesMap.getOrDefault(right, Set.of()).contains(left))
						return false;
				}
			}
			return true;
		}
		
		Update correct(Map<Integer, Set<Integer>> rulesMap) {
			Comparator<Integer> comparator = (left, right) -> {
				if (rulesMap.getOrDefault(left, Set.of()).contains(right))
					return -1;
				if (rulesMap.getOrDefault(right, Set.of()).contains(left))
					return 1;
				return 0;
			};
			return new Update(Arrays.stream(pages).boxed().sorted(comparator).mapToInt(i -> i).toArray());
		}
		
		static Update parse(String line) {
			return new Update(Arrays.stream(StringUtils.split(line, ",")).mapToInt(Integer::parseInt).toArray());
		}
	}
}