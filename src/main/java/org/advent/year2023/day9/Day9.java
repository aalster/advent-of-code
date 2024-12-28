package org.advent.year2023.day9;

import lombok.Data;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day9 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day9()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 114, 2),
				new ExpectedAnswers("input.txt", 1708206096, 1050)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		lines = Utils.readLines(Utils.scanFileNearClass(getClass(), file));
	}
	
	@Override
	public Object part1() {
		return lines.stream().map(Sequence::parse).mapToLong(sequence -> sequence.get(sequence.size())).sum();
	}
	
	@Override
	public Object part2() {
		return lines.stream().map(Sequence::parse).mapToLong(sequence -> sequence.get(-1)).sum();
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
			return new Sequence(Arrays.stream(line.split(" ")).map(Integer::parseInt)
					.collect(Collectors.toCollection(ArrayList::new)));
		}
	}
}