package org.advent.year2020.day18;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

public class Day18 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day18()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 71, 231),
				new ExpectedAnswers("example2.txt", 51, 51),
				new ExpectedAnswers("example3.txt", 26, 46),
				new ExpectedAnswers("example4.txt", 437, 1445),
				new ExpectedAnswers("example5.txt", 12240, 669060),
				new ExpectedAnswers("example6.txt", 13632, 23340),
				new ExpectedAnswers("input.txt", 75592527415659L, 360029542265462L)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
	}
	
	@Override
	public Object part1() {
		return lines.stream().mapToLong(l -> eval(l, SequentialResultEvaluator::new)).sum();
	}
	
	@Override
	public Object part2() {
		return lines.stream().mapToLong(l -> eval(l, PrioritizeSumResultEvaluator::new)).sum();
	}
	
	long eval(String expression, Supplier<ResultEvaluator> resultEvaluatorSupplier) {
		ResultEvaluator result = resultEvaluatorSupplier.get();
		long currentNumber = 0;
		for (int index = 0; index < expression.length(); index++) {
			char c = expression.charAt(index);
			if (Character.isDigit(c)) {
				currentNumber = currentNumber * 10 + (c - '0');
			} else {
				if (currentNumber > 0) {
					result.acceptNumber(currentNumber);
					currentNumber = 0;
				}
				if (c == '*')
					result.acceptOperator(true);
				else if (c == '+')
					result.acceptOperator(false);
				else if (c == '(') {
					int endIndex = parenthesesEnd(expression, index);
					long nested = eval(expression.substring(index + 1, endIndex), resultEvaluatorSupplier);
					index = endIndex + 1;
					result.acceptNumber(nested);
				}
			}
		}
		if (currentNumber > 0)
			result.acceptNumber(currentNumber);
		return result.result();
	}
	
	int parenthesesEnd(String expression, int start) {
		int parentheses = 1;
		for (int j = start + 1; j < expression.length(); j++) {
			if (expression.charAt(j) == '(')
				parentheses++;
			else if (expression.charAt(j) == ')')
				parentheses--;
			if (parentheses == 0)
				return j;
		}
		return -1;
	}
	
	interface ResultEvaluator {
		void acceptOperator(boolean multiply);
		void acceptNumber(long number);
		long result();
	}
	
	static class SequentialResultEvaluator implements ResultEvaluator {
		long result;
		boolean currentOperatorMultiply;
		
		@Override
		public void acceptOperator(boolean multiply) {
			currentOperatorMultiply = multiply;
		}
		
		@Override
		public void acceptNumber(long number) {
			result = currentOperatorMultiply ? result * number : result + number;
		}
		
		@Override
		public long result() {
			return result;
		}
	}
	
	static class PrioritizeSumResultEvaluator implements ResultEvaluator {
		long result = 1;
		long currentMultiplier;
		
		@Override
		public void acceptOperator(boolean multiply) {
			if (multiply)
				multiplierFinished();
		}
		
		@Override
		public void acceptNumber(long number) {
			currentMultiplier += number;
		}
		
		void multiplierFinished() {
			if (currentMultiplier > 0) {
				result = result * currentMultiplier;
				currentMultiplier = 0;
			}
		}
		
		@Override
		public long result() {
			multiplierFinished();
			return result;
		}
	}
}