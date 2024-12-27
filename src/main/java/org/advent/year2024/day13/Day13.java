package org.advent.year2024.day13;

import org.advent.common.Utils;
import org.advent.runner.AbstractDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day13 extends AbstractDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day13()).run("input.txt");
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 480, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 40069, 71493195288102L)
		);
	}
	
	List<Machine> machines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		machines = new ArrayList<>();
		while (input.hasNext())
			machines.add(Machine.parse(input));
	}
	
	@Override
	public Object part1() {
		return machines.stream().mapToLong(Machine::winCost).sum();
	}
	
	@Override
	public Object part2() {
		LongPoint prizeShift = new LongPoint(10000000000000L, 10000000000000L);
		return machines.stream()
				.map(m -> new Machine(m.a, m.b, prizeShift.add(m.prize)))
				.mapToLong(Machine::winCost)
				.sum();
	}
	
	record Machine(LongPoint a, LongPoint b, LongPoint prize) {
		
		long winCost() {
			// aPresses = (px * by - bx * py) / (ax * by - bx * ay)
			// bPresses = (px - aPresses * ax) / bx
			long aNumerator = prize.x * b.y - b.x * prize.y;
			long aDenominator = a.x * b.y - b.x * a.y;
			if (aDenominator == 0 || aNumerator % aDenominator != 0)
				return 0;
			long aPresses = aNumerator / aDenominator;
			
			long bNumerator = prize.x - aPresses * a.x;
			if (bNumerator % b.x != 0)
				return 0;
			long bPresses = bNumerator / b.x;
			return aPresses * 3 + bPresses;
		}
		
		static Machine parse(Scanner input) {
			return new Machine(parsePoint(input), parsePoint(input), parsePoint(input));
		}
		
		private static LongPoint parsePoint(Scanner input) {
			String line = input.nextLine();
			if (line.isEmpty())
				line = input.nextLine();
			String[] split = line.split(": ")[1]
					.replace("+", ", ").replace("=", ", ")
					.split(", ");
			return new LongPoint(Long.parseLong(split[1]), Long.parseLong(split[3]));
		}
	}
	
	record LongPoint(long x, long y) {
		
		LongPoint add(LongPoint other) {
			return new LongPoint(x + other.x, y + other.y);
		}
	}
}