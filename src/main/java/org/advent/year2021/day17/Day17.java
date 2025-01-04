package org.advent.year2021.day17;

import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Day17 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day17()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 45, 112),
				new ExpectedAnswers("input.txt", 19503, 5200)
		);
	}
	
	Target target;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		target = Target.parse(input.nextLine().replace("target area: ", ""));
	}
	
	@Override
	public Object part1() {
		for (int y = target.minY; y <= target.maxY; y++) {
			int vy = - y - 1;
			for (int vx = target.maxX; vx > 0; vx--)
				if (target.landed(vx, vy))
					return vy * (vy + 1) / 2;
		}
		return 0;
	}
	
	@Override
	public Object part2() {
		Map<Integer, List<Integer>> vyByTime = new HashMap<>();
		
		for (int vy = target.minY; vy <= -target.minY; vy++) {
			int t = 0;
			int y = 0;
			int currentVy = vy;
			while (y >= target.minY) {
				t++;
				y += currentVy;
				currentVy--;
				if (target.minY <= y && y <= target.maxY)
					vyByTime.computeIfAbsent(t, k -> new ArrayList<>()).add(vy);
			}
		}
		
		int maxT = vyByTime.keySet().stream().mapToInt(t -> t).max().orElseThrow();
		
		Map<Integer, List<Integer>> vxByTime = new HashMap<>();
		for (int vx = 1; vx <= target.maxX; vx++) {
			int t = 0;
			int x = 0;
			int currentVx = vx;
			while (x <= target.maxX && t <= maxT) {
				t++;
				x += currentVx;
				if (currentVx > 0)
					currentVx--;
				if (target.minX <= x && x <= target.maxX)
					vxByTime.computeIfAbsent(t, k -> new ArrayList<>()).add(vx);
			}
		}
		
		Set<Point> velocities = new HashSet<>();
		for (Map.Entry<Integer, List<Integer>> entry : vyByTime.entrySet())
			for (int vx : vxByTime.getOrDefault(entry.getKey(), List.of()))
				for (int vy : entry.getValue())
					velocities.add(new Point(vx, vy));
		
		return velocities.size();
	}
	
	record Target(int minX, int maxX, int minY, int maxY) {
		
		boolean landed(int vx, int vy) {
			int y = -vy + 1;
			int x = vx * (vx + 1) / 2;
			int t = vy * 2 + 1;
			if (t < vx)
				x -= t * (t + 1) / 2;
			return minX <= x && x <= maxX && minY <= y && y <= maxY;
		}
		
		static Target parse(String line) {
			line = line.replace("x=", "").replace("y=", "").replace("..", ", ");
			int[] array = Arrays.stream(line.split(", ")).mapToInt(Integer::parseInt).toArray();
			return new Target(array[0], array[1], array[2], array[3]);
		}
	}
}