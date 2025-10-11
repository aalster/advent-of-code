package org.advent.year2019.day22;

import lombok.RequiredArgsConstructor;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day22 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day22()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "0 3 6 9 2 5 8 1 4 7", ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", "3 0 7 4 1 8 5 2 9 6", ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", "9 2 5 8 1 4 7 0 3 6", ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 3749, null)
		);
	}
	
	List<Operation> operations;
	boolean example;
	Map<Long, Long> cache;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		operations = Utils.readLines(input).stream().map(Operation::parse).toList();
		example = file.startsWith("example");
		cache = new HashMap<>();
	}
	
	@Override
	public Object part1() {
		if (example) {
			int[] result = new int[10];
			for (int i = 0; i < result.length; i++)
				result[(int) nextIndex(operations, i, result.length)] = i;
			return IntStream.of(result).mapToObj(String::valueOf).collect(Collectors.joining(" "));
		}
		return nextIndex(operations, 2019, 10007);
	}
	
	@Override
	public Object part2() {
		long size = 119315717514047L;
		long index = 2020;
		for (long i = 0; i < 101741582076661L; i++) {
			index = nextIndex(operations, index, size);
			if (i % 1000000 == 0)
				System.out.println(i + " " + index);
		}
		System.out.println("Result: " + index);
		return index;
	}
	
	long nextIndex(List<Operation> operations, long index, long size) {
		for (Operation operation : operations)
			index = nextIndex(operation, index, size);
		return index;
	}
	
	long nextIndex(Operation operation, long index, long size) {
		return switch (operation.type) {
			case NEW_STACK -> size - index - 1;
//			case INCREMENT -> BigInteger.valueOf(index).multiply(BigInteger.valueOf(operation.value)).mod(BigInteger.valueOf(size)).longValueExact();
			case INCREMENT -> index * operation.value % size;
//			case CUT -> BigInteger.valueOf(index).subtract(BigInteger.valueOf(operation.value)).mod(BigInteger.valueOf(size)).longValueExact();
			case CUT -> Math.floorMod(index - operation.value,  size);
		};
	}
	
	@RequiredArgsConstructor
	enum OperationType {
		NEW_STACK("deal into new stack"),
		INCREMENT("deal with increment"),
		CUT("cut");
		
		private final String prefix;
		
		static OperationType parse(String line) {
			for (OperationType value : values())
				if (line.startsWith(value.prefix))
					return value;
			throw new IllegalArgumentException();
		}
	}
	
	record Operation(OperationType type, int value) {
		static Operation parse(String line) {
			OperationType type = OperationType.parse(line);
			int value = type == OperationType.NEW_STACK ? 0 : Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1));
			return new Operation(type, value);
		}
	}
}