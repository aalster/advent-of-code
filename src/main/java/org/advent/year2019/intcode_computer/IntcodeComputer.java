package org.advent.year2019.intcode_computer;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IntcodeComputer {
	private static final boolean inspectOperations = false;
	
	private long[] program;
	private final InputProvider inputProvider;
	private final OutputConsumer outputConsumer;
	
	private int index = 0;
	private int relativeBase = 0;
	
	public IntcodeComputer(long[] program, InputProvider inputProvider, OutputConsumer outputConsumer) {
		this.program = Arrays.copyOf(program, program.length);
		this.inputProvider = inputProvider;
		this.outputConsumer = outputConsumer;
	}
	
	private void set(int index, int mode, long value) {
		index = (int) program[index];
		index += mode == 2 ? relativeBase : 0;
		expand(index);
		program[index] = value;
	}
	
	private long get(int index, int mode) {
		long parameter = program[index];
		return switch (mode) {
			case 0 -> {
				expand((int) parameter);
				yield program[(int) parameter];
			}
			case 1 -> parameter;
			case 2 -> {
				expand((int) (relativeBase + parameter));
				yield program[(int) (relativeBase + parameter)];
			}
			default -> throw new IllegalStateException("Unknown mode: " + mode);
		};
	}
	
	private void expand(int index) {
		if (program.length <= index)
			program = Arrays.copyOf(program, index + 1);
	}
	
	public void run() {
		while (0 <= index && index < program.length) {
			long operation = program[index];
			int opcode = (int) (operation % 100);
			operation /= 100;
			int modeLeft = (int) (operation % 10);
			operation /= 10;
			int modeRight = (int) (operation % 10);
			operation /= 10;
			int modeResult = (int) (operation % 10);
			
			if (inspectOperations)
				System.out.println(operations.get(opcode).inspect(program, index));
			
			switch (opcode) {
				case 1 -> {
					set(index + 3, modeResult, get(index + 1, modeLeft) + get(index + 2, modeRight));
					index += 4;
				}
				case 2 -> {
					set(index + 3, modeResult, get(index + 1, modeLeft) * get(index + 2, modeRight));
					index += 4;
				}
				case 3 -> {
					if (!inputProvider.hasNext())
						return;
					set(index + 1, modeLeft, inputProvider.nextInput());
					index += 2;
				}
				case 4 -> {
					long output = get(index + 1, modeLeft);
					outputConsumer.accept(output);
					index += 2;
				}
				case 5 -> {
					if (get(index + 1, modeLeft) != 0)
						index = (int) get(index + 2, modeRight);
					else
						index += 3;
				}
				case 6 -> {
					if (get(index + 1, modeLeft) == 0)
						index = (int) get(index + 2, modeRight);
					else
						index += 3;
				}
				case 7 -> {
					set(index + 3, modeResult, get(index + 1, modeLeft) < get(index + 2, modeRight) ? 1 : 0);
					index += 4;
				}
				case 8 -> {
					set(index + 3, modeResult, get(index + 1, modeLeft) == get(index + 2, modeRight) ? 1 : 0);
					index += 4;
				}
				case 9 -> {
					relativeBase += (int) get(index + 1, modeLeft);
					index += 2;
				}
				case 99 -> {
					return;
				}
			}
		}
	}
	
	public String toString() {
		return Arrays.stream(program).mapToObj(String::valueOf).collect(Collectors.joining(","));
	}
	
	public static long[] parseProgram(String line) {
		return Arrays.stream(line.split(",")).mapToLong(Long::parseLong).toArray();
	}
	
	record IntcodeOperation(int opcode, int length, String name, String description) {
		String inspect(long[] program, int index) {
			return name + ": " + Arrays.toString(Arrays.copyOfRange(program, index, index + length));
		}
	}
	
	private static final Map<Integer, IntcodeOperation> operations = Stream.of(
			new IntcodeOperation(1, 4, "add", "Opcode 1 adds together" +
					" numbers read from two positions and stores the result in a third position. The three integers" +
					" immediately after the opcode tell you these three positions - the first two indicate the positions" +
					" from which you should read the input values, and the third indicates the position at which" +
					" the output should be stored."),
			
			new IntcodeOperation(2, 4, "mul", "Opcode 2 works exactly like opcode 1," +
					" except it multiplies the two inputs instead of adding them. Again, the three integers after" +
					" the opcode indicate where the inputs and outputs are, not their values."),
			
			new IntcodeOperation(3, 2, "input", "Opcode 3 takes a single integer as input" +
					" and saves it to the position given by its only parameter. For example, the instruction 3,50 would" +
					" take an input value and store it at address 50."),
			
			new IntcodeOperation(4, 2, "output", "Opcode 4 outputs the value of its only" +
					" parameter. For example, the instruction 4,50 would output the value at address 50."),
			
			new IntcodeOperation(5, 3, "jump-if-true", "Opcode 5 is jump-if-true:" +
					" if the first parameter is non-zero, it sets the instruction pointer to the value from the" +
					" second parameter. Otherwise, it does nothing."),
			
			new IntcodeOperation(6, 3, "jump-if-false", "Opcode 6 is jump-if-false:" +
					" if the first parameter is zero, it sets the instruction pointer to the value from the" +
					" second parameter. Otherwise, it does nothing."),
			
			new IntcodeOperation(7, 4, "less-than", "Opcode 7 is less than: if the" +
					" first parameter is less than the second parameter, it stores 1 in the position given by the" +
					" third parameter. Otherwise, it stores 0."),
			
			new IntcodeOperation(8, 4, "equals", "Opcode 8 is equals: if the first" +
					" parameter is equal to the second parameter, it stores 1 in the position given by the third" +
					" parameter. Otherwise, it stores 0."),
			
			new IntcodeOperation(9, 2, "relative-base", "Opcode 9 adjusts the relative base" +
					" by the value of its only parameter. The relative base increases (or decreases, if the value is" +
					" negative) by the value of the parameter."),
			
			new IntcodeOperation(99, 1, "halt", "99 means that the program is finished" +
					" and should immediately halt.")
	).collect(Collectors.toMap(IntcodeOperation::opcode, op -> op));
}