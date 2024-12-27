package org.advent.year2024.day11;

import org.advent.common.Utils;
import org.advent.runner.AbstractDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2024.day24.Day24;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day11 extends AbstractDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day24()).run("input.txt");
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 55312, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 224529, 266820198587914L)
		);
	}
	
	String line;
	
	@Override
	public void prepare(String file) {
		line = Utils.scanFileNearClass(getClass(), file).nextLine();
	}
	
	@Override
	public Object part1() {
		return solve(25);
	}
	
	@Override
	public Object part2() {
		return solve(75);
	}
	
	long solve(int blinks) {
		Map<Long, Long> stones = Arrays.stream(line.split(" "))
				.map(Long::parseLong)
				.collect(Collectors.toMap(n -> n, n -> 1L));
		
		while (blinks > 0) {
			Map<Long, Long> nextStones = new HashMap<>();
			for (Map.Entry<Long, Long> entry : stones.entrySet()) {
				long stone = entry.getKey();
				if (stone == 0) {
					nextStones.compute(1L, (k, v) -> (v == null ? 0 : v) + entry.getValue());
					continue;
				}
				int digits = countDigits(stone);
				if (digits % 2 == 0) {
					long tens = tenPow(digits / 2);
					nextStones.compute(stone / tens, (k, v) -> (v == null ? 0 : v) + entry.getValue());
					nextStones.compute(stone % tens, (k, v) -> (v == null ? 0 : v) + entry.getValue());
					continue;
				}
				nextStones.compute(stone * 2024, (k, v) -> (v == null ? 0 : v) + entry.getValue());
			}
			stones = nextStones;
			blinks--;
		}
		return stones.values().stream().mapToLong(c -> c).sum();
	}
	
	int countDigits(long n) {
		int count = 0;
		while (n > 0) {
			n /= 10;
			count++;
		}
		return count;
	}
	
	long tenPow(int n) {
		long result = 1;
		while (n > 0) {
			result *= 10;
			n--;
		}
		return result;
	}
}