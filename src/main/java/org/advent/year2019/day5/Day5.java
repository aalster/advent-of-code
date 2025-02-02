package org.advent.year2019.day5;

import lombok.RequiredArgsConstructor;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day5 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day5()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 9961446, 742621)
		);
	}
	
	IntcodeComputer computer;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		computer = IntcodeComputer.parse(input.nextLine());
	}
	
	@Override
	public Object part1() {
		return computer.run(1).getLast();
	}
	
	@Override
	public Object part2() {
		return computer.run(5).getLast();
	}
	
	@RequiredArgsConstructor
	static class IntcodeComputer {
		final int[] program;
		
		void set(int index, int value) {
			program[index] = value;
		}
		
		int get(int parameter, int mode) {
			return mode == 0 ? program[parameter] : parameter;
		}
		
		List<Integer> run(int input) {
			List<Integer> output = new ArrayList<>();
			int index = 0;
			loop: while (0 <= index && index < program.length) {
				int operation = program[index];
				int opcode = operation % 100;
				operation = operation / 100;
				int modeLeft = operation % 10;
				operation = operation / 10;
				int modeRight = operation % 10;
//				operation = operation / 10;
//				int mode3 = operation % 10;
				
				switch (opcode) {
					case 1 -> {
						set(program[index + 3], get(program[index + 1], modeLeft) + get(program[index + 2], modeRight));
						index += 4;
					}
					case 2 -> {
						set(program[index + 3], get(program[index + 1], modeLeft) * get(program[index + 2], modeRight));
						index += 4;
					}
					case 3 -> {
						set(program[index + 1], input);
						index += 2;
					}
					case 4 -> {
						output.add(get(program[index + 1], modeLeft));
						index += 2;
					}
					case 5 -> {
						if (get(program[index + 1], modeLeft) != 0)
							index = get(program[index + 2], modeRight);
						else
							index += 3;
					}
					case 6 -> {
						if (get(program[index + 1], modeLeft) == 0)
							index = get(program[index + 2], modeRight);
						else
							index += 3;
					}
					case 7 -> {
						set(program[index + 3], get(program[index + 1], modeLeft) < get(program[index + 2], modeRight) ? 1 : 0);
						index += 4;
					}
					case 8 -> {
						set(program[index + 3], get(program[index + 1], modeLeft) == get(program[index + 2], modeRight) ? 1 : 0);
						index += 4;
					}
					case 99 -> {break loop;}
				}
			}
			return output;
		}
		
		IntcodeComputer copy() {
			return new IntcodeComputer(Arrays.copyOf(program, program.length));
		}
		
		static IntcodeComputer of(int[] program) {
			return new IntcodeComputer(program);
		}
		
		static IntcodeComputer parse(String line) {
			return IntcodeComputer.of(Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray());
		}
	}
}