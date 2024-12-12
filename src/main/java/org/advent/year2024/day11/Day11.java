package org.advent.year2024.day11;

import org.advent.common.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day11 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day11.class, "input.txt");
		String line = input.nextLine();
		
		System.out.println("Answer 1: " + solve(line, 25));
		System.out.println("Answer 2: " + solve(line, 75));
	}
	
	private static long solve(String line, int blinks) {
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
	
	static int countDigits(long n) {
		int count = 0;
		while (n > 0) {
			n /= 10;
			count++;
		}
		return count;
	}
	
	static long tenPow(int n) {
		long result = 1;
		while (n > 0) {
			result *= 10;
			n--;
		}
		return result;
	}
}