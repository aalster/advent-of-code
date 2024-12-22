package org.advent.year2024.day22;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day22 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day22.class, "input.txt");
		long[] secrets = Utils.readLines(input).stream().mapToLong(Long::parseLong).toArray();
		
		System.out.println("Answer 1: " + part1(secrets));
		System.out.println("Answer 2: " + part2(secrets));
	}
	
	private static long part1(long[] secrets) {
		long result = 0;
		for (long secret : secrets) {
			for (int i = 0; i < 2000; i++)
				secret = nextSecret(secret);
			result += secret;
		}
		return result;
	}
	
	private static long part2(long[] secrets) {
		List<Map<Integer, Integer>> allPrices = new ArrayList<>();
		for (long secret : secrets) {
			Map<Integer, Integer> prices = new HashMap<>();
			
			int prevPrice = ((int) secret % 10);
			int fourDiffs = 0;
			for (int i = 0; i < 2000; i++) {
				secret = nextSecret(secret);
				
				int price = (int) secret % 10;
				int priceDiff = price - prevPrice;
				fourDiffs = (fourDiffs % (20*20*20)) * 20 + priceDiff + 10;
				if (i > 2)
					prices.putIfAbsent(fourDiffs, price);
				prevPrice = price;
			}
			allPrices.add(prices);
		}
		
		Map<Integer, Integer> totals = new HashMap<>();
		for (Map<Integer, Integer> prices : allPrices)
			for (Map.Entry<Integer, Integer> entry : prices.entrySet())
				totals.compute(entry.getKey(), (k, v) -> v == null ? entry.getValue() : v + entry.getValue());
		return totals.values().stream().mapToInt(i -> i).max().orElseThrow();
	}
	
	static final long prune = 16777216;
	
	static long nextSecret(long secret) {
		secret = (secret ^ (secret << 6)) % prune;
		secret = (secret ^ (secret >> 5)) % prune;
		secret = (secret ^ (secret << 11)) % prune;
		return secret;
	}
}