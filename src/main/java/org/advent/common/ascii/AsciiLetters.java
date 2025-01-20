package org.advent.common.ascii;

import org.advent.common.Point;
import org.advent.runner.DayRunner;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsciiLetters {
	private static final Map<LetterSize, LetterPoints> letterPointsCache = new HashMap<>();
	private static final Object lock = new Object();
	
	private static LetterPoints letterPoints(LetterSize size) {
		LetterPoints letterPoints = letterPointsCache.get(size);
		if (letterPoints == null) {
			synchronized (lock) {
				letterPoints = letterPointsCache.get(size);
				if (letterPoints == null) {
					letterPoints = LetterPoints.load(size);
					letterPointsCache.put(size, letterPoints);
				}
			}
		}
		return letterPoints;
	}
	
	public static String parse(LetterSize size, Collection<Point> points) {
		return letterPoints(size).parse(points);
	}
	
	public static String parse(Collection<Point> points) {
		return parse(LetterSize.forHeight(Point.maxY(points) - Point.minY(points) + 1), points);
	}
	
	public static String parse(String asciiLetters, char symbol) {
		return parse(Point.readField(List.of(asciiLetters.split("\n"))).get(symbol));
	}
	
	public static void main(String[] args) {
		List.of(
				new org.advent.year2016.day8.Day8(),
				new org.advent.year2018.day10.Day10(),
				new org.advent.year2021.day13.Day13(),
				new org.advent.year2022.day10.Day10()
		).forEach(day -> new DayRunner(day).runAll());
	}
}
