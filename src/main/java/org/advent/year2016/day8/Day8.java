package org.advent.year2016.day8;

import org.advent.common.AsciiLetters;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day8 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day8()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 6, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 110, "ZJHRKCPLYJ")
		);
	}
	
	final int letterWidth = 5;
	int width;
	int height;
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
		width = switch (file) {
			case "example.txt" -> 7;
			case "input.txt" -> 50;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
		height = switch (file) {
			case "example.txt" -> 3;
			case "input.txt" -> 6;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		return draw(lines).size();
	}
	
	@Override
	public Object part2() {
		Collection<Point> image = draw(lines);
		StringBuilder result = new StringBuilder();
		while (!image.isEmpty()) {
			Map<Boolean, List<Point>> groups = image.stream().collect(Collectors.groupingBy(p -> p.x() < letterWidth));
			image = groups.getOrDefault(false, List.of()).stream().map(p -> p.shift(-letterWidth, 0)).toList();
			result.append(AsciiLetters.parse(groups.get(true)));
		}
		return result.toString();
	}
	
	Set<Point> draw(List<String> lines) {
		Set<Point> points = new HashSet<>();
		
		Pattern pattern = Pattern.compile("\\D(\\d+)\\D+(\\d+)$");
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.find())
				throw new RuntimeException("No numbers");
			int left = Integer.parseInt(matcher.group(1));
			int right = Integer.parseInt(matcher.group(2));
			if (line.startsWith("rect")) {
				for (int y = 0; y < right; y++)
					for (int x = 0; x < left; x++)
						points.add(new Point(x, y));
			} else if (line.contains("row")) {
				points = points.stream()
						.map(p -> p.y() != left ? p : wrap(p.shift(right, 0)))
						.collect(Collectors.toSet());
			} else if (line.contains("column")) {
				points = points.stream()
						.map(p -> p.x() != left ? p : wrap(p.shift(0, right)))
						.collect(Collectors.toSet());
			}
		}
		return points;
	}
	
	Point wrap(Point p) {
		while (p.x() < 0)
			p = p.shift(width, 0);
		while (width <= p.x())
			p = p.shift(-width, 0);
		while (p.y() < 0)
			p = p.shift(0, height);
		while (height <= p.y())
			p = p.shift(0, -height);
		return p;
	}
}