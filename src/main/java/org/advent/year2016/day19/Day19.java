package org.advent.year2016.day19;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day19 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day19()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 3, 2),
				new ExpectedAnswers("input.txt", 1830117, 1417887)
		);
	}
	
	int count;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		count = input.nextInt();
	}
	
	@Override
	public Object part1() {
		long step = 2;
		long winningIndex = 0;
		long deleted = 0;
		while (deleted < count - 1) {
			deleted++;
			winningIndex += step;
			if (winningIndex >= count) {
				winningIndex = (winningIndex % count) * 2;
				step *= 2;
			}
		}
		return winningIndex + 1;
		
		// Более короткое решение у Numberphile https://www.youtube.com/watch?v=uCsD3ZGzMgE
//		int poweredTwo = 1;
//		while (poweredTwo < count)
//			poweredTwo <<= 1;
//		int leftover = count - (poweredTwo >> 1);
//		return (leftover << 1) + 1;
	}
	
	@Override
	public Object part2() {
		if (count == 1)
			return 1;
		int poweredThree = 1;
		while (poweredThree < count)
			poweredThree *= 3;
		poweredThree /= 3;
		int leftover = count - poweredThree;
		return leftover + Math.max(leftover - poweredThree, 0);
	}
}