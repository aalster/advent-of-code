package org.advent.year2022.day23;

import lombok.Data;
import org.advent.common.DirectionExt;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day23 {
	
	static final boolean debug = false;
	
	public static void main(String[] args) throws Exception {
		Scanner input = Utils.scanFileNearClass(Day23.class, "input.txt");
		Set<Point> elvesPoints = new HashSet<>();
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
		
		System.out.println("Answer 1: " + moveElves(elvesPoints, 10));
		System.out.println("Answer 2: " + moveElves(elvesPoints, Integer.MAX_VALUE));
	}
	
	private static int moveElves(Set<Point> elvesPoints, int rounds) throws Exception {
		MovementCheck[] checks = MovementCheck.initial();
		Map<Point, Elf> elves = elvesPoints.stream().collect(Collectors.toMap(p -> p, Elf::new));
		if (debug) {
			printField(elves, 0);
			Thread.sleep(1000);
		}
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
			
			if (debug) {
				printField(elves, round + 1);
				Thread.sleep(1000);
			}
			if (moves <= 0)
				return round + 1;
		}
		return countEmptyCells(elves);
	}
	
	private static void printField(Map<Point, Elf> elves, int round) {
		System.out.println(round <= 0 ? "\n== Initial State ==" : "\n== End of Round " + round + " ==");
		for (int y = -2; y < 10; y++) {
			for (int x = -3; x < 11; x++) {
				System.out.print(elves.containsKey(new Point(x, y)) ? '#' : '.');
			}
			System.out.println();
		}
	}
	
	private static int countEmptyCells(Map<Point, Elf> elves) {
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