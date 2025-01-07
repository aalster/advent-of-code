package org.advent.year2016.day15;

import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day15 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day15()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 5, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 148737, 2353212)
		);
	}
	
	List<Pair<Integer, Integer>> disks;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		disks = new ArrayList<>();
		for (String line : Utils.readLines(input)) {
			int positions = Integer.parseInt(line.split(" positions")[0].split("has ")[1]);
			int start = Integer.parseInt(line.split("position ")[1].split("\\.")[0]);
			disks.add(Pair.of(positions, start));
		}
	}
	
	@Override
	public Object part1() {
		return solve(disks);
	}
	
	@Override
	public Object part2() {
		List<Pair<Integer, Integer>> expandedDisks = new ArrayList<>(disks);
		expandedDisks.add(Pair.of(11, 0));
		return solve(expandedDisks);
	}
	
	int solve(List<Pair<Integer, Integer>> disks) {
		int index = 1;
		int globalPositions = 1;
		int time = 0;
		for (Pair<Integer, Integer> disk : disks) {
			int positions = disk.left();
			int start = (disk.right() + index) % positions;
			
			while ((time + start) % positions != 0)
				time += globalPositions;
			
			globalPositions *= positions;
			index++;
		}
		return time;
	}
}