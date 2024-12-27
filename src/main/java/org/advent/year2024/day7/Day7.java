package org.advent.year2024.day7;

import org.advent.common.Utils;
import org.advent.runner.AbstractDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day7 extends AbstractDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day7()).run("input.txt");
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 3749, 11387),
				new ExpectedAnswers("input.txt", 8401132154762L, 95297119227552L)
		);
	}
	
	List<Equation> equations;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		equations = Utils.readLines(input).stream().map(Equation::parse).toList();
	}
	
	@Override
	public Object part1() {
		return equations.stream().filter(Equation::canBeTrue).mapToLong(Equation::result).sum();
	}
	
	@Override
	public Object part2() {
		return equations.stream().filter(Equation::canBeTrueConcatenation).mapToLong(Equation::result).sum();
	}
	
	record Equation(long result, int[] numbers) {
		
		boolean canBeTrue() {
			return canBeTrueRecursive(numbers[0], 1, false);
		}
		
		boolean canBeTrueConcatenation() {
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
		
		long concatenate(long left, long right) {
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
		
		static Equation parse(String line) {
			String[] split = line.split(": ");
			int[] numbers = Arrays.stream(split[1].split(" ")).mapToInt(Integer::parseInt).toArray();
			return new Equation(Long.parseLong(split[0]), numbers);
		}
	}
}