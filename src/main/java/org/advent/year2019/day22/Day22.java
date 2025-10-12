package org.advent.year2019.day22;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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
				new ExpectedAnswers("input.txt", 3749, 77225522112241L)
		);
	}
	
	List<String> lines;
	boolean example;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
		example = file.startsWith("example");
	}
	
	@Override
	public Object part1() {
		if (example) {
			long[] cards = LongStream.range(0, 10).toArray();
			long[] result = Operation.parseAndCombine(lines, cards.length).computeAll(cards);
			return LongStream.of(result).mapToObj(String::valueOf).collect(Collectors.joining(" "));
		}
		return Operation.parseAndCombine(lines, 10007).compute(2019);
	}
	
	@Override
	public Object part2() {
		long size = 119315717514047L;
		long shuffles = 101741582076661L;
		long index = 2020;
		
		// Порядок возвращается в начальный, если перемешать колоду size раз.
		return Operation.parseAndCombine(lines, size)
				.combineRepeated(size - shuffles - 1, size)
				.compute(index);
	}
	
	record Operation(BigInteger mul, BigInteger add, BigInteger mod) {
		
		Operation(long mul, long add, long mod) {
			this(BigInteger.valueOf(mul), BigInteger.valueOf(add), BigInteger.valueOf(mod));
		}
		
		static Operation identity(long mod) {
			return new Operation(1, 0, mod);
		}
		
		// Подставляет функцию саму в себя times раз
		Operation combineRepeated(long times, long size) {
			Operation original = this;
			Operation total = Operation.identity(size);
			while (times > 1) {
				if (times % 2 > 0)
					total = total.combine(original);
				original = original.combine(original);
				times /= 2;
			}
			return total.combine(original);
		}
		
		Operation combine(Operation other) {
			return new Operation(
					mul.multiply(other.mul).mod(mod),
					mul.multiply(other.add).add(add).mod(mod),
					mod);
		}
		
		long compute(long index) {
			return BigInteger.valueOf(index).multiply(mul).add(add).mod(mod).longValue();
		}
		
		long[] computeAll(long[] values) {
			long[] result = new long[values.length];
			for (int i = 0; i < result.length; i++)
				result[(int) compute(i)] = values[i];
			return result;
		}
		
		static Operation parse(String line, long size) {
			if (line.startsWith("deal into new stack")) {
				// index = size - index - 1
				return new Operation(-1, size - 1, size);
			}
			int value = Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1));
			if (line.startsWith("deal with increment")) {
				// index = index * value % size
				return new Operation(value, 0, size);
			}
			if (line.startsWith("cut")) {
				// index = (index - value) % size
				return new Operation(1, -value, size);
			}
			throw new IllegalArgumentException("Unknown operation: " + line);
		}
		
		static Operation parseAndCombine(List<String> lines, long size) {
			Operation operation = Operation.identity(size);
			for (String line : lines)
				operation = Operation.parse(line, size).combine(operation);
			return operation;
		}
	}
}