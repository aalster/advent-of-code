package org.advent.year2018.day18;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day18 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day18()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 1147, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 583426, 169024)
		);
	}
	
	Acres acres;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		acres = Acres.parse(Utils.readLines(input));
	}
	
	@Override
	public Object part1() {
		for (int i = 0; i < 10; i++)
			acres.step();
		return acres.result();
	}
	
	@Override
	public Object part2() {
		int totalGenerations = 1000000000;
		Map<Object, Integer> previous = new HashMap<>();
		
		int generation = 0;
		while (true) {
			generation++;
			acres.step();
			Integer prevGeneration = previous.put(acres.key(), generation);
			if (prevGeneration != null) {
				totalGenerations = (totalGenerations - prevGeneration) % (generation - prevGeneration);
				break;
			}
		}
		
		while (totalGenerations-- > 0)
			acres.step();
		
		return acres.result();
	}
	
	static class Acres {
		final char[][] acresLeft;
		final char[][] acresRight;
		int step;
		
		Acres(char[][] acres) {
			acresLeft = acres;
			acresRight = new char[acres.length][];
			for (int i = 0; i < acres.length; i++)
				acresRight[i] = Arrays.copyOf(acresLeft[i], acresLeft[i].length);
		}
		
		char[][] acres() {
			return step % 2 == 0 ? acresLeft : acresRight;
		}
		
		void step() {
			char[][] from = acres();
			step++;
			char[][] to = acres();
			
			for (int y = 1; y < from.length - 1; y++) {
				for (int x = 1; x < from.length - 1; x++) {
					to[y][x] = switch (from[y][x]) {
						case '.' -> adjacentMatch(from, x, y, 3, 0) ? '|' : '.';
						case '|' -> adjacentMatch(from, x, y, 0, 3) ? '#' : '|';
						case '#' -> adjacentMatch(from, x, y, 1, 1) ? '#' : '.';
						default -> throw new IllegalStateException("Unexpected value: " + from[y][x]);
					};
				}
			}
		}
		
		boolean adjacentMatch(char[][] acres, int x, int y, int trees, int lumberyard) {
			char[] adjacent = new char[] {
					acres[y-1][x-1], acres[y-1][x], acres[y-1][x+1],
					acres[y][x-1], acres[y][x+1],
					acres[y+1][x-1], acres[y+1][x], acres[y+1][x+1]
			};
			for (char c : adjacent) {
				if (c == '|')
					trees--;
				else if (c == '#')
					lumberyard--;
				if (trees <= 0 && lumberyard <= 0)
					return true;
			}
			return false;
		}
		
		Object key() {
			char[][] acres = acres();
			return Arrays.stream(acres, 1, acres.length - 1)
					.map(row -> new String(row, 1, row.length - 1))
					.toList();
		}
		
		int result() {
			int trees = 0;
			int lumberyard = 0;
			for (char[] row : acres()) {
				for (char c : row) {
					if (c == '|')
						trees++;
					else if (c == '#')
						lumberyard++;
				}
			}
			return trees * lumberyard;
		}
		
		static Acres parse(List<String> lines) {
			char[][] acres = new char[lines.size() + 2][lines.getFirst().length() + 2];
			Arrays.fill(acres[0], ' ');
			int y = 1;
			for (String line : lines) {
				acres[y][0] = ' ';
				System.arraycopy(line.toCharArray(), 0, acres[y], 1, line.length());
				acres[y][line.length() + 1] = ' ';
				y++;
			}
			Arrays.fill(acres[y], ' ');
			return new Acres(acres);
		}
	}
}