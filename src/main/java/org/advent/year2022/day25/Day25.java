package org.advent.year2022.day25;

import org.advent.common.Utils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day25 {
	
	static final char[] symbols = {'=', '-', '0', '1', '2'};
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day25.class, "input.txt");
		List<String> lines = new ArrayList<>();
		while (input.hasNext()) {
			lines.add(input.nextLine());
		}
		
		System.out.println("Answer: " + toSnafu(lines.stream().mapToLong(Day25::fromSnafu).sum()));
	}
	
	static long fromSnafu(String s) {
		long number = 0;
		for (int i = 0; i < s.length(); i++) {
			int place = ArrayUtils.indexOf(symbols, s.charAt(i)) - 2;
			number = number * 5 + place;
		}
		return number;
	}
	
	static String toSnafu(long number) {
		StringBuilder result = new StringBuilder();
		while (number > 0) {
			int place = (int) (number % 5);
			result.insert(0, symbols[(place + 2) % 5]);
			number = number / 5 + (place > 2 ? 1 : 0);
		}
		return result.toString();
	}
}