package org.advent.year2023.day12;

import org.advent.common.Utils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day12 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day12.class, "example.txt");
		List<String> lines = new ArrayList<>();
		while (input.hasNext()) {
			lines.add(input.nextLine());
		}
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static long part1(List<String> lines) {
		return solve(lines, 1);
	}
	
	private static long part2(List<String> lines) {
		return solve(lines, 5);
	}
	
	private static long solve(List<String> lines, int copies) {
		long result = 0;
		for (String line : lines) {
			ConditionsRecord record = ConditionsRecord.parse(line, copies);
			long variants = record.countVariants();
			System.out.println(line + ": " + variants);
			result += variants;
		}
		return result;
	}
	
	private record ConditionsRecord(int[] conditions, List<Integer> sizes) {
		static int EMPTY = 0;
		static int DAMAGED = 1;
		static int UNKNOWN = 2;
		
		boolean isValid() {
			int sizesCounter = 0;
			int currentSize = 0;
			for (int condition : conditions) {
				if (condition == UNKNOWN)
					return true;
				
				if (condition == DAMAGED) {
					currentSize++;
					continue;
				}
				
				if (condition == EMPTY) {
					if (currentSize == 0)
						continue;
					
					if (sizesCounter >= sizes.size() || currentSize != sizes.get(sizesCounter))
						return false;
					currentSize = 0;
					sizesCounter++;
				}
			}
			if (currentSize > 0) {
				if (sizesCounter >= sizes.size() || currentSize != sizes.get(sizesCounter))
					return false;
				sizesCounter++;
			}
			return sizes.size() == sizesCounter;
		}
		
		long countVariants() {
			if (!isValid()) {
//				System.out.println("not valid: " + this);
				return 0;
			}
			
			int firstUnknown = ArrayUtils.indexOf(conditions, UNKNOWN);
			if (firstUnknown < 0) {
//				System.out.println("Valid: " + this);
				return 1;
			}
			
			int[] left = ArrayUtils.clone(conditions);
			int[] right = ArrayUtils.clone(conditions);
			left[firstUnknown] = EMPTY;
			right[firstUnknown] = DAMAGED;
			return new ConditionsRecord(left, sizes).countVariants() + new ConditionsRecord(right, sizes).countVariants();
		}
		
		@Override
		public String toString() {
			return Arrays.stream(conditions).mapToObj(v -> v == EMPTY ? "." : v == DAMAGED ? "#" : "?").collect(Collectors.joining())
					+ " " + sizes.stream().map(String::valueOf).collect(Collectors.joining(", "));
		}
		
		static ConditionsRecord parse(String line, int copies) {
			String[] split = line.split(" ");
			
			String conditionsString = IntStream.range(0, copies).mapToObj(i -> split[0]).collect(Collectors.joining("?"));
			int[] conditions = conditionsString.chars().map(c -> c == '.' ? EMPTY : c == '#' ? DAMAGED : UNKNOWN).toArray();
			
			String sizesString = IntStream.range(0, copies).mapToObj(i -> split[1]).collect(Collectors.joining(","));
			List<Integer> sizes = Arrays.stream(sizesString.split(",")).map(Integer::parseInt).toList();
			return new ConditionsRecord(conditions, sizes);
		}
	}
}