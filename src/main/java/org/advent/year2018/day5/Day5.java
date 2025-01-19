package org.advent.year2018.day5;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day5 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day5()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 10, 4),
				new ExpectedAnswers("input.txt", 9116, 6890)
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
		return collapse(line).length();
	}
	
	@Override
	public Object part2() {
		String collapsed = collapse(line);
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < 26; i++) {
			String replaced = Utils.removeEach(collapsed, Character.toString('a' + i), Character.toString('A' + i));
			min = Math.min(min, collapse(replaced).length());
		}
		return min;
	}
	
	String collapse(String polymer) {
		int lettersDiff = 'a' - 'A';
		char[] chars = polymer.toCharArray();
		int length = chars.length;
		for (int i = 0; i < length - 1; i++) {
			int left = i;
			int right = i + 1;
			while (left >= 0 && right < length && Math.abs(chars[left] - chars[right]) == lettersDiff) {
				left--;
				right++;
			}
			if (left < i) {
				System.arraycopy(chars, right, chars, left + 1, length - right);
				i = left;
				length -= right - left - 1;
			}
		}
		return new String(chars, 0, length);
	}
}