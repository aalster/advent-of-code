package org.advent.year2015.day6;

import lombok.RequiredArgsConstructor;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day6 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day6()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 7, 23),
				new ExpectedAnswers("input.txt", 543903, 14687245)
		);
	}
	
	List<Instruction> instructions;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		instructions = Utils.readLines(input).stream().map(Instruction::parse).toList();
	}
	
	@Override
	public Object part1() {
		List<Instruction> reversedInstructions = instructions.reversed();
		int lights = 0;
		for (int y = 0; y < 1000; y++) {
			nextLight: for (int x = 0; x < 1000; x++) {
				boolean toggle = false;
				for (Instruction instruction : reversedInstructions) {
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
	
	@Override
	public Object part2() {
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