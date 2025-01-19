package org.advent.year2018.day3;

import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day3()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 4, 3),
				new ExpectedAnswers("input.txt", 119572, 775)
		);
	}
	
	Claim[] claims;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		claims = Utils.readLines(input).stream().map(Claim::parse).toArray(Claim[]::new);
	}
	
	@Override
	public Object part1() {
		int[][] fabric = new int[1000][1000];
		for (Claim claim : claims)
			for (int x = claim.rect.minX(); x <= claim.rect.maxX(); x++)
				for (int y = claim.rect.minY(); y <= claim.rect.maxY(); y++)
					fabric[x][y]++;

		int overlaps = 0;
		for (int[] row : fabric)
			for (int count : row)
				overlaps += count > 1 ? 1 : 0;
		return overlaps;
	}
	
	@Override
	public Object part2() {
		Set<Integer> overlapsIds = new HashSet<>();
		for (int l = 0; l < claims.length; l++) {
			Claim left = claims[l];
			for (int r = l + 1; r < claims.length; r++) {
				Claim right = claims[r];
				if (left.overlaps(right))
					overlapsIds.addAll(List.of(left.id, right.id));
			}
		}
		return Arrays.stream(claims).map(Claim::id).filter(id -> !overlapsIds.contains(id)).findFirst().orElse(null);
	}
	
	record Claim(int id, Rect rect) {
		
		boolean overlaps(Claim other) {
			return rect.intersectsInclusive(other.rect);
		}
		
		static Pattern pattern = Pattern.compile("#(.+) @ (.+): (.+)x(.+)");
		static Claim parse(String line) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches())
				throw new IllegalArgumentException("Invalid line: " + line);
			int id = Integer.parseInt(matcher.group(1));
			Point start = Point.parse(matcher.group(2));
			Point size = new Point(Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
			return new Claim(id, new Rect(start, start.shift(size).shift(new Point(-1, -1))));
		}
	}
}