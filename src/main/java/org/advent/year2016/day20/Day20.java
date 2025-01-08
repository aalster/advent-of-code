package org.advent.year2016.day20;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Day20 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day20()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 3, 2),
				new ExpectedAnswers("input.txt", 22887907, 109)
		);
	}
	
	List<Range> ranges;
	long max;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		ranges = Utils.readLines(input).stream().map(Range::parse).toList();
		max = switch (file) {
			case "example.txt" -> 9;
			case "input.txt" -> 4294967295L;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		List<Range> sortedRanges = new ArrayList<>(ranges);
		sortedRanges.sort(Comparator.comparing(Range::start).thenComparing(Range::end));
		return removeAndMergeLowest(sortedRanges).end + 1;
	}
	
	@Override
	public Object part2() {
		List<Range> sortedRanges = new ArrayList<>(ranges);
		sortedRanges.sort(Comparator.comparing(Range::start).thenComparing(Range::end));
		List<Range> mergedRanges = new ArrayList<>();
		while (!sortedRanges.isEmpty())
			mergedRanges.add(removeAndMergeLowest(sortedRanges));
		return max + 1 - mergedRanges.stream().mapToLong(Range::size).sum();
	}
	
	Range removeAndMergeLowest(List<Range> sortedRanges) {
		Range minMerged = sortedRanges.removeFirst();
		while (!sortedRanges.isEmpty() && minMerged.intersects(sortedRanges.getFirst()))
			minMerged = minMerged.merge(sortedRanges.removeFirst());
		return minMerged;
	}
	
	record Range(long start, long end) {
		
		boolean intersects(Range other) {
			return contains(other.start) || other.contains(end + 1);
		}
		
		boolean contains(long x) {
			return start <= x && x <= end;
		}
		
		Range merge(Range other) {
			return new Range(Math.min(start, other.start), Math.max(end, other.end));
		}
		
		long size() {
			return end - start + 1;
		}
		
		static Range parse(String line) {
			String[] split = line.split("-");
			return new Range(Long.parseLong(split[0]), Long.parseLong(split[1]));
		}
	}
}