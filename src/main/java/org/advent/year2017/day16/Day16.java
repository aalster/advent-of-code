package org.advent.year2017.day16;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day16 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day16()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "baedc", ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", "ociedpjbmfnkhlga", "gnflbkojhicpmead")
		);
	}
	
	List<Move> moves;
	int size;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		moves = Arrays.stream(input.nextLine().split(",")).map(Move::parse).toList();
		size = switch (file) {
			case "example.txt" -> 5;
			case "input.txt" -> 16;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		return new String(dance(createPrograms(size), moves));
	}
	
	@Override
	public Object part2() {
		char[] start = createPrograms(size);
		
		char[] next = dance(Arrays.copyOf(start, size), moves);
		int loop = 1;
		while (!Arrays.equals(start, next)) {
			next = dance(next, moves);
			loop++;
		}
		int leftover = 1_000_000_000 % loop;
		while (leftover-- > 0)
			next = dance(next, moves);
		return new String(next);
	}
	
	private char[] createPrograms(int size) {
		char[] programs = new char[size];
		for (int i = 0; i < size; i++)
			programs[i] = (char) ('a' + i);
		return programs;
	}
	
	char[] dance(char[] programs, List<Move> moves) {
		int totalSpin = 0;
		for (Move move : moves)
			totalSpin = move.apply(totalSpin, programs);
		
		totalSpin = Math.floorMod(totalSpin, size) - size;
		char[] afterSpin = new char[size];
		for (int i = 0; i < size; i++)
			afterSpin[i] = programs[(i - totalSpin) % size];
		return afterSpin;
	}
	
	interface Move {
		int apply(int totalSpin, char[] programs);
		
		static Move parse(String move) {
			return switch (move.charAt(0)) {
				case 's' -> new SpinMove(Integer.parseInt(move.substring(1)));
				case 'x' -> {
					String[] split = move.substring(1).split("/");
					yield new ExchangeMove(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
				}
				case 'p' -> new PartnerMove(move.charAt(1), move.charAt(3));
				default -> throw new IllegalStateException("Unexpected value: " + move);
			};
		}
	}
	
	record SpinMove(int spin) implements Move {
		@Override
		public int apply(int totalSpin, char[] programs) {
			return totalSpin + spin;
		}
	}
	
	record ExchangeMove(int left, int right) implements Move {
		@Override
		public int apply(int totalSpin, char[] programs) {
			int spinnedLeft = Math.floorMod(left - totalSpin, programs.length);
			int spinnedRight = Math.floorMod(right - totalSpin, programs.length);
			char temp = programs[spinnedLeft];
			programs[spinnedLeft] = programs[spinnedRight];
			programs[spinnedRight] = temp;
			return totalSpin;
		}
	}
	
	record PartnerMove(char left, char right) implements Move {
		@Override
		public int apply(int totalSpin, char[] programs) {
			int leftIndex = ArrayUtils.indexOf(programs, left);
			int rightIndex = ArrayUtils.indexOf(programs, right);
			programs[leftIndex] = right;
			programs[rightIndex] = left;
			return totalSpin;
		}
	}
}