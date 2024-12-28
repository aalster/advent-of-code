package org.advent.year2023.day16;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day16 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day16()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 46, 51),
				new ExpectedAnswers("input.txt", 7951, 8148)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		lines = Utils.readLines(Utils.scanFileNearClass(getClass(), file));
	}
	
	@Override
	public Object part1() {
		Field field = Field.parse(lines);
		return countLights(field, new Light(new Point(0, 0), Direction.RIGHT));
	}
	
	@Override
	public Object part2() {
		Field field = Field.parse(lines);
		long maxLightsCount = 0;
		for (int x = 0; x <= field.maxX(); x++) {
			long countDown = countLights(field, new Light(new Point(x, 0), Direction.DOWN));
			long countUp = countLights(field, new Light(new Point(x, field.maxY()), Direction.UP));
			maxLightsCount = Math.max(maxLightsCount, Math.max(countDown, countUp));
		}
		for (int y = 0; y <= field.maxX(); y++) {
			long countRight = countLights(field, new Light(new Point(0, y), Direction.RIGHT));
			long countLeft = countLights(field, new Light(new Point(field.maxX(), y), Direction.LEFT));
			maxLightsCount = Math.max(maxLightsCount, Math.max(countRight, countLeft));
		}
		return maxLightsCount;
	}
	
	long countLights(Field field, Light start) {
		Set<Light> currentLights = Set.of(start);
		Set<Light> allLights = new HashSet<>(currentLights);
		while (!currentLights.isEmpty()) {
			currentLights = field.next(currentLights)
					.filter(l -> !allLights.contains(l))
					.collect(Collectors.toSet());
			allLights.addAll(currentLights);
		}
		return allLights.stream().map(Light::position).distinct().count();
	}
	
	record Field(Map<Point, Mirror> field, int maxX, int maxY) {
		
		Stream<Light> next(Collection<Light> lights) {
			return lights.stream().flatMap(this::next).filter(l -> inBounds(l.position()));
		}
		
		Stream<Light> next(Light light) {
			Mirror mirror = field.get(light.position());
			return mirror == null
					? Stream.of(new Light(light.position().shift(light.direction()), light.direction()))
					: mirror.next(light);
		}
		
		boolean inBounds(Point point) {
			return 0 <= point.x() && point.x() <= maxX && 0 <= point.y() && point.y() <= maxY;
		}
		
		static Field parse(List<String> lines) {
			int y = 0;
			Map<Point, Mirror> field = new HashMap<>();
			for (String line : lines) {
				for (int x = 0; x < line.length(); x++) {
					Mirror mirror = Mirror.parse(line.charAt(x));
					if (mirror != null)
						field.put(new Point(x, y), mirror);
				}
				y++;
			}
			return new Field(field, Point.maxX(field.keySet()), Point.maxY(field.keySet()));
		}
	}
	
	interface Mirror {
		Stream<Light> next(Light light);
		
		static Mirror parse(char c) {
			return switch (c) {
				case '|' -> new Splitter(true);
				case '-' -> new Splitter(false);
				case '/' -> new Reflector(true);
				case '\\' -> new Reflector(false);
				default -> null;
			};
		}
	}
	
	record Splitter(boolean vertical) implements Mirror {
		@Override
		public Stream<Light> next(Light light) {
			Stream<Direction> nextDirections = switch (light.direction()) {
				case RIGHT, LEFT -> vertical ? Stream.of(Direction.UP, Direction.DOWN) : Stream.of(light.direction());
				case DOWN, UP -> vertical ? Stream.of(light.direction()) : Stream.of(Direction.LEFT, Direction.RIGHT);
			};
			return nextDirections.map(d -> new Light(light.position().shift(d), d));
		}
	}
	
	record Reflector(boolean rightTop) implements Mirror {
		@Override
		public Stream<Light> next(Light light) {
			Direction nextDirection = switch (light.direction) {
				case RIGHT -> rightTop ? Direction.UP : Direction.DOWN;
				case LEFT -> rightTop ? Direction.DOWN : Direction.UP;
				case DOWN -> rightTop ? Direction.LEFT : Direction.RIGHT;
				case UP -> rightTop ? Direction.RIGHT : Direction.LEFT;
			};
			return Stream.of(new Light(light.position().shift(nextDirection), nextDirection));
		}
	}
	
	record Light(Point position, Direction direction) {
	}
}