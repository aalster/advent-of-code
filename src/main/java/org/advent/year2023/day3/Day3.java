package org.advent.year2023.day3;

import org.advent.common.DirectionExt;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day3 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day3.class, "input.txt");
		List<String> lines = new ArrayList<>();
		while (input.hasNext()) {
			lines.add(input.nextLine());
		}
		char[][] field = lines.stream().map(String::toCharArray).toArray(char[][]::new);
		
		System.out.println("Answer 1: " + part1(field));
		System.out.println("Answer 2: " + part2(field));
	}
	
	private static long part1(char[][] field) {
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
	
	private static long part2(char[][] field) {
		List<PartNumber> numbers = parseNumbers(field);
		List<Symbol> symbols = parseSymbols(field);
		
		return symbols.stream()
				.filter(s -> s.value() == '*')
				.map(s -> s.adjacentParts(numbers))
				.filter(parts -> parts.size() == 2)
				.mapToLong(parts -> parts.stream().mapToLong(PartNumber::value).reduce(1, (l, r) -> l * r))
				.sum();
	}
	
	private static List<PartNumber> parseNumbers(char[][] field) {
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
	
	private static List<Symbol> parseSymbols(char[][] field) {
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
	
	private record PartNumber(long value, Set<Point> position) {
		
		public boolean containsAny(Collection<Point> points) {
			return points.stream().anyMatch(position::contains);
		}
	}
	
	private record Symbol(char value, Point position) {
		
		public Stream<Point> adjacentPoints() {
			return Arrays.stream(DirectionExt.values()).map(d -> d.shift(position));
		}

		public List<PartNumber> adjacentParts(List<PartNumber> numbers) {
			Set<Point> adjacentPoints = adjacentPoints().collect(Collectors.toSet());
			return numbers.stream().filter(n -> n.containsAny(adjacentPoints)).toList();
		}
	}
}