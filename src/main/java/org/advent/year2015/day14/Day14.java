package org.advent.year2015.day14;

import org.advent.common.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day14 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day14.class, "input.txt");
		List<Reindeer> reindeers = Utils.readLines(input).stream().map(Reindeer::parse).toList();
		
		System.out.println("Answer 1: " + part1(reindeers));
		System.out.println("Answer 2: " + part2(reindeers));
	}
	
	private static long part1(List<Reindeer> reindeers) {
		int seconds = 2503;
		return reindeers.stream().mapToInt(r -> r.distance(seconds)).max().orElseThrow();
	}
	
	private static long part2(List<Reindeer> reindeers) {
		int seconds = 2503;
		Map<String, Integer> distances = new HashMap<>();
		Map<String, Integer> points = new HashMap<>();
		for (Reindeer reindeer : reindeers) {
			distances.put(reindeer.name, 0);
			points.put(reindeer.name, 0);
		}
		for (int currentSecond = 0; currentSecond < seconds; currentSecond++) {
			int _currentSecond = currentSecond;
			for (Reindeer reindeer : reindeers)
				distances.computeIfPresent(reindeer.name, (k, v) -> v + reindeer.tick(_currentSecond));
			int maxDistance = distances.values().stream().mapToInt(v -> v).max().orElseThrow();
			for (Map.Entry<String, Integer> entry : distances.entrySet())
				if (entry.getValue() == maxDistance)
					points.computeIfPresent(entry.getKey(), (k, v) -> v + 1);
		}
		return points.values().stream().mapToInt(v -> v).max().orElseThrow();
	}
	
	record Reindeer(String name, int speed, int time, int restTime) {
		
		int distance(int seconds) {
			int fullCycles = seconds / (time + restTime);
			int leftoverSeconds = seconds % (time + restTime);
			return speed * (fullCycles * time + Math.min(time, leftoverSeconds));
		}
		
		int tick(int currentSecond) {
			return currentSecond % (time + restTime) < time ? speed : 0;
		}
		
		static Pattern pattern = Pattern.compile("(\\w+) can fly (\\d+) km/s for (\\d+) seconds, but then must rest for (\\d+) seconds\\.");
		static Reindeer parse(String line) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches())
				throw new IllegalArgumentException("Invalid line: " + line);
			int speed = Integer.parseInt(matcher.group(2));
			int time = Integer.parseInt(matcher.group(3));
			int restTime = Integer.parseInt(matcher.group(4));
			return new Reindeer(matcher.group(1), speed, time, restTime);
		}
	}
}