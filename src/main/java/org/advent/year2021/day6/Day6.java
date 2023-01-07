package org.advent.year2021.day6;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day6 {
	
	private static final int period = 7 - 1;
	private static final int initialPeriod = period + 2;
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day6.class, "input.txt");
		List<Integer> counters = new ArrayList<>();
		while (input.hasNext()) {
			counters.addAll(Arrays.stream(input.nextLine().split(",")).map(Integer::parseInt).toList());
		}
		
		System.out.println("Answer 1: " + solve(counters, 80));
		System.out.println("Answer 2: " + solve(counters, 256));
	}
	
	private static long solve(List<Integer> counters, int steps) {
		List<Fish> fishes = merge(counters.stream().map(c -> new Fish(c, 1))).toList();
		while (steps > 0) {
			fishes = merge(fishes.stream().flatMap(Fish::step)).toList();
			steps--;
		}
		return fishes.stream().mapToLong(Fish::duplicates).sum();
	}
	
	private static Stream<Fish> merge(Stream<Fish> fishes) {
		return fishes.collect(Collectors.groupingBy(Fish::counter))
				.entrySet().stream()
				.map(e -> new Fish(e.getKey(), e.getValue().stream().mapToLong(Fish::duplicates).sum()));
	}
	
	record Fish(int counter, long duplicates) {
		
		public Stream<Fish> step() {
			if (counter > 0)
				return Stream.of(new Fish(counter - 1, duplicates));
			return Stream.of(new Fish(period, duplicates), new Fish(initialPeriod, duplicates));
		}
	}
}