package org.advent.year2022.day23;

import lombok.Data;
import org.advent.common.DirectionExt;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day23 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day23()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 110, 20),
				new ExpectedAnswers("input.txt", 4138, 1010)
		);
	}
	
	Set<Point> elvesPoints;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		elvesPoints = new HashSet<>();
		int y = 0;
		while (input.hasNext()) {
			char[] charArray = input.nextLine().toCharArray();
			for (int x = 0; x < charArray.length; x++) {
				char c = charArray[x];
				if (c == '#')
					elvesPoints.add(new Point(x, y));
			}
			y++;
		}
	}
	
	@Override
	public Object part1() {
		return moveElves(10);
	}
	
	@Override
	public Object part2() {
		return moveElves(Integer.MAX_VALUE);
	}
	
	int moveElves(int rounds) {
		MovementCheck[] checks = MovementCheck.initial();
		Map<Point, Elf> elves = elvesPoints.stream().collect(Collectors.toMap(p -> p, Elf::new));
		for (int round = 0; round < rounds; round++) {
			elves.values().forEach(e -> e.setNextPosition(null));
			Map<Point, Integer> counts = new HashMap<>(elves.size() * 2);
			for (Elf elf : elves.values()) {
				Point nextPosition = elf.nextPosition(elves, round, checks);
				if (nextPosition != null)
					counts.compute(nextPosition, (p, c) -> c == null ? 1 : c + 1);
			}
			Map<Point, Elf> map = new HashMap<>();
			int moves = 0;
			for (Elf elf : elves.values()) {
				if (elf.getNextPosition() != null && counts.get(elf.getNextPosition()) == 1) {
					elf.moveToNext();
					moves++;
				}
				map.put(elf.getPosition(), elf);
			}
			elves = map;
			
			if (moves <= 0)
				return round + 1;
		}
		return countEmptyCells(elves);
	}
	
	int countEmptyCells(Map<Point, Elf> elves) {
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (Point p : elves.keySet()) {
			if (p.x() < minX)
				minX = p.x();
			if (maxX < p.x())
				maxX = p.x();
			if (p.y() < minY)
				minY = p.y();
			if (maxY < p.y())
				maxY = p.y();
		}
		return (maxX - minX + 1) * (maxY - minY + 1) - elves.size();
	}
	
	@Data
	static class Elf {
		Point position;
		Point nextPosition;
		
		public Elf(Point position) {
			this.position = position;
		}
		
		Point nextPosition(Map<Point, Elf> elves, int round, MovementCheck[] checks) {
			if (Arrays.stream(DirectionExt.values()).map(d -> d.shift(position)).noneMatch(elves::containsKey)) {
				return null;
			}
			
			for (int i = 0; i < checks.length; i++) {
				MovementCheck check = checks[(i + round) % 4];
				if (check.canMove(position, elves)) {
					nextPosition = check.target.shift(position);
					break;
				}
			}
			return nextPosition;
		}
		
		boolean moveToNext() {
			if (nextPosition == null)
				return false;
			position = nextPosition;
			return true;
		}
	}
	
	record MovementCheck(DirectionExt target, DirectionExt check1, DirectionExt check2) {
		
		boolean canMove(Point position, Map<Point, Elf> elves) {
			return !elves.containsKey(target.shift(position)) && !elves.containsKey(check1.shift(position)) && !elves.containsKey(check2.shift(position));
		}
		
		static MovementCheck[] initial() {
			return new MovementCheck[]{
					new MovementCheck(DirectionExt.N, DirectionExt.NW, DirectionExt.NE),
					new MovementCheck(DirectionExt.S, DirectionExt.SW, DirectionExt.SE),
					new MovementCheck(DirectionExt.W, DirectionExt.NW, DirectionExt.SW),
					new MovementCheck(DirectionExt.E, DirectionExt.NE, DirectionExt.SE)
			};
		}
	}
}