package org.advent.year2023.day9;

import lombok.Data;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day9 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day9.class, "input.txt");
		List<String> lines = new ArrayList<>();
		while (input.hasNext()) {
			lines.add(input.nextLine());
		}
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static long part1(List<String> lines) {
		long result = 0;
		for (String line : lines) {
			Sequence sequence = Sequence.parse(line);
			result += sequence.get(sequence.size());
		}
		return result;
	}
	
	private static long part2(List<String> lines) {
		long result = 0;
		for (String line : lines) {
			Sequence sequence = Sequence.parse(line);
			result += sequence.get(-1);
		}
		return result;
	}
	
	@Data
	private static class Sequence {
		private final List<Integer> values;
		private Sequence subSequence;
		private Boolean allZeroes;
		private int shift = 0;
		
		int size() {
			return values.size();
		}
		
		int get(int index) {
			if (allZeroes())
				return 0;
			
			while (shift + index < 0) {
				shift++;
				values.addFirst(values.getFirst() - subSequence().get(-1));
			}
			
			while (size() <= index + shift) {
				values.add(values.getLast() + subSequence().get(size() - 1));
			}
			return values.get(index + shift);
		}
		
		Sequence subSequence() {
			if (subSequence == null) {
				if (allZeroes())
					throw new IllegalStateException("All zeroes subsequence call");
				
				List<Integer> diffs = new ArrayList<>();
				for (int i = 1; i < values.size(); i++)
					diffs.add(values.get(i) - values.get(i - 1));
				subSequence = new Sequence(diffs);
			}
			return subSequence;
		}
		
		boolean allZeroes() {
			if (allZeroes == null) {
				Set<Integer> set = new HashSet<>(values);
				allZeroes = set.size() == 1 && set.contains(0);
			}
			return allZeroes;
		}
		
		static Sequence parse(String line) {
			return new Sequence(Arrays.stream(line.split(" ")).map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new)));
		}
	}
}