package org.advent.year2018.day14;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;

public class Day14 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day14()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "5158916779", ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", "0124515891", ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", "9251071085", ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example4.txt", "5941429882", ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example5.txt", ExpectedAnswers.IGNORE, 9),
				new ExpectedAnswers("example6.txt", ExpectedAnswers.IGNORE, 5),
				new ExpectedAnswers("example7.txt", ExpectedAnswers.IGNORE, 18),
				new ExpectedAnswers("example8.txt", ExpectedAnswers.IGNORE, 2018),
				new ExpectedAnswers("input.txt", "2103141159", 20165733)
		);
	}
	
	String input;
	
	@Override
	public void prepare(String file) {
		input = Utils.scanFileNearClass(getClass(), file).nextLine();
	}
	
	@Override
	public Object part1() {
		int recipesCount = Integer.parseInt(input) + 10;
		StringBuilder scoreboard = new StringBuilder(1000).append("37");
		int first = 0;
		int second = 1;
		while (scoreboard.length() < recipesCount) {
			int firstScore = scoreboard.charAt(first) - '0';
			int secondScore = scoreboard.charAt(second) - '0';
			scoreboard.append(firstScore + secondScore);
			first = (first + firstScore + 1) % scoreboard.length();
			second = (second + secondScore + 1) % scoreboard.length();
		}
		return scoreboard.substring(recipesCount - 10, recipesCount);
	}
	
	@Override
	public Object part2() {
		StringBuilder scoreboard = new StringBuilder(1000).append("37");
		int first = 0;
		int second = 1;
		while (true) {
			int firstScore = scoreboard.charAt(first) - '0';
			int secondScore = scoreboard.charAt(second) - '0';
			scoreboard.append(firstScore + secondScore);
			int index = scoreboard.indexOf(input, scoreboard.length() - input.length() - 1);
			if (index > 0)
				return index;
			
			first = (first + firstScore + 1) % scoreboard.length();
			second = (second + secondScore + 1) % scoreboard.length();
		}
	}
}