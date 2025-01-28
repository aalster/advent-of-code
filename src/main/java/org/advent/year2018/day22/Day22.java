package org.advent.year2018.day22;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.stream.Stream;

public class Day22 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day22()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 114, 45),
				new ExpectedAnswers("input.txt", 10204, 1004)
		);
	}
	
	int depth;
	Point target;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		depth = Integer.parseInt(input.nextLine().split(": ")[1]);
		target = Point.parse(input.nextLine().split(": ")[1]);
	}
	
	@Override
	public Object part1() {
		int[][] map = map(target);
		
		int riskLevel = 0;
		for (int y = 0; y <= target.y(); y++)
			for (int x = 0; x <= target.x(); x++)
				riskLevel += map[x][y];
		return riskLevel;
	}
	
	@Override
	public Object part2() {
		// Tools: 0 - neither, 1 - torch, 2 - climbing gear
		int[][] map = map(target.scale(2));
		Rect bounds = new Rect(0, map.length - 1, 0, map[0].length - 1);
		Map<Point, Map<Integer, Integer>> bestTimes = new HashMap<>();
		
		Queue<Path> paths = new PriorityQueue<>(1000, Comparator.comparing(Path::minutes));
		paths.add(new Path(new Point(0, 0), 1, 0));
		
		while (!paths.isEmpty()) {
			Path path = paths.poll();
			Map<Integer, Integer> bestTimesByTool = bestTimes.computeIfAbsent(path.position, k -> new HashMap<>());
			int bestTime = bestTimesByTool.getOrDefault(path.tool, Integer.MAX_VALUE);
			if (bestTime <= path.minutes)
				continue;
			bestTimesByTool.put(path.tool, path.minutes);
			
			if (path.position.equals(target) && path.tool == 1)
				return Math.min(bestTimesByTool.get(1), bestTimesByTool.getOrDefault(2, 10000) + 7);
			
			path.next(map, bounds).forEach(paths::add);
		}
		return null;
	}
	
	int[][] map(Point max) {
		int[][] erosionLevels = new int[max.x() + 1][max.y() + 1];
		int[][] map = new int[erosionLevels.length][erosionLevels[0].length];
		
		for (int y = 0; y <= max.y(); y++) {
			for (int x = 0; x <= max.x(); x++) {
				int geologicIndex;
				if (x == target.x() && y == target.y())
					geologicIndex = 0;
				else if (x == 0 || y == 0)
					geologicIndex = x * 16807 + y * 48271;
				else
					geologicIndex = erosionLevels[x][y - 1] * erosionLevels[x - 1][y];
				int erosionLevel = (geologicIndex + depth) % 20183;
				erosionLevels[x][y] = erosionLevel;
				map[x][y] = erosionLevel % 3;
			}
		}
		return map;
	}
	
	record Path(Point position, int tool, int minutes) {
		
		Stream<Path> next(int[][] map, Rect bounds) {
			int currentRegion = map[position.x()][position.y()];
			return Direction.stream()
					.map(position::shift)
					.filter(bounds::containsInclusive)
					.map(n -> {
						int nextRegion = map[n.x()][n.y()];
						if (nextRegion != tool)
							return new Path(n, tool, minutes + 1);
						return new Path(n, 3 - currentRegion - nextRegion, minutes + 1 + 7);
					});
		}
	}
}