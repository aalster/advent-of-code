package org.advent.year2024.day5;

import org.advent.common.Utils;
import org.advent.runner.AbstractDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
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

public class Day5 extends AbstractDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day5()).run("input.txt");
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 143, 123),
				new ExpectedAnswers("input.txt", 5391, 6142)
		);
	}
	
	List<Rule> rules;
	List<Update> updates;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		rules = new ArrayList<>();
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty())
				break;
			rules.add(Rule.parse(line));
		}
		updates = new ArrayList<>();
		while (input.hasNext())
			updates.add(Update.parse(input.nextLine()));
	}
	
	@Override
	public Object part1() {
		Map<Integer, Set<Integer>> rulesMap = new HashMap<>();
		for (Rule rule : rules)
			rulesMap.computeIfAbsent(rule.left(), k -> new HashSet<>()).add(rule.right());
		
		return updates.stream()
				.filter(update -> update.inCorrectOrder(rulesMap))
				.mapToInt(update -> update.pages()[update.pages().length / 2])
				.sum();
	}
	
	@Override
	public Object part2() {
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