package org.advent.year2023.day3;

import org.advent.common.DirectionExt;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day3 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day3()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 4361, 467835),
				new ExpectedAnswers("input.txt", 554003, 87263515)
		);
	}
	
	char[][] field;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		field = Utils.readLines(input).stream().map(String::toCharArray).toArray(char[][]::new);
	}
	
	@Override
	public Object part1() {
		List<PartNumber> numbers = parseNumbers(field);
		List<Symbol> symbols = parseSymbols(field);
		
		Set<Point> adjacentToSymbols = symbols.stream()
				.flatMap(Symbol::adjacentPoints)
				.collect(Collectors.toSet());
		
		return numbers.stream()
				.filter(n -> n.containsAny(adjacentToSymbols))
				.mapToLong(PartNumber::value)
				.sum();
	}
	
	@Override
	public Object part2() {
		List<PartNumber> numbers = parseNumbers(field);
		List<Symbol> symbols = parseSymbols(field);
		
		return symbols.stream()
				.filter(s -> s.value() == '*')
				.map(s -> s.adjacentParts(numbers))
				.filter(parts -> parts.size() == 2)
				.mapToLong(parts -> parts.stream().mapToLong(PartNumber::value).reduce(1, (l, r) -> l * r))
				.sum();
	}
	
	List<PartNumber> parseNumbers(char[][] field) {
		List<PartNumber> numbers = new ArrayList<>();
		
		for (int y = 0; y < field.length; y++) {
			char[] chars = field[y];
			
			long current = 0;
			Set<Point> currentPos = new HashSet<>();
			for (int x = 0; x < chars.length; x++) {
				char character = chars[x];
				if (Character.isDigit(character)) {
					current = current * 10 + (character - '0');
					currentPos.add(new Point(x, y));
				} else {
					if (current > 0) {
						numbers.add(new PartNumber(current, currentPos));
						current = 0;
						currentPos = new HashSet<>();
					}
				}
			}
			if (current > 0) {
				numbers.add(new PartNumber(current, currentPos));
			}
		}
		return numbers;
	}
	
	List<Symbol> parseSymbols(char[][] field) {
		List<Symbol> symbols = new ArrayList<>();
		
		for (int y = 0; y < field.length; y++) {
			char[] chars = field[y];
			
			for (int x = 0; x < chars.length; x++) {
				char character = chars[x];
				if (!Character.isDigit(character) && character != '.')
					symbols.add(new Symbol(character, new Point(x, y)));
			}
		}
		return symbols;
	}
	
	record PartNumber(long value, Set<Point> position) {
		
		boolean containsAny(Collection<Point> points) {
			return points.stream().anyMatch(position::contains);
		}
	}
	
	record Symbol(char value, Point position) {
		
		Stream<Point> adjacentPoints() {
			return Arrays.stream(DirectionExt.values()).map(d -> d.shift(position));
		}

		List<PartNumber> adjacentParts(List<PartNumber> numbers) {
			Set<Point> adjacentPoints = adjacentPoints().collect(Collectors.toSet());
			return numbers.stream().filter(n -> n.containsAny(adjacentPoints)).toList();
		}
	}
}