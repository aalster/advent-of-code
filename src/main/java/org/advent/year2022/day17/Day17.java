package org.advent.year2022.day17;

import lombok.SneakyThrows;
import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day17 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day17()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 3068, 1514285714288L),
				new ExpectedAnswers("input.txt", 3171, 1586627906921L)
		);
	}
	
	List<String> rockLines;
	String windLine;
	
	@SneakyThrows
	@Override
	public void prepare(String file) {
		rockLines = Utils.readLines(Utils.scanFileNearClass(getClass(), "rocks.txt"));
		windLine = Utils.scanFileNearClass(getClass(), file).nextLine();
	}
	
	@Override
	public Object part1() {
		RocksFactory rocksFactory = new RocksFactory(rockLines);
		WindFactory windFactory = new WindFactory(windLine);
		Set<Point> fallen = play(Set.of(), rocksFactory, windFactory, 2022);
		return Point.maxY(fallen) + 1;
	}
	
	@Override
	public Object part2() {
		RocksFactory rocksFactory = new RocksFactory(rockLines);
		WindFactory windFactory = new WindFactory(windLine);
		long target = 1000000000000L;
		
		Set<Point> fallen = Set.of();
		
		int cycle;
		int cycleHeight;
		Map<HeadState, Stats> heads = new HashMap<>();
		int step = 0;
		while (true) {
			fallen = play(fallen, rocksFactory, windFactory, 1);
			step++;
			
			HeadState head = HeadState.of(fallen, rocksFactory, windFactory);
			Stats cycleStart = heads.get(head);
			if (cycleStart != null) {
				cycle = step - cycleStart.step;
				cycleHeight = Point.maxY(fallen) - cycleStart.maxY;
				break;
			}
			heads.put(head, new Stats(Point.maxY(fallen), step));
		}
		target -= step;
		
		fallen = play(fallen, rocksFactory, windFactory, (int) (target % cycle));
		target -= target % cycle;
		
		return Point.maxY(fallen) + 1 + (target / cycle) * cycleHeight;
	}
	
	private Set<Point> play(Set<Point> initial, RocksFactory rocksFactory, WindFactory windFactory, int rocksCount) {
		Set<Point> fallen = new HashSet<>(initial);
		
		int height = fallen.isEmpty() ? 0 : Point.maxY(fallen) + 1;
		
		for (int i = 0; i < rocksCount; i++) {
			Point startingPosition = new Point(2, 3 + height);
			List<Point> rock = rocksFactory.nextRock().stream().map(startingPosition::shift).toList();
			while (true) {
				Direction wind = windFactory.nextDirection();
				List<Point> windedRock = rock.stream().map(wind::shift).toList();
				if (windedRock.stream().noneMatch(p -> p.x() < 0 || 6 < p.x() || fallen.contains(p)))
					rock = windedRock;
				
				List<Point> loweredRock = rock.stream().map(Direction.UP::shift).toList();
				if (loweredRock.stream().anyMatch(p -> p.y() < 0 || fallen.contains(p)))
					break;
				rock = loweredRock;
			}
			fallen.addAll(rock);
			height = Math.max(height, Point.maxY(fallen) + 1);
			// Периодически удаляем точки, которые не повлияют на результат
			if (i % 100 == 0) {
				int skipHeight = height - HeadState.headStateSize;
				fallen.removeIf(p -> p.y() < skipHeight);
			}
		}
		return fallen;
	}
	
	static class RocksFactory {
		final List<List<Point>> rocks;
		int step = -1;
		
		public RocksFactory(List<String> lines) {
			rocks = Utils.splitByEmptyLine(lines).stream()
					.map(r -> Point.readField(r.reversed()).get('#'))
					.toList();
		}
		
		List<Point> nextRock() {
			step = (step + 1) % rocks.size();
			return rocks.get(step);
		}
		
	}
	
	static class WindFactory {
		final Direction[] directions;
		int step = -1;
		
		public WindFactory(String line) {
			directions = line.chars().mapToObj(c -> c == '<' ? Direction.LEFT : Direction.RIGHT).toArray(Direction[]::new);
		}
		
		Direction nextDirection() {
			step = (step + 1) % directions.length;
			return directions[step];
		}
	}
	
	record HeadState(Set<Point> head, int rockStep, int windStep) {
		static final int headStateSize = 50;
		
		static HeadState of(Set<Point> fallen, RocksFactory rocksFactory, WindFactory windFactory) {
			return new HeadState(head(fallen), rocksFactory.step, windFactory.step);
		}
		
		static Set<Point> head(Set<Point> fallen) {
			int headY = Point.maxY(fallen) - headStateSize;
			return fallen.stream()
					.filter(p -> headY < p.y())
					.map(p -> p.shift(0, -headY))
					.collect(Collectors.toSet());
		}
	}
	
	record Stats(int maxY, int step) {
	}
}