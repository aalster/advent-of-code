package org.advent.year2024.day25;

import org.advent.common.Utils;
import org.advent.runner.AbstractDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day25 extends AbstractDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day25()).run("example.txt");
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 3, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 3255, ExpectedAnswers.IGNORE)
		);
	}
	
	List<Pins> locks = new ArrayList<>();
	List<Pins> keys = new ArrayList<>();
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		
		List<String> lines = new ArrayList<>();
		while (input.hasNext()) {
			String line = input.nextLine();
			if (!line.isEmpty()) {
				lines.add(line);
				continue;
			}
			(lines.getFirst().startsWith("#") ? locks : keys).add(Pins.parse(lines));
			lines = new ArrayList<>();
		}
		(lines.getFirst().startsWith("#") ? locks : keys).add(Pins.parse(lines));
	}
	
	@Override
	public Object part1() {
		return locks.stream().flatMap(lock -> keys.stream().map(lock::fits)).filter(f -> f).count();
	}
	
	@Override
	public Object part2() {
		return null;
	}
	
	record Pins(int[] pins) {
		
		boolean fits(Pins other) {
			for (int i = 0; i < pins.length; i++)
				if (pins[i] + other.pins[i] > 5)
					return false;
			return true;
		}
		
		static Pins parse(List<String> lines) {
			int[] pins = new int[] {-1, -1, -1, -1, -1};
			for (String line : lines) {
				char[] chars = line.toCharArray();
				for (int i = 0; i < chars.length; i++)
					pins[i] += chars[i] == '#' ? 1 : 0;
			}
			return new Pins(pins);
		}
	}
}