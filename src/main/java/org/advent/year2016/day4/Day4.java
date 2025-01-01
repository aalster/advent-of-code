package org.advent.year2016.day4;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day4 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day4()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 1514, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 278221, 267)
		);
	}
	
	List<Room> rooms;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		rooms = Utils.readLines(input).stream().map(Room::parse).toList();
	}
	
	@Override
	public Object part1() {
		return rooms.stream().filter(Room::valid).mapToInt(Room::sectorId).sum();
	}
	
	@Override
	public Object part2() {
		return rooms.stream()
				.filter(r -> r.rotatedName().contains("northpole"))
				.mapToInt(Room::sectorId)
				.findFirst().orElse(0);
	}
	
	record Room(String name, int sectorId, String hash) {
		
		boolean valid() {
			return hash.equals(expectedHash());
		}
		
		String rotatedName() {
			StringBuilder result = new StringBuilder(name.length());
			for (char c : name.toCharArray()) {
				if (c == '-')
					result.append(" ");
				else
					result.append((char) ((c - 'a' + sectorId) % ('z' - 'a' + 1) + 'a'));
			}
			return result.toString();
		}
		
		private String expectedHash() {
			Map<Integer, Long> charStats = Arrays.stream(name.split("-"))
					.flatMap(s -> s.chars().boxed())
					.collect(Collectors.groupingBy(c -> c, Collectors.counting()));
			return charStats.entrySet().stream()
					.sorted(Map.Entry.<Integer, Long>comparingByValue().reversed().thenComparing(Map.Entry::getKey))
					.limit(5)
					.map(e -> "" + (char) (int) e.getKey())
					.collect(Collectors.joining());
		}
		
		static Pattern pattern = Pattern.compile("([a-z-]+)(\\d+)\\[([a-z]{5})]");
		static Room parse(String line) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.find())
				return new Room(matcher.group(1), Integer.parseInt(matcher.group(2)), matcher.group(3));
			throw new IllegalArgumentException("Invalid line: " + line);
		}
	}
}