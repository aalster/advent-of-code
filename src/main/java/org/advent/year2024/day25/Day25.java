package org.advent.year2024.day25;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day25 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day25.class, "input.txt");
		List<Pins> locks = new ArrayList<>();
		List<Pins> keys = new ArrayList<>();
		
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
		
		System.out.println("Answer: " + part1(locks, keys));
	}
	
	private static long part1(List<Pins> locks, List<Pins> keys) {
		return locks.stream().flatMap(lock -> keys.stream().map(lock::fits)).filter(f -> f).count();
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