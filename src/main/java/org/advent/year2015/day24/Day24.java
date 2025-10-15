package org.advent.year2015.day24;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalLong;
import java.util.Scanner;

public class Day24 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day24()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 99, 44),
				new ExpectedAnswers("input.txt", 10723906903L, 74850409)
		);
	}
	
	int[] weights;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		weights = Utils.readLines(input).stream()
				.map(Integer::parseInt)
				.sorted(Comparator.reverseOrder())
				.mapToInt(i -> i)
				.toArray();
	}
	
	@Override
	public Object part1() {
		return solve(weights, 3);
	}
	
	@Override
	public Object part2() {
		return solve(weights, 4);
	}
	
	long solve(int[] weights, int groupsCount) {
		int groupTotal = Arrays.stream(weights).sum() / groupsCount;
		for (int firstGroupSize = 1; firstGroupSize < weights.length; firstGroupSize++) {
			OptionalLong result = combinations(weights, groupTotal, 0, new Group(), firstGroupSize).stream()
					.sorted(Comparator.comparing(Group::quantumEntanglement))
					.filter(group -> {
						int[] nextWeights = Arrays.stream(weights).filter(group::notUsed).toArray();
						return otherGroupsSolvable(nextWeights, groupTotal, groupsCount - 1);
					})
					.mapToLong(Group::quantumEntanglement)
					.findFirst();
			if (result.isPresent())
				return result.getAsLong();
		}
		return 0;
	}
	
	List<Group> combinations(int[] weights, int groupTotal, int index, Group group, int maxWeights) {
		if (group.total == groupTotal)
			return List.of(group);
		
		if (index >= weights.length || group.length() >= maxWeights)
			return List.of();
		
		List<Group> result = combinations(weights, groupTotal, index + 1, group, maxWeights);
		if (weights[index] + group.total <= groupTotal) {
			result = new ArrayList<>(result);
			result.addAll(combinations(weights, groupTotal, index + 1, group.add(weights[index]), maxWeights));
		}
		return result;
	}
	
	boolean otherGroupsSolvable(int[] weights, int groupTotal, int groupsToSolve) {
		return groupsToSolve == 1 || combinations(weights, groupTotal, 0, new Group(), Integer.MAX_VALUE).stream()
				.map(group -> Arrays.stream(weights).filter(group::notUsed).toArray())
				.anyMatch(nextWeights -> otherGroupsSolvable(nextWeights, groupTotal, groupsToSolve - 1));
	}
	
	record Group(int[] weights, int total) {
		
		public Group() {
			this(new int[0], 0);
		}
		
		int length() {
			return weights.length;
		}
		
		boolean notUsed(int weight) {
			return Arrays.stream(weights).noneMatch(w -> w == weight);
		}
		
		long quantumEntanglement() {
			return Arrays.stream(weights).mapToLong(w -> w).reduce(1, (a, b) -> a * b);
		}
		
		Group add(int weight) {
			int[] nextWeights = Arrays.copyOf(weights, weights.length + 1);
			nextWeights[weights.length] = weight;
			return new Group(nextWeights, total + weight);
		}
	}
}