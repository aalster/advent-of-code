package org.advent.year2021.day20;

import org.advent.common.DirectionExt;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day20 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day20()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 35, 3351),
				new ExpectedAnswers("input.txt", 4917, 16389)
		);
	}
	
	static final List<Function<Point, Point>> inputPixels = List.of(
			DirectionExt.NW::shift,
			DirectionExt.N::shift,
			DirectionExt.NE::shift,
			DirectionExt.W::shift,
			p -> p,
			DirectionExt.E::shift,
			DirectionExt.SW::shift,
			DirectionExt.S::shift,
			DirectionExt.SE::shift
	);
	
	int[] enhancement;
	Image image;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<List<String>> lines = Utils.splitByEmptyLine(Utils.readLines(input));
		enhancement = lines.getFirst().stream().flatMapToInt(String::chars).map(c -> c == '#' ? 1 : 0).toArray();
		image = Image.parse(lines.getLast());
	}
	
	@Override
	public Object part1() {
		return image.enhance(enhancement, 2).countLitPixels();
	}
	
	@Override
	public Object part2() {
		return image.enhance(enhancement, 50).countLitPixels();
	}
	
	record Image(Map<Point, Integer> pixels, Rect bounds, int infinityPixel) {
		
		Image(Map<Point, Integer> pixels, int infinityPixel) {
			this(pixels, Point.bounds(pixels.keySet()), infinityPixel);
		}
		
		Image enhance(int[] enhancement) {
			Rect nextBounds = new Rect(bounds.topLeft().shift(-1, -1), bounds.bottomRight().shift(1, 1));
			
			Map<Point, Integer> nextPixels = new HashMap<>(pixels.size() * 2);
			for (int x = nextBounds.minX(); x <= nextBounds.maxX(); x++) {
				for (int y = nextBounds.minY(); y <= nextBounds.maxY(); y++) {
					Point current = new Point(x, y);
					int[] bits = inputPixels.stream().map(d -> d.apply(current))
							.mapToInt(p -> pixels.getOrDefault(p, infinityPixel))
							.toArray();
					nextPixels.put(current, enhancement[enhancementNumber(bits)]);
				}
			}
			int[] infinityBits = new int[9];
			Arrays.fill(infinityBits, infinityPixel);
			return new Image(nextPixels, nextBounds, enhancement[enhancementNumber(infinityBits)]);
		}
		
		Image enhance(int[] enhancement, int count) {
			Image image = this;
			while (count-- > 0)
				image = image.enhance(enhancement);
			return image;
		}
		
		int enhancementNumber(int[] bits) {
			int enhancementNumber = 0;
			for (int bit : bits)
				enhancementNumber = enhancementNumber << 1 | bit;
			return enhancementNumber;
		}
		
		long countLitPixels() {
			if (infinityPixel == 1)
				return Long.MAX_VALUE;
			return pixels.values().stream().mapToInt(n -> n).sum();
		}
		
		static Image parse(List<String> lines) {
			Map<Point, Integer> pixels = Point.readFieldMap(lines).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() == '#' ? 1 : 0));
			return new Image(pixels, 0);
		}
	}
}