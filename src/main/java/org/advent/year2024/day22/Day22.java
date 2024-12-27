package org.advent.year2024.day22;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day22 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day22()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 37327623, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 23),
				new ExpectedAnswers("input.txt", 19458130434L, 2130)
		);
	}
	
	long[] secrets;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		secrets = Utils.readLines(input).stream().mapToLong(Long::parseLong).toArray();
	}
	
	@Override
	public Object part1() {
		long result = 0;
		for (long secret : secrets) {
			for (int i = 0; i < 2000; i++)
				secret = nextSecret(secret);
			result += secret;
		}
		return result;
	}
	
	@Override
	public Object part2() {
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
	
	final long prune = 16777216;
	
	long nextSecret(long secret) {
		secret = (secret ^ (secret << 6)) % prune;
		secret = (secret ^ (secret >> 5)) % prune;
		secret = (secret ^ (secret << 11)) % prune;
		return secret;
	}
}