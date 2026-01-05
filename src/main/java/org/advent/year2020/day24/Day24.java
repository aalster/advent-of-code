package org.advent.year2020.day24;

import lombok.RequiredArgsConstructor;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day24 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day24()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 10, 2208),
				new ExpectedAnswers("input.txt", 269, 3667)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
	}
	
	@Override
	public Object part1() {
		return blackTiles(lines).size();
	}
	
	@Override
	public Object part2() {
		Set<Tile> black = blackTiles(lines);
		for (int i = 0; i < 100; i++) {
			Set<Tile> nextPossible = new HashSet<>(black.size() * 4);
			nextPossible.addAll(black);
			black.stream().flatMap(Tile::neighbors).forEach(nextPossible::add);
			
			Set<Tile> next = new HashSet<>(nextPossible.size());
			for (Tile tile : nextPossible) {
				long count = tile.neighbors().filter(black::contains).count();
				if (count == 2 || (count == 1 && black.contains(tile)))
					next.add(tile);
			}
			black = next;
		}
		return black.size();
	}
	
	Set<Tile> blackTiles(List<String> lines) {
		Map<Tile, Integer> tiles = new HashMap<>();
		lines.stream().map(Tile::parse).forEach(t -> tiles.merge(t, 1, Integer::sum));
		return tiles.entrySet().stream()
				.filter(e -> e.getValue() % 2 == 1)
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());
	}
	
	interface Direction {
		int NW = 0;
		int W = 1;
		int SW = 2;
		int SE = 3;
		int E = 4;
		int NE = 5;
		
		static int read(CharIterator iterator) {
			return switch (iterator.next()) {
				case 'w' -> Direction.W;
				case 'e' -> Direction.E;
				case 'n' -> switch (iterator.next()) {
					case 'w' -> Direction.NW;
					case 'e' -> Direction.NE;
					default -> throw new IllegalStateException("Unexpected value");
				};
				case 's' -> switch (iterator.next()) {
					case 'w' -> Direction.SW;
					case 'e' -> Direction.SE;
					default -> throw new IllegalStateException("Unexpected value");
				};
				default -> throw new IllegalStateException("Unexpected value");
			};
		}
	}
	
	record Tile(int[] path) {
		
		Tile move(int direction) {
			int[] result = Arrays.copyOf(path, path.length);
			//noinspection StatementWithEmptyBody
			if (decrease(result, direction + 3)) {
				// nothing
			} else if (decrease(result, direction + 2)) {
				result[Math.floorMod(direction + 1, result.length)]++;
			} else if (decrease(result, direction - 2)) {
				result[Math.floorMod(direction - 1, result.length)]++;
			} else {
				result[direction]++;
			}
			return new Tile(result);
		}
		
		Stream<Tile> neighbors() {
			return IntStream.range(0, 6).mapToObj(this::move);
		}
		
		@Override
		public int hashCode() {
			return Arrays.hashCode(path);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Tile))
				return false;
			return Arrays.equals(path, ((Tile) obj).path);
		}
		
		static boolean decrease(int[] array, int index) {
			index = Math.floorMod(index, array.length);
			if (array[index] <= 0)
				return false;
			array[index]--;
			return true;
		}
		
		static Tile parse(String line) {
			CharIterator iterator = new CharIterator(line);
			Tile tile = new Tile(new int[6]);
			while (iterator.hasNext())
				tile = tile.move(Direction.read(iterator));
			return tile;
		}
	}
	
	@RequiredArgsConstructor
	static class CharIterator {
		final String str;
		int index;
		
		char next() {
			return str.charAt(index++);
		}
		
		boolean hasNext() {
			return index < str.length();
		}
	}
}