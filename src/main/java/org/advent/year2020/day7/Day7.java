package org.advent.year2020.day7;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day7 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day7()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 4, 32),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 126),
				new ExpectedAnswers("input.txt", 235, 158493)
		);
	}
	
	Map<String, Rule> rules;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		rules = Utils.readLines(input).stream().map(Rule::parse).collect(Collectors.toMap(Rule::bag, r -> r));
	}
	
	@Override
	public Object part1() {
		Map<String, List<String>> wrappers = new HashMap<>();
		for (Rule rule : rules.values())
			for (Content content : rule.contents)
				wrappers.computeIfAbsent(content.bag, k -> new ArrayList<>()).add(rule.bag);
		
		Set<String> possible = new HashSet<>();
		List<String> queue = new ArrayList<>(wrappers.getOrDefault("shiny gold", List.of()));
		while (!queue.isEmpty()) {
			String bag = queue.removeFirst();
			if (!possible.add(bag))
				continue;
			queue.addAll(wrappers.getOrDefault(bag, List.of()));
		}
		return possible.size();
	}
	
	@Override
	public Object part2() {
		List<Content> contentsQueue = new ArrayList<>(rules.get("shiny gold").contents);
		int contentsBags = 0;
		while (!contentsQueue.isEmpty()) {
			Content content = contentsQueue.removeFirst();
			contentsBags += content.count;
			contentsQueue.addAll(rules.get(content.bag).contents.stream().map(c -> new Content(c.bag, c.count * content.count)).toList());
		}
		return contentsBags;
	}
	
	record Content(String bag, int count) {
	}
	
	record Rule(String bag, List<Content> contents) {
		
		static Rule parse(String line) {
			String[] split = line.replace(".", "").split(" bags contain ");
			String bag = split[0];
			
			if (split[1].equals("no other bags"))
				return new Rule(bag, List.of());
			
			List<Content> contents = Arrays.stream(split[1].split(", "))
					.map(s -> s.replaceAll(" bags?", "").split(" ", 2))
					.map(pair -> new Content(pair[1], Integer.parseInt(pair[0])))
					.toList();
			
			return new Rule(bag, contents);
		}
	}
}