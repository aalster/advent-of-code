package org.advent.year2022.day4;

import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day4 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day4()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 2, 4),
				new ExpectedAnswers("input.txt", 459, 779)
		);
	}
	
	List<Pair<Section, Section>> sections;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		sections = new ArrayList<>();
		while (input.hasNext()) {
			String[] pairs = input.nextLine().split(",");
			sections.add(new Pair<>(Section.parse(pairs[0]), Section.parse(pairs[1])));
		}
	}
	
	@Override
	public Object part1() {
		int result = 0;
		for (Pair<Section, Section> pair : sections) {
			Section first = pair.left();
			Section second = pair.right();
			
			if (first.start() == second.start()) {
				result++;
				continue;
			}
			if (first.start() < second.start()) {
				if (second.end() <= first.end())
					result++;
			} else {
				if (first.end() <= second.end())
					result++;
			}
		}
		return result;
	}
	
	@Override
	public Object part2() {
		int result = 0;
		for (Pair<Section, Section> pair : sections) {
			Section first = pair.left();
			Section second = pair.right();
			
			if (first.start() <= second.end() && second.start() <= first.end())
				result++;
		}
		return result;
	}
	
	record Section(int start, int end) {
		static Section parse(String value) {
			String[] split = value.split("-");
			int start = Integer.parseInt(split[0]);
			int end = Integer.parseInt(split[1]);
			return new Section(start, end);
		}
	}
}