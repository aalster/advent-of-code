package org.advent.year2018.day25;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day25 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day25()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 2, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 4, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 3, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example4.txt", 8, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 383, ExpectedAnswers.IGNORE)
		);
	}
	
	List<int[]> points;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		points = Utils.readLines(input).stream()
				.map(line -> Arrays.stream(line.replace(" ", "").split(",")).mapToInt(Integer::parseInt).toArray())
				.collect(Collectors.toList());
	}
	
	@Override
	public Object part1() {
		int constellations = 0;
		List<int[]> currentConstellation = new ArrayList<>();
		while (!points.isEmpty()) {
			currentConstellation.add(points.removeLast());
			while (!currentConstellation.isEmpty()) {
				int[] current = currentConstellation.removeLast();
				for (Iterator<int[]> iterator = points.iterator(); iterator.hasNext(); ) {
					int[] point = iterator.next();
					if (sameConstellation(current, point)) {
						currentConstellation.add(point);
						iterator.remove();
					}
				}
			}
			constellations++;
		}
		return constellations;
	}
	
	boolean sameConstellation(int[] current, int[] point) {
		int distance = 0;
		for (int i = 0; i < 4; i++)
			distance += Math.abs(current[i] - point[i]);
		return distance <= 3;
	}
	
	@Override
	public Object part2() {
		return null;
	}
}