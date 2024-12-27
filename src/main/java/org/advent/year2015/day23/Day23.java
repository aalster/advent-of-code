package org.advent.year2015.day23;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day23 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day23()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 170, 247)
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
		return solve(0);
	}
	
	@Override
	public Object part2() {
		return solve(1);
	}
	
	long solve(long a) {
		long b = 0;
		
		for (int i = 0; i < lines.size(); i++) {
			String[] split = lines.get(i).replace(",", "").split(" ");
			switch (split[0]) {
				case "hlf" -> {
					if (split[1].equals("a"))
						a /= 2;
					else
						b /= 2;
				}
				case "tpl" -> {
					if (split[1].equals("a"))
						a *= 3;
					else
						b *= 3;
				}
				case "inc" -> {
					if (split[1].equals("a"))
						a++;
					else
						b++;
				}
				case "jmp" -> i += Integer.parseInt(split[1]) - 1;
				case "jie" -> {
					if ((split[1].equals("a") ? a : b) % 2 == 0)
						i += Integer.parseInt(split[2]) - 1;
				}
				case "jio" -> {
					if ((split[1].equals("a") ? a : b) == 1)
						i += Integer.parseInt(split[2]) - 1;
				}
			}
		}
		return b;
	}
}