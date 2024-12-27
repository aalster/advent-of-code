package org.advent.year2024.day1;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day1 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day1()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 11, 31),
				new ExpectedAnswers("input.txt", 2742123, 21328497)
		);
	}
	
	List<Integer> left;
	List<Integer> right;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		left = new ArrayList<>();
		right = new ArrayList<>();
		while (input.hasNext()) {
			String[] split = Stream.of(input.nextLine().split(" "))
					.filter(s -> !s.isEmpty())
					.toArray(String[]::new);
			left.add(Integer.parseInt(split[0]));
			right.add(Integer.parseInt(split[1]));
		}
	}
	
	@Override
	public Object part1() {
		List<Integer> l = new ArrayList<>(left);
		List<Integer> r = new ArrayList<>(right);
		l.sort(Integer::compareTo);
		r.sort(Integer::compareTo);
		
		long diff = 0;
		Iterator<Integer> leftIterator = l.iterator();
		Iterator<Integer> rightIterator = r.iterator();
		while (leftIterator.hasNext() && rightIterator.hasNext())
			diff += Math.abs(leftIterator.next() - rightIterator.next());
		return diff;
	}
	
	@Override
	public Object part2() {
		Map<Integer, Long> rightCounts = right.stream().collect(Collectors.groupingBy(n -> n, Collectors.counting()));
		return left.stream().mapToLong(n -> n * rightCounts.getOrDefault(n, 0L)).sum();
	}
}