package org.advent.year2022.day4;

import org.advent.common.Pair;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day4 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day4.class,"input.txt");
		List<Pair<Section, Section>> sections = new ArrayList<>();
		while (input.hasNext()) {
			String[] pairs = input.nextLine().split(",");
			sections.add(new Pair<>(Section.parse(pairs[0]), Section.parse(pairs[1])));
		}
		
		System.out.println("Answer 1: " + part1(sections));
		System.out.println("Answer 2: " + part2(sections));
	}
	
	private static long part1(List<Pair<Section, Section>> sections) {
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
	
	private static long part2(List<Pair<Section, Section>> sections) {
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