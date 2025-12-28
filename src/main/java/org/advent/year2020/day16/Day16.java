package org.advent.year2020.day16;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day16 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day16()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 71, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 1716),
				new ExpectedAnswers("input.txt", 25059, 3253972369789L)
		);
	}
	
	Map<String, Ranges> rules;
	int[] yourTicket;
	List<int[]> nearbyTickets;
	String fieldPrefix;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<List<String>> groups = Utils.splitByEmptyLine(Utils.readLines(input));
		
		rules = groups.getFirst().stream()
				.map(line -> line.split(": "))
				.collect(Collectors.toMap(s -> s[0], s -> Ranges.parse(s[1])));
		
		yourTicket = Arrays.stream(groups.get(1).get(1).split(",")).mapToInt(Integer::parseInt).toArray();
		
		nearbyTickets = groups.getLast().stream()
				.skip(1)
				.map(line -> Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray())
				.toList();
		
		fieldPrefix = "input.txt".equals(file) ? "departure " : "";
	}
	
	@Override
	public Object part1() {
		int result = 0;
		for (int[] ticket : nearbyTickets)
			for (int value : ticket)
				if (rules.values().stream().noneMatch(ranges -> ranges.contains(value)))
					result += value;
		return result;
	}
	
	@Override
	public Object part2() {
		List<Set<String>> possibleFields = new ArrayList<>();
		rules.keySet().forEach(k -> possibleFields.add(new HashSet<>(rules.keySet())));
		
		List<int[]> tickets = new ArrayList<>(nearbyTickets);
		tickets.add(yourTicket);
		
		for (Iterator<int[]> iterator = tickets.iterator(); iterator.hasNext(); )
			for (int value : iterator.next())
				if (rules.values().stream().noneMatch(ranges -> ranges.contains(value)))
					iterator.remove();
		
		for (int index = 0; index < possibleFields.size(); index++) {
			for (Iterator<String> iterator = possibleFields.get(index).iterator(); iterator.hasNext(); ) {
				Ranges currentRule = rules.get(iterator.next());
				for (int[] ticket : tickets) {
					if (!currentRule.contains(ticket[index])) {
						iterator.remove();
						break;
					}
				}
			}
		}
		
		for (Set<String> fields : possibleFields)
			if (fields.size() == 1)
				removeUniqueRecursive(possibleFields, fields.iterator().next());
		
		return IntStream.range(0, possibleFields.size())
				.filter(index -> possibleFields.get(index).iterator().next().startsWith(fieldPrefix))
				.mapToLong(index -> yourTicket[index]).reduce(1, (a, b) -> a * b);
	}
	
	void removeUniqueRecursive(List<Set<String>> valuesList, String remove) {
		for (Set<String> values : valuesList)
			if (values.size() > 1 && values.remove(remove) && values.size() == 1)
				removeUniqueRecursive(valuesList, values.iterator().next());
	}
	
	record Ranges(List<Range> ranges) {
		boolean contains(int value) {
			return ranges.stream().anyMatch(r -> r.contains(value));
		}
		
		static Ranges parse(String line) {
			return new Ranges(Arrays.stream(line.split(" or ")).map(Range::parse).toList());
		}
	}
	
	record Range(int min, int max) {
		boolean contains(int value) {
			return min <= value && value <= max;
		}
		
		static Range parse(String range) {
			String[] split = range.split("-");
			return new Range(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
		}
	}
}