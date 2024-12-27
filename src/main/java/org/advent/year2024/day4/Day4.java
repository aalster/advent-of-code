package org.advent.year2024.day4;

import org.advent.common.DirectionExt;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day4 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day4()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 18, 9),
				new ExpectedAnswers("input.txt", 2297, 1745)
		);
	}
	
	Map<Point, Character> field;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		field = Point.readFieldMap(Utils.readLines(input));
	}
	
	@Override
	public Object part1() {
		long result = 0;
		char[] word = "XMAS".toCharArray();
		
		for (Map.Entry<Point, Character> entry : field.entrySet()) {
			if (entry.getValue() != word[0])
				continue;
			
			for (DirectionExt direction : DirectionExt.values()) {
				Point next = entry.getKey();
				boolean matches = true;
				for (int i = 1; i < word.length; i++) {
					next = direction.shift(next);
					Character nextChar = field.get(next);
					if (nextChar == null || nextChar != word[i]) {
						matches = false;
						break;
					}
				}
				if (matches)
					result++;
			}
		}
		return result;
	}
	
	@Override
	public Object part2() {
		long result = 0;
		
		for (Map.Entry<Point, Character> entry : field.entrySet()) {
			if (entry.getValue() != 'A')
				continue;
			
			Point center = entry.getKey();
			if (msPair(field.get(DirectionExt.NE.shift(center)), field.get(DirectionExt.SW.shift(center)))
					&& msPair(field.get(DirectionExt.NW.shift(center)), field.get(DirectionExt.SE.shift(center))))
				result++;
		}
		return result;
	}
	
	boolean msPair(Character a, Character b) {
		if (a == null || b == null)
			return false;
		if (a != 'M' && a != 'S' || b != 'M' && b != 'S')
			return false;
		return a != b;
	}
}