package org.advent.year2021.day8;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day8 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day8()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", ExpectedAnswers.IGNORE, 5353),
				new ExpectedAnswers("example2.txt", 26, 61229),
				new ExpectedAnswers("input.txt", 303, 961734)
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
		Set<Integer> segmentsSizeToCount = Set.of(2, 3, 4, 7);
		return (int) lines.stream()
				.map(s -> StringUtils.substringAfter(s, " | "))
				.flatMap(s -> Arrays.stream(s.split(" ")))
				.filter(s -> segmentsSizeToCount.contains(s.length()))
				.count();
	}
	
	@Override
	public Object part2() {
		int result = 0;
		for (String datum : lines) {
			String[] split = datum.split(" \\| ");
			Map<Integer, List<Digit>> digits = Stream.of(split[0].split(" "))
					.map(Digit::unknown)
					.collect(Collectors.groupingBy(d -> d.signals().size()));
			List<String> outputNumbers = List.of(split[1].split(" "));
			
			Digit one = digits.get(2).getFirst().known(1);
			Digit four = digits.get(4).getFirst().known(4);
			Digit seven = digits.get(3).getFirst().known(7);
			Digit eight = digits.get(7).getFirst().known(8);
			
			Digit six = digits.get(6).stream().filter(d -> d.signalsSizeExcept(seven) == 4).findAny().orElseThrow().known(6);
			Digit nine = digits.get(6).stream().filter(d -> d.signalsSizeExcept(four) == 2).findAny().orElseThrow().known(9);
			Digit zero = digits.get(6).stream().filter(d -> !d.equalSignals(six) && !d.equalSignals(nine)).findAny().orElseThrow().known(0);
			
			Digit five = digits.get(5).stream().filter(d -> d.signalsSizeExcept(nine) == 0 && d.signalsSizeExcept(seven) == 3).findAny().orElseThrow().known(5);
			Digit three = digits.get(5).stream().filter(d -> d.signalsSizeExcept(seven) == 2).findAny().orElseThrow().known(3);
			Digit two = digits.get(5).stream().filter(d -> d.signalsSizeExcept(nine) == 1).findAny().orElseThrow().known(2);
			
			List<Digit> allDigits = List.of(zero, one, two, three, four, five, six, seven, eight, nine);
			
			int output = 0;
			for (String outputNumber : outputNumbers) {
				Digit unknown = Digit.unknown(outputNumber);
				for (Digit known : allDigits) {
					if (unknown.equalSignals(known)) {
						unknown = known;
						break;
					}
				}
				if (unknown.digit < 0)
					throw new RuntimeException("Digit not found for " + outputNumber);
				output = output * 10 + unknown.digit();
			}
			
			result += output;
		}
		return result;
	}
	
	record Digit(int digit, Set<Integer> signals) {
		boolean equalSignals(Digit other) {
			return signals.equals(other.signals);
		}
		
		int signalsSizeExcept(Digit d) {
			return this.signals.stream().filter(o -> !d.signals.contains(o)).collect(Collectors.toSet()).size();
		}
		
		Digit known(int digit) {
			return new Digit(digit, signals);
		}
		
		static Digit unknown(String s) {
			return new Digit(-1, s.chars().boxed().collect(Collectors.toSet()));
		}
	}
}