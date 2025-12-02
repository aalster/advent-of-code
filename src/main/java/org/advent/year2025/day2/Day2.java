package org.advent.year2025.day2;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Day2 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day2()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 1227775554, 4174379265L),
				new ExpectedAnswers("input.txt", 23560874270L, 44143124633L)
		);
	}
	
	List<Range> ranges;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		ranges = Arrays.stream(String.join("", Utils.readLines(input)).split(","))
				.flatMap(Range::parseAndSplit)
				.toList();
	}
	
	@Override
	public Object part1() {
		Function<Integer, int[]> repeatsCountsProvider = digitsCount -> new int[] {2};
		return ranges.stream().flatMapToLong(r -> r.getInvalidIds(repeatsCountsProvider)).sum();
	}
	
	@Override
	public Object part2() {
		Function<Integer, int[]> repeatsCountsProvider = digitsCount ->
				IntStream.rangeClosed(2, digitsCount).filter(repeats -> digitsCount % repeats == 0).toArray();
		return ranges.stream().flatMapToLong(r -> r.getInvalidIds(repeatsCountsProvider)).sum();
	}
	
	static long tenPow(int exponent) {
		int result = 1;
		while (exponent > 0) {
			result *= 10;
			exponent--;
		}
		return result;
	}
	
	static long repeatNumber(long value, int times, int digitsCount) {
		long mask = tenPow(digitsCount);
		long result = 0;
		while (times-- > 0)
			result = result * mask + value;
		return result;
	}
	
	record Range(long min, long max, int digitsCount) {
		
		Range(String min, String max, int digitsCount) {
			this(Long.parseLong(min), Long.parseLong(max), digitsCount);
		}
		
		LongStream getInvalidIds(Function<Integer, int[]> repeatsCountsProvider) {
			Set<Long> invalidIds = new HashSet<>();
			for (int repeats : repeatsCountsProvider.apply(digitsCount)) {
				int repeatSize = digitsCount / repeats;
				long mask = tenPow(digitsCount - repeatSize);
				
				LongStream.rangeClosed(min / mask, max / mask)
						.map(part -> repeatNumber(part, repeats, repeatSize))
						.filter(part -> min <= part && part <= max)
						.forEach(invalidIds::add);
			}
			return invalidIds.stream().mapToLong(n -> n);
		}
		
		static Stream<Range> parseAndSplit(String line) {
			String[] parts = line.split("-");
			return parseAndSplit(parts[0], parts[1]);
		}
		
		static Stream<Range> parseAndSplit(String min, String max) {
			if (min.length() == max.length())
				return Stream.of(new Range(min, max, min.length()));
			return Stream.concat(
					Stream.of(new Range(min, "9".repeat(min.length()), min.length())),
					parseAndSplit("1" + "0".repeat(min.length()), max)
			);
		}
	}
}