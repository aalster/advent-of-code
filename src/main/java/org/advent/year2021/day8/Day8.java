package org.advent.year2021.day8;

import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day8 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day8.class, "input.txt");
		List<String> lines = new ArrayList<>();
		while (input.hasNext()) {
			lines.add(input.nextLine());
		}
		
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2(lines));
	}
	
	private static int part1(List<String> data) {
		Set<Integer> segmentsSizeToCount = Set.of(2, 3, 4, 7);
		return (int) data.stream()
				.map(s -> StringUtils.substringAfter(s, " | "))
				.flatMap(s -> Arrays.stream(s.split(" ")))
				.filter(s -> segmentsSizeToCount.contains(s.length()))
				.count();
	}
	
	private static int part2(List<String> data) {
		int result = 0;
		for (String datum : data) {
			String[] split = datum.split(" \\| ");
			Map<Integer, List<Digit>> digits = Stream.of(split[0].split(" "))
					.map(Digit::unknown)
					.collect(Collectors.groupingBy(d -> d.signals().size()));
			List<String> outputNumbers = List.of(split[1].split(" "));
			
			Digit one = digits.get(2).get(0).known(1);
			Digit four = digits.get(4).get(0).known(4);
			Digit seven = digits.get(3).get(0).known(7);
			Digit eight = digits.get(7).get(0).known(8);
			
			Digit six = digits.get(6).stream().filter(d -> d.signalsExcept(seven).size() == 4).findAny().orElseThrow().known(6);
			Digit nine = digits.get(6).stream().filter(d -> d.signalsExcept(four).size() == 2).findAny().orElseThrow().known(9);
			Digit zero = digits.get(6).stream().filter(d -> !d.equalSignals(six) && !d.equalSignals(nine)).findAny().orElseThrow().known(0);
			
			Digit five = digits.get(5).stream().filter(d -> d.signalsExcept(nine).size() == 0 && d.signalsExcept(seven).size() == 3).findAny().orElseThrow().known(5);
			Digit three = digits.get(5).stream().filter(d -> d.signalsExcept(seven).size() == 2).findAny().orElseThrow().known(3);
			Digit two = digits.get(5).stream().filter(d -> d.signalsExcept(nine).size() == 1).findAny().orElseThrow().known(2);
			
			List<Digit> allDigits = List.of(zero, one, two, three, four, five, six, seven, eight, nine);
			
//			allDigits.forEach(System.out::println);
			
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
			
//			System.out.println(outputNumbers + " -> " + output);
			result += output;
		}
		return result;
	}
	
	record Digit(int digit, Set<Integer> signals) {
		boolean equalSignals(Digit other) {
			return signals.equals(other.signals);
		}
		
		Set<Integer> signalsExcept(Digit d) {
			return this.signals.stream().filter(o -> !d.signals.contains(o)).collect(Collectors.toSet());
		}
		
		Digit known(int digit) {
			return new Digit(digit, signals);
		}
		
		static Digit unknown(String s) {
			return new Digit(-1, s.chars().boxed().collect(Collectors.toSet()));
		}
		
		@Override
		public String toString() {
			return "Digit[" + digit + ", " + signals.stream().map(Character::toString).collect(Collectors.joining()) + "]";
		}
	}
}