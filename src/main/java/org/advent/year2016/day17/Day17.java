package org.advent.year2016.day17;

import lombok.SneakyThrows;
import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day17 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day17()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "DDRRRD", 370),
				new ExpectedAnswers("example2.txt", "DDUDRLRRUDRD", 492),
				new ExpectedAnswers("example3.txt", "DRURDRUDDLLDLUURRDULRLDUUDDDRR", 830),
				new ExpectedAnswers("input.txt", "RDURRDDLRD", 526)
		);
	}
	
	static MessageDigest digest;
	static {
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	String passcode;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		passcode = input.nextLine();
	}
	
	@Override
	public Object part1() {
		List<State> states = List.of(new State(passcode, "", new Point(0, 0)));
		Point end = new Point(3, 3);
		while (!states.isEmpty()) {
			states = states.stream().flatMap(State::next).toList();
			Optional<State> finished = states.stream().filter(s -> s.position.equals(end)).findAny();
			if (finished.isPresent())
				return finished.get().path;
		}
		return "";
	}
	
	@Override
	public Object part2() {
		List<State> states = List.of(new State(passcode, "", new Point(0, 0)));
		Point end = new Point(3, 3);
		int maxPath = 0;
		while (!states.isEmpty()) {
			states = states.stream().flatMap(State::next).toList();
			Map<Boolean, List<State>> split = states.stream().collect(Collectors.groupingBy(s -> s.position.equals(end)));
			states = split.getOrDefault(false, List.of());
			List<State> finished = split.getOrDefault(true, List.of());
			if (!finished.isEmpty())
				maxPath = finished.getFirst().path.length();
		}
		return maxPath;
	}
	
	record State(String passcode, String path, Point position) {
		
		Stream<State> next() {
			return openDirections()
					.flatMap(d -> {
						Point nextPosition = position.shift(d);
						if (nextPosition.x() < 0 || nextPosition.y() < 0 || 3 < nextPosition.x() || 3 < nextPosition.y())
							return Stream.empty();
						return Stream.of(new State(passcode, path + d.name().charAt(0), nextPosition));
					});
		}
		
		Stream<Direction> openDirections() {
			char[] hashChars = hash().substring(0, 4).toCharArray();
			List<Direction> directions = new ArrayList<>();
			if (hashChars[0] >= 'b')
				directions.add(Direction.UP);
			if (hashChars[1] >= 'b')
				directions.add(Direction.DOWN);
			if (hashChars[2] >= 'b')
				directions.add(Direction.LEFT);
			if (hashChars[3] >= 'b')
				directions.add(Direction.RIGHT);
			return directions.stream();
		}
		@SneakyThrows
		String hash() {
			return HexFormat.of().formatHex(digest.digest((passcode + path).getBytes()));
		}
	}
}