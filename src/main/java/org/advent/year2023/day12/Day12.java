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
		Scanner input = Utils.scanFileNearClass(Day12.class, "input.txt");
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
		long start = System.currentTimeMillis();
		int i = 0;
		for (String line : lines) {
			ConditionsRecord record = ConditionsRecord.parse(line, copies);
			long variants = record.countVariants();
			System.out.println(i++ + " (" + (System.currentTimeMillis() - start) + ") - " + line + ": " + variants);
			result += variants;
		}
		return result;
	}
	
	private record ConditionsRecord(int[] conditions, List<Integer> sizes, int nonEmptyCount, int sizesSum) {
		static int EMPTY = 0;
		static int DAMAGED = 1;
		static int UNKNOWN = 2;
		
		long countVariants() {
			int firstDamaged = ArrayUtils.indexOf(conditions, DAMAGED);
			if (sizes.isEmpty())
				return firstDamaged >= 0 ? 0 : 1;
			
			int firstUnknown = ArrayUtils.indexOf(conditions, UNKNOWN);
			
			if (firstUnknown < 0 && firstDamaged < 0)
				return 0;
			
			if (nonEmptyCount < sizesSum)
				return 0;
			
			if (conditions.length > 30) {
				int emptyCenter = ArrayUtils.indexOf(conditions, EMPTY, conditions.length / 3);
				if (0 <= emptyCenter && emptyCenter < conditions.length * 2 / 3) {
					return splitAndCount(emptyCenter);
				}
				int emptyUnknown = ArrayUtils.indexOf(conditions, UNKNOWN, conditions.length / 3);
				if (0 <= emptyUnknown && emptyUnknown < conditions.length * 2 / 3) {
					int[] newConditions = ArrayUtils.clone(conditions);
					newConditions[emptyUnknown] = DAMAGED;
					long other = new ConditionsRecord(newConditions, sizes, nonEmptyCount, sizesSum).countVariants();
					return other + splitAndCount(emptyUnknown);
				}
			}
			
			int nextSize = sizes.getFirst();
			if (firstDamaged >= 0 && (firstUnknown < 0 || firstDamaged < firstUnknown)) {
				int nextEmpty = ArrayUtils.indexOf(conditions, EMPTY, firstDamaged);
				if (nextEmpty < 0)
					nextEmpty = conditions.length;
				
				if (nextEmpty < firstDamaged + nextSize)
					return 0;
				
				if (conditions.length > firstDamaged + nextSize && conditions[firstDamaged + nextSize] == DAMAGED)
					return 0;
				
				if (conditions.length == firstDamaged + nextSize)
					return sizes.size() == 1 ? 1 : 0;
				
				int[] newCondition = ArrayUtils.subarray(conditions, firstDamaged + nextSize + 1, conditions.length);
				ConditionsRecord invariant = new ConditionsRecord(newCondition, sizes.subList(1, sizes.size()),
						nonEmptyCount - nextSize, sizesSum - nextSize);
//				System.out.println("Invariant: " + this + " -> " + invariant);
				return invariant.countVariants();
			}
			
			int[] left = ArrayUtils.clone(conditions);
			int[] right = ArrayUtils.clone(conditions);
			left[firstUnknown] = EMPTY;
			right[firstUnknown] = DAMAGED;
			return new ConditionsRecord(left, sizes, nonEmptyCount - 1, sizesSum).countVariants()
					+ new ConditionsRecord(right, sizes, nonEmptyCount, sizesSum).countVariants();
		}
		
		private long splitAndCount(int splitCondition) {
			int[] conditionsLeft = ArrayUtils.subarray(conditions, 0, splitCondition);
			int[] conditionsRight = ArrayUtils.subarray(conditions, splitCondition + 1, conditions.length);
			long result = 0;
			for (int splitIndex = 0; splitIndex <= sizes.size(); splitIndex++) {
				List<Integer> sizesLeft = sizes.subList(0, splitIndex);
				List<Integer> sizesRight = sizes.subList(splitIndex, sizes.size());
				ConditionsRecord left = ConditionsRecord.of(conditionsLeft, sizesLeft);
				ConditionsRecord right = ConditionsRecord.of(conditionsRight, sizesRight);
				result += left.countVariants() * right.countVariants();
			}
			return result;
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
			return ConditionsRecord.of(conditions, sizes);
		}
		
		static ConditionsRecord of(int[] conditions, List<Integer> sizes) {
			int nonEmptyCount = (int) Arrays.stream(conditions).filter(c -> c != EMPTY).count();
			int sizesSum = sizes.stream().mapToInt(i -> i).sum();
			return new ConditionsRecord(conditions, sizes, nonEmptyCount, sizesSum);
		}
	}
}