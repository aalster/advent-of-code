package org.advent.year2019.intcode_computer;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class IntcodeComputer {
	private static final boolean debug = true;
	public enum State {
		RUNNING, HALTED, WAITING_INPUT
	}
	
	private int[] program;
	private int index = 0;
	private int relativeBase = 0;
	private State state = State.RUNNING;
	
	public IntcodeComputer(int[] program) {
		this.program = program;
	}
	
	public IntcodeComputer copy() {
		return new IntcodeComputer(Arrays.copyOf(program, program.length));
	}
	
	public void set(int index, int value) {
		expand(index);
		program[index] = value;
	}
	
	public int get(int parameter, int mode) {
		return switch (mode) {
			case 0 -> {
				expand(parameter);
				yield program[parameter];
			}
			case 1 -> parameter;
			case 2 -> {
				expand(relativeBase + parameter);
				yield program[relativeBase + parameter];
			}
			default -> throw new IllegalStateException("Unknown mode: " + mode);
		};
	}
	
	private void expand(int index) {
		if (program.length <= index)
			program = Arrays.copyOf(program, index + 1);
	}
	
	public Integer runUntilOutput(InputProvider input) {
		if (state == State.HALTED)
			return null;
		if (state == State.WAITING_INPUT && !input.hasNext())
			return null;
		
		loop: while (0 <= index && index < program.length) {
			int operation = program[index];
			int opcode = operation % 100;
			operation = operation / 100;
			int modeLeft = operation % 10;
			operation = operation / 10;
			int modeRight = operation % 10;
			
			if (debug) {
				IntcodeOperation operationDescription = operations.get(opcode);
				System.out.println(operationDescription.name + ": "
						+ Arrays.toString(Arrays.copyOfRange(program, index, index + operationDescription.length)));
			}
			
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
					if (!input.hasNext()) {
						state = State.WAITING_INPUT;
						return null;
					}
					set(program[index + 1], input.nextInput());
					index += 2;
				}
				case 4 -> {
					int output = get(program[index + 1], modeLeft);
					index += 2;
					return output;
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
				case 9 -> {
					relativeBase += get(program[index + 1], modeLeft);
					index += 2;
				}
				case 99 -> {
					break loop;
				}
			}
		}
		state = State.HALTED;
		return null;
	}
	
	public List<Integer> run(InputProvider input) {
		List<Integer> output = new ArrayList<>();
		while (true) {
			Integer value = runUntilOutput(input);
			if (value == null)
				break;
			output.add(value);
		}
		return output;
	}
	
	public static IntcodeComputer parse(String line) {
		return new IntcodeComputer(Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray());
	}
	
	record IntcodeOperation(int opcode, int length, String name, String description) {
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