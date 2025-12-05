package org.advent.year2025.day5;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day5 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day5()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 3, 14),
				new ExpectedAnswers("input.txt", 613, 336495597913098L)
		);
	}
	
	List<Range> ranges;
	List<Long> ingredients;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<List<String>> parts = Utils.splitByEmptyLine(Utils.readLines(input));
		ranges = parts.getFirst().stream().map(Range::parse).toList();
		ingredients = parts.getLast().stream().map(Long::parseLong).toList();
	}
	
	@Override
	public Object part1() {
		ranges = merge(ranges);
		return ingredients.stream().filter(i -> ranges.stream().anyMatch(r -> r.contains(i))).count();
	}
	
	@Override
	public Object part2() {
		return merge(ranges).stream().mapToLong(Range::size).sum();
	}
	
	private List<Range> merge(List<Range> ranges) {
		List<Range> notMerged = ranges.stream().sorted(Comparator.comparing(Range::min)).collect(Collectors.toCollection(LinkedList::new));
		List<Range> merged = new ArrayList<>(ranges.size());
		
		while (!notMerged.isEmpty()) {
			Range current = notMerged.removeFirst();
			for (Iterator<Range> iterator = notMerged.iterator(); iterator.hasNext(); ) {
				Range mergedRange = current.merge(iterator.next());
				if (mergedRange != null) {
					iterator.remove();
					current = mergedRange;
				}
			}
			merged.add(current);
		}
		return merged;
	}
	
	record Range(long min, long max) {
		
		Range merge(Range other) {
			if (other.min < min)
				return other.merge(this);
			
			if (other.min <= max + 1)
				return new Range(min, Math.max(max, other.max));
			return null;
		}
		
		boolean contains(long value) {
			return min <= value && value <= max;
		}
		
		long size() {
			return max - min + 1;
		}
		
		static Range parse(String line) {
			String[] parts = line.split("-");
			return new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
		}
	}
}