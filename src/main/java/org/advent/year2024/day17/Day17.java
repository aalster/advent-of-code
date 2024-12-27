package org.advent.year2024.day17;

import lombok.ToString;
import org.advent.common.Utils;
import org.advent.runner.AbstractDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day17 extends AbstractDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day17()).run("input.txt");
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "4,6,3,5,6,3,5,2,1,0", ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 117440),
				new ExpectedAnswers("input.txt", "1,6,3,6,5,6,5,1,7", 247839653009594L)
		);
	}
	
	Map<String, Long> initialRegisters = new HashMap<>();
	int[] program;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty())
				break;
			String[] split = line.replace("Register ", "").split(": ");
			initialRegisters.put(split[0], Long.parseLong(split[1]));
		}
		program = Arrays.stream(input.nextLine().replace("Program: ", "").split(","))
				.mapToInt(Integer::parseInt).toArray();
	}
	
	@Override
	public Object part1() {
		return run(initialRegisters, program, false).stream()
				.map(String::valueOf)
				.collect(Collectors.joining(","));
	}
	
	@Override
	public Object part2() {
		/*
			2,4,  1,1,  7,5,  4,0,  0,3,  1,6,  5,5,  3,0
			
			2,4 | B = A % 8
			1,1 | B = B ^ 1
			7,5 | C = A >> B
			4,0 | B = B ^ C
			0,3 | A = A / 8
			1,6 | B = B ^ 6
			5,5 | OUT: B % 8
			3,0 | JUMP 0 if A > 0
			
			while (A > 0) {
				B = A % 8 ^ 1;
				C = A >> B;
				B = B ^ C ^ 6;
				OUT(B % 8);
				A = A >> 3;
			}
			
			pow(8, 15) < A < pow(8, 16) => 35184372088832 < A < 281474976710656
			DIGIT = ((((A % 8) ^ 1) ^ (A >> ((A % 8) ^ 1))) ^ 6) % 8
		 */
		return find(program, program, 0);
	}
	
	List<Long> run(Map<String, Long> initialRegisters, int[] program, boolean onlyFirstDigit) {
		Registers reg = new Registers(initialRegisters);
		
		List<Long> output = new ArrayList<>();
		int index = 0;
		while (index < program.length - 1) {
			int opcode = program[index];
			int operand = program[index + 1];
			switch (opcode) {
				case 0 -> reg.A = reg.A >> reg.comboValue(operand);
				case 1 -> reg.B ^= operand;
				case 2 -> reg.B = reg.comboValue(operand) % 8;
				case 3 -> {
					if (reg.A != 0) {
						index = operand;
						continue;
					}
				}
				case 4 -> reg.B ^= reg.C;
				case 5 -> output.add(reg.comboValue(operand) % 8);
				case 6 -> reg.B = reg.A >> reg.comboValue(operand);
				case 7 -> reg.C = reg.A >> reg.comboValue(operand);
			}
			if (onlyFirstDigit && !output.isEmpty())
				return output;
			index += 2;
		}
		
		return output;
	}
	
	long find(int[] program, int[] targetOutput, long subAnswer) {
		if (targetOutput.length == 0)
			return subAnswer;
		
		int[] nextTargetOutput = Arrays.copyOfRange(targetOutput, 0, targetOutput.length - 1);
		
		for (int b = 0; b < 8; b++) {
			long a = (subAnswer << 3) + b;
			List<Long> answer = run(Map.of("A", a), program, true);
			if (answer.getFirst() == targetOutput[targetOutput.length - 1]) {
				long next = find(program, nextTargetOutput, a);
				if (next >= 0)
					return next;
			}
		}
		return -1;
	}
	
	@ToString
	static class Registers {
		long A;
		long B;
		long C;
		
		public Registers(Map<String, Long> initialRegisters) {
			A = initialRegisters.getOrDefault("A", 0L);
			B = initialRegisters.getOrDefault("B", 0L);
			C = initialRegisters.getOrDefault("C", 0L);
		}
		
		long comboValue(int n) {
			return switch (n) {
				case 0, 1, 2, 3 -> n;
				case 4 -> A;
				case 5 -> B;
				case 6 -> C;
				default -> throw new IllegalStateException("Unexpected value: " + n);
			};
		}
	}
}