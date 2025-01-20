package org.advent.year2018.day10;

import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.common.ascii.AsciiLetters;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day10 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day10()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "HI", 3),
				new ExpectedAnswers("input.txt", "RLEZNRAN", 10240)
		);
	}
	
	List<Light> initialLights;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		initialLights = Utils.readLines(input).stream().map(Light::parse).toList();
	}
	
	@Override
	public Object part1() {
		return solve(initialLights).left();
	}
	
	@Override
	public Object part2() {
		return solve(initialLights).right();
	}
	
	Pair<String, Integer> solve(List<Light> lights) {
		lights = new ArrayList<>(lights);
		Light min = lights.stream().min(Comparator.comparing(Light::pos, Comparator.comparing(Point::y))).orElseThrow();
		Light max = lights.stream().max(Comparator.comparing(Light::pos, Comparator.comparing(Point::y))).orElseThrow();
		int relativeDistance = max.pos.y() - min.pos.y();
		int relativeSpeed = min.vel.y() - max.vel.y();
		int skipTime = Math.max((relativeDistance - 20) / relativeSpeed, 0);
		
		lights = lights.stream().map(l -> l.move(skipTime)).toList();
		int time = skipTime;
		
		int height = Integer.MAX_VALUE;
		while (true) {
			List<Light> nextLights = lights.stream().map(l -> l.move(1)).toList();
			int minY = Integer.MAX_VALUE;
			int maxY = Integer.MIN_VALUE;
			for (Light light : nextLights) {
				minY = Math.min(minY, light.pos.y());
				maxY = Math.max(maxY, light.pos.y());
			}
			int nextHeight = maxY - minY;
			if (nextHeight > height)
				break;
			lights = nextLights;
			height = nextHeight;
			time++;
		}
		Set<Point> points = lights.stream().map(Light::pos).collect(Collectors.toSet());
		return Pair.of(AsciiLetters.parse(points), time);
	}
	
	record Light(Point pos, Point vel) {
		Light move(int time) {
			return new Light(pos.shift(vel.scale(time)), vel);
		}
		
		static Pattern pattern = Pattern.compile("position=<(.+)>velocity=<(.+)>");
		static Light parse(String line) {
			Matcher matcher = pattern.matcher(line.replace(" ", ""));
			if (!matcher.matches())
				throw new IllegalArgumentException("Invalid line: " + line);
			return new Light(Point.parse(matcher.group(1)), Point.parse(matcher.group(2)));
		}
	}
}