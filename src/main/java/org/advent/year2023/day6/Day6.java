package org.advent.year2023.day6;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Day6 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day6()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 288, 71503),
				new ExpectedAnswers("input.txt", 220320, 34454850)
		);
	}
	
	String timeLine;
	String distanceLine;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		timeLine = input.nextLine();
		distanceLine = input.nextLine();
	}
	
	@Override
	public Object part1() {
		Function<String, long[]> parser = value -> Arrays.stream(value.split(":")[1].split(" "))
				.filter(StringUtils::isNotBlank)
				.mapToLong(Long::parseLong)
				.toArray();
		long[] time = parser.apply(timeLine);
		long[] distance = parser.apply(distanceLine);
		List<Race> races = IntStream.range(0, time.length).mapToObj(i -> new Race(time[i], distance[i])).toList();
		return races.stream().mapToLong(Day6::winWays).reduce(1, (l, r) -> l * r);
	}
	
	@Override
	public Object part2() {
		Function<String, Long> parser = value -> Long.parseLong(value.split(":")[1].replaceAll(" ", ""));
		return winWays(new Race(parser.apply(timeLine), parser.apply(distanceLine)));
	}
	
	static long winWays(Race race) {
		long count = 0;
		for (int i = 1; i < race.time() - 1; i++) {
			long distance = i * (race.time() - i);
			if (distance > race.distance())
				count++;
		}
		return count;
	}
	
	record Race(long time, long distance) {
	}
}