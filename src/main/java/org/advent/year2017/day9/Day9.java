package org.advent.year2017.day9;

import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day9 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day9()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 1, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 6, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 5, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example4.txt", 16, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example5.txt", 1, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example6.txt", 9, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example7.txt", 9, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example8.txt", 3, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example9.txt", ExpectedAnswers.IGNORE, 0),
				new ExpectedAnswers("example10.txt", ExpectedAnswers.IGNORE, 17),
				new ExpectedAnswers("example11.txt", ExpectedAnswers.IGNORE, 3),
				new ExpectedAnswers("example12.txt", ExpectedAnswers.IGNORE, 2),
				new ExpectedAnswers("example13.txt", ExpectedAnswers.IGNORE, 0),
				new ExpectedAnswers("example14.txt", ExpectedAnswers.IGNORE, 0),
				new ExpectedAnswers("example15.txt", ExpectedAnswers.IGNORE, 10),
				new ExpectedAnswers("input.txt", 23588, 10045)
		);
	}
	
	String line;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		line = input.nextLine();
	}
	
	@Override
	public Object part1() {
		return count(line).score;
	}
	
	@Override
	public Object part2() {
		return count(line).garbageSize;
	}
	
	Stats count(String line) {
		return count(line.toCharArray(), 0, 0).right();
	}
	
	Pair<Integer, Stats> count(char[] chars, int index, int level) {
		boolean garbage = false;
		int currentIndex = index;
		Stats stats = new Stats(level, 0);
		while (currentIndex < chars.length) {
			switch (chars[currentIndex]) {
				case '<' -> {
					if (!garbage)
						stats = stats.garbageIncrease(-1);
					garbage = true;
				}
				case '>' -> {
					if (garbage)
						garbage = false;
				}
				case '!' -> {
					if (garbage) {
						currentIndex++;
						stats = stats.garbageIncrease(-1);
					}
				}
				case '{' -> {
					if (!garbage) {
						Pair<Integer, Stats> nested = count(chars, currentIndex + 1, level + 1);
						currentIndex = nested.left();
						stats = stats.merge(nested.right());
					}
				}
				case '}' -> {
					if (!garbage) {
						return Pair.of(currentIndex, stats);
					}
				}
			}
			if (garbage)
				stats = stats.garbageIncrease(1);
			currentIndex++;
		}
		return Pair.of(currentIndex, stats);
	}
	
	record Stats(int score, int garbageSize) {
		
		Stats garbageIncrease(int garbageDelta) {
			return new Stats(score, garbageSize + garbageDelta);
		}
		
		Stats merge(Stats other) {
			return new Stats(score + other.score, garbageSize + other.garbageSize);
		}
	}
}