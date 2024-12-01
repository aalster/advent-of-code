package org.advent.year2024.template;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day1 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day1.class, "input.txt");
		
		List<Integer> left = new ArrayList<>();
		List<Integer> right = new ArrayList<>();
		while (input.hasNext()) {
			String[] split = Stream.of(input.nextLine().split(" "))
					.filter(s -> !s.isEmpty())
					.toArray(String[]::new);
			left.add(Integer.parseInt(split[0]));
			right.add(Integer.parseInt(split[1]));
		}
		
		System.out.println("Answer 1: " + part1(left, right));
		System.out.println("Answer 2: " + part2(left, right));
	}
	
	private static long part1(List<Integer> left, List<Integer> right) {
		left = new ArrayList<>(left);
		right = new ArrayList<>(right);
		left.sort(Integer::compareTo);
		right.sort(Integer::compareTo);
		
		long diff = 0;
		Iterator<Integer> leftIterator = left.iterator();
		Iterator<Integer> rightIterator = right.iterator();
		while (leftIterator.hasNext() && rightIterator.hasNext())
			diff += Math.abs(leftIterator.next() - rightIterator.next());
		return diff;
	}
	
	private static long part2(List<Integer> left, List<Integer> right) {
		Map<Integer, Long> rightCounts = right.stream().collect(Collectors.groupingBy(n -> n, Collectors.counting()));
		return left.stream()
				.mapToLong(n -> n * rightCounts.getOrDefault(n, 0L))
				.sum();
	}
}