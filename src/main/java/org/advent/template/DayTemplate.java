package org.advent.template;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class DayTemplate extends AdventDay {
	
	public static void main(String[] args) {
		DayRunner runner = new DayRunner(new DayTemplate());
//		runner.run("example.txt", 1);
		runner.runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", null, null),
				new ExpectedAnswers("input.txt", null, null)
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
		return null;
	}
	
	@Override
	public Object part2() {
		return null;
	}
}