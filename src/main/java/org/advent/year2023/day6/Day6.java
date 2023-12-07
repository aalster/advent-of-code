package org.advent.year2023.day6;

import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Day6 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day6.class, "input.txt");
		String timeLine = input.nextLine();
		String distanceLine = input.nextLine();
		
		System.out.println("Answer 1: " + part1(timeLine, distanceLine));
		System.out.println("Answer 2: " + part2(timeLine, distanceLine));
	}
	
	private static long part1(String timeLine, String distanceLine) {
		Function<String, long[]> parser = value -> Arrays.stream(value.split(":")[1].split(" "))
				.filter(StringUtils::isNotBlank)
				.mapToLong(Long::parseLong)
				.toArray();
		long[] time = parser.apply(timeLine);
		long[] distance = parser.apply(distanceLine);
		List<Race> races = IntStream.range(0, time.length).mapToObj(i -> new Race(time[i], distance[i])).toList();
		return races.stream().mapToLong(Day6::winWays).reduce(1, (l, r) -> l * r);
	}
	
	private static long part2(String timeLine, String distanceLine) {
		Function<String, Long> parser = value -> Long.parseLong(value.split(":")[1].replaceAll(" ", ""));
		return winWays(new Race(parser.apply(timeLine), parser.apply(distanceLine)));
	}
	
	private static long winWays(Race race) {
		long count = 0;
		for (int i = 1; i < race.time() - 1; i++) {
			long distance = i * (race.time() - i);
			if (distance > race.distance())
				count++;
		}
		return count;
	}
	
	private record Race(long time, long distance) {
	}
}