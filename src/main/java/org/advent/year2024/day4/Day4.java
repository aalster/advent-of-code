package org.advent.year2024.day4;

import org.advent.common.DirectionExt;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.Map;
import java.util.Scanner;

public class Day4 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day4.class, "input.txt");
		Map<Point, Character> field = Point.readFieldMap(Utils.readLines(input));
		
		System.out.println("Answer 1: " + part1(field));
		System.out.println("Answer 2: " + part2(field));
	}
	
	private static long part1(Map<Point, Character> field) {
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
	
	private static long part2(Map<Point, Character> field) {
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
	
	static boolean msPair(Character a, Character b) {
		if (a == null || b == null)
			return false;
		if (a != 'M' && a != 'S' || b != 'M' && b != 'S')
			return false;
		return a != b;
	}
}