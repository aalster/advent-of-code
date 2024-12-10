package org.advent.year2015.day6;

import lombok.RequiredArgsConstructor;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;

import java.util.List;
import java.util.Scanner;

public class Day6 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day6.class, "input.txt");
		List<Instruction> instructions = Utils.readLines(input).stream().map(Instruction::parse).toList();
		
		System.out.println("Answer 1: " + part1(instructions));
		System.out.println("Answer 2: " + part2(instructions));
	}
	
	private static long part1(List<Instruction> instructions) {
		instructions = instructions.reversed();
		int lights = 0;
		for (int y = 0; y < 1000; y++) {
			nextLight: for (int x = 0; x < 1000; x++) {
				boolean toggle = false;
				for (Instruction instruction : instructions) {
					if (!instruction.rect.containsInclusive(x, y))
						continue;
					switch (instruction.light) {
						case TOGGLE -> toggle = !toggle;
						case ON -> {
							if (!toggle)
								lights++;
							continue nextLight;
						}
						case OFF -> {
							if (toggle)
								lights++;
							continue nextLight;
						}
					}
				}
				if (toggle)
					lights++;
			}
		}
		return lights;
	}
	
	private static long part2(List<Instruction> instructions) {
		int totalBrightness = 0;
		for (int y = 0; y < 1000; y++) {
			for (int x = 0; x < 1000; x++) {
				int brightness = 0;
				for (Instruction instruction : instructions)
					if (instruction.rect.containsInclusive(x, y))
						brightness = Math.max(brightness + instruction.light.value, 0);
				totalBrightness += brightness;
			}
		}
		return totalBrightness;
	}
	
	record Instruction(Light light, Rect rect) {
		
		static Instruction parse(String line) {
			Light light = Light.parse(line);
			String[] split = line.replace(light.prefix + " ", "").split(" through ");
			return new Instruction(light, new Rect(Point.parse(split[0]), Point.parse(split[1])));
		}
	}
	
	@RequiredArgsConstructor
	enum Light {
		ON("turn on", 1),
		OFF("turn off", -1),
		TOGGLE("toggle", 2);
		
		final String prefix;
		final int value;
		
		static Light parse(String line) {
			for (Light value : values())
				if (line.startsWith(value.prefix))
					return value;
			throw new IllegalArgumentException();
		}
	}
}