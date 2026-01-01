package org.advent.year2020.day19;

import lombok.RequiredArgsConstructor;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day19()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 2, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 3, 12),
				new ExpectedAnswers("input.txt", 124, 228)
		);
	}
	
	List<String> rulesDescriptions;
	List<String> messages;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<List<String>> groups = Utils.splitByEmptyLine(Utils.readLines(input));
		rulesDescriptions = groups.getFirst();
		messages = groups.getLast();
	}
	
	@Override
	public Object part1() {
		Rule rule = Rule.parseTree(rulesDescriptions, List.of());
		return messages.stream().filter(rule::matches).count();
	}
	
	@Override
	public Object part2() {
		Rule rule = Rule.parseTree(rulesDescriptions, List.of("8: 42 ?", "11: 42 ? 31"));
		return messages.stream().filter(rule::matches).count();
	}
	
	interface Rule {
		
		List<Integer> match(String line, int index);
		
		Rule substitute(Map<Integer, Rule> rules);
		
		default boolean matches(String line) {
			return match(line, 0).stream().anyMatch(m -> m == line.length());
		}
		
		
		static Rule parse(String line) {
			if (line.equals("?"))
				return RecursiveRule.PLACEHOLDER;
			if (line.contains("\""))
				return new SymbolRule(line.charAt(1));
			if (line.contains(" | "))
				return new ChoiseRule(Stream.of(line.split(" \\| ")).map(Rule::parse).toList());
			if (line.contains("?"))
				return new RecursiveRule(Stream.of(line.split(" ")).map(Rule::parse).toList());
			if (line.contains(" "))
				return new SequenceRule(Stream.of(line.split(" ")).map(Rule::parse).toList());
			return new ReferenceRule(Integer.parseInt(line));
		}
		
		static Map<Integer, Rule> parse(List<String> lines) {
			return lines.stream()
					.map(l -> l.split(": "))
					.collect(Collectors.toMap(s -> Integer.parseInt(s[0]), s -> parse(s[1])));
		}
		
		static Rule parseTree(List<String> lines, List<String> replacements) {
			Map<Integer, Rule> rules = new HashMap<>(parse(lines));
			rules.putAll(parse(replacements));
			return rules.get(0).substitute(rules);
		}
	}
	
	@RequiredArgsConstructor
	static class ReferenceRule implements Rule {
		final int reference;
		
		@Override
		public List<Integer> match(String line, int index) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Rule substitute(Map<Integer, Rule> rules) {
			return rules.get(reference).substitute(rules);
		}
	}
	
	@RequiredArgsConstructor
	static class SymbolRule implements Rule {
		final char symbol;
		
		@Override
		public List<Integer> match(String line, int index) {
			return index < line.length() && line.charAt(index) == symbol ? List.of(1) : List.of();
		}
		
		@Override
		public Rule substitute(Map<Integer, Rule> rules) {
			return this;
		}
	}
	
	@RequiredArgsConstructor
	static class SequenceRule implements Rule {
		final List<Rule> children;
		
		@Override
		public List<Integer> match(String line, int index) {
			List<Integer> sizes = List.of(0);
			for (Rule child : children) {
				sizes = matchChild(line, index, child, sizes);
				if (sizes.isEmpty())
					return sizes;
			}
			return sizes;
		}
		
		List<Integer> matchChild(String line, int index, Rule child, List<Integer> sizes) {
			return sizes.stream().flatMap(s -> child.match(line, index + s).stream().map(r -> s + r)).toList();
		}
		
		@Override
		public Rule substitute(Map<Integer, Rule> rules) {
			return new SequenceRule(children.stream().map(r -> r.substitute(rules)).toList());
		}
	}
	
	@RequiredArgsConstructor
	static class ChoiseRule implements Rule {
		final List<Rule> children;
		
		@Override
		public List<Integer> match(String line, int index) {
			return children.stream().flatMap(r -> r.match(line, index).stream()).toList();
		}
		
		@Override
		public Rule substitute(Map<Integer, Rule> rules) {
			return new ChoiseRule(children.stream().map(r -> r.substitute(rules)).toList());
		}
	}
	
	static class RecursiveRule extends SequenceRule {
		static final Rule PLACEHOLDER = new RecursiveRule(List.of());
		
		RecursiveRule(List<Rule> children) {
			super(children);
		}
		
		@Override
		public List<Integer> match(String line, int index) {
			List<Integer> sizes = List.of(0);
			for (Rule child : children) {
				if (child == PLACEHOLDER)
					sizes = Stream.of(sizes, matchChild(line, index, this, sizes)).flatMap(List::stream).toList();
				else
					sizes = matchChild(line, index, child, sizes);
				if (sizes.isEmpty())
					return sizes;
			}
			return sizes;
		}
		
		@Override
		public Rule substitute(Map<Integer, Rule> rules) {
			return new RecursiveRule(children.stream().map(r -> r == PLACEHOLDER ? r : r.substitute(rules)).toList());
		}
	}
}