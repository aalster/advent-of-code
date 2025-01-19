package org.advent.year2018.day4;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day4 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day4()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 240, 4455),
				new ExpectedAnswers("input.txt", 76357, 41668)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input).stream().sorted().toList();
	}
	
	@Override
	public Object part1() {
		Map<Integer, int[]> sleepMinutes = guardSleepMinutes();
		Map<Integer, Integer> totalSleepTime = sleepMinutes.entrySet().stream().collect(Collectors.toMap(
				Map.Entry::getKey, e -> Arrays.stream(e.getValue()).sum()));
		int maxSleepGuard = totalSleepTime.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow().getKey();
		int[] minutes = sleepMinutes.get(maxSleepGuard);
		int maxSleepMinute = ArrayUtils.indexOf(minutes, Arrays.stream(minutes).max().orElseThrow());
		return maxSleepGuard * maxSleepMinute;
	}
	
	@Override
	public Object part2() {
		Map<Integer, int[]> sleepMinutes = guardSleepMinutes();
		Map<Integer, Integer> maxSleepValues = sleepMinutes.entrySet().stream().collect(Collectors.toMap(
				Map.Entry::getKey, e -> Arrays.stream(e.getValue()).max().orElseThrow()));
		int maxSleepGuard = maxSleepValues.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow().getKey();
		int maxSleepMinute = ArrayUtils.indexOf(sleepMinutes.get(maxSleepGuard), maxSleepValues.get(maxSleepGuard));
		return maxSleepGuard * maxSleepMinute;
	}
	
	Map<Integer, int[]> guardSleepMinutes() {
		Map<Integer, int[]> sleepTimes = new HashMap<>();
		Function<String, Integer> parseTime = s -> Integer.valueOf(s.split(":")[1].replace("]", ""));
		int guard = -1;
		int sleepStart = 0;
		for (String line : lines) {
			if (line.endsWith("shift")) {
				guard = Integer.parseInt(line.split("Guard #")[1].split(" ")[0]);
			} else if (line.endsWith("falls asleep")) {
				sleepStart = parseTime.apply(line.replace(" falls asleep", ""));
			} else if (line.endsWith("wakes up")) {
				int sleepEnd = parseTime.apply(line.replace(" wakes up", ""));
				int[] times = sleepTimes.computeIfAbsent(guard, k -> new int[60]);
				for (int t = sleepStart; t < sleepEnd; t++)
					times[t]++;
			}
		}
		return sleepTimes;
	}
}