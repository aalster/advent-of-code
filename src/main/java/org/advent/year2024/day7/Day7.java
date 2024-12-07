package org.advent.year2024.day7;

import org.advent.common.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day7 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day7.class, "input.txt");
		List<Equation> equations = Utils.readLines(input).stream().map(Equation::parse).toList();
		
		System.out.println("Answer 1: " + part1(equations));
		System.out.println("Answer 2: " + part2(equations));
	}
	
	private static long part1(List<Equation> equations) {
		return equations.stream().filter(Equation::canBeTrue).mapToLong(Equation::result).sum();
	}
	
	private static long part2(List<Equation> equations) {
		return equations.stream().filter(Equation::canBeTrue2).mapToLong(Equation::result).sum();
	}
	
	static long concatenate(long left, long right) {
		int rightDigitsMarker = 1;
		while (rightDigitsMarker * 10L <= right)
			rightDigitsMarker *= 10;
		
		while (rightDigitsMarker > 0) {
			long digit = right / rightDigitsMarker;
			left = left * 10 + digit;
			right %= rightDigitsMarker;
			rightDigitsMarker /= 10;
		}
		return left;
	}
	
	record Equation(long result, int[] numbers) {
		
		boolean canBeTrue() {
			return canBeTrueRecursive(numbers[0], 1, false);
		}
		
		boolean canBeTrue2() {
			return canBeTrueRecursive(numbers[0], 1, true);
		}
		
		boolean canBeTrueRecursive(long total, int index, boolean useConcatenation) {
			if (index >= numbers.length)
				return total == result;
			if (total > result)
				return false;
			return canBeTrueRecursive(total * numbers[index], index + 1, useConcatenation)
					|| (useConcatenation && canBeTrueRecursive(concatenate(total, numbers[index]), index + 1, true))
					|| canBeTrueRecursive(total + numbers[index], index + 1, useConcatenation);
		}
		
		static Equation parse(String line) {
			String[] split = line.split(": ");
			int[] numbers = Arrays.stream(split[1].split(" ")).mapToInt(Integer::parseInt).toArray();
			return new Equation(Long.parseLong(split[0]), numbers);
		}
	}
}