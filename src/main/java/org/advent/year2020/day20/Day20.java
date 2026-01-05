package org.advent.year2020.day20;

import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day20 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day20()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 20899048083289L, 273),
				new ExpectedAnswers("input.txt", 19955159604613L, 1639)
		);
	}
	
	List<Tile> tiles;
	Grid seaMonster;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		tiles = Utils.splitByEmptyLine(Utils.readLines(input)).stream().map(Tile::parse).toList();
		
		String seaMonsterPattern = """
				                  #\s
				#    ##    ##    ###
				 #  #  #  #  #  #  \s""";
		seaMonster = Grid.parse(Arrays.asList(seaMonsterPattern.split("\n")));
	}
	
	@Override
	public Object part1() {
		Map<Integer, Long> sideBitsStats = tiles.stream()
				.flatMap(tile -> tile.allSides.stream())
				.collect(Collectors.groupingBy(i -> i, Collectors.counting()));
		return tiles.stream()
				.filter(tile -> tile.sides.values().stream()
						.map(s -> sideBitsStats.get(s.bits()))
						.filter(c -> c == 1L)
						.count() == 2)
				.mapToLong(tile -> tile.id)
				.reduce(1, (a, b) -> a * b);
	}
	
	@Override
	public Object part2() {
		Grid grid = Grid.combine(arrange(tiles));
		return grid.countPoints() - grid.findMaxPatterns(seaMonster) * seaMonster.countPoints();
	}
	
	Map<Point, Grid> arrange(List<Tile> tiles) {
		List<Tile> unprocessed = new ArrayList<>(tiles);
		List<Tile> queue = new ArrayList<>();
		Map<Integer, Point> positions = new HashMap<>();
		Map<Point, Grid> grid = new HashMap<>();
		
		Tile first = unprocessed.removeFirst();
		queue.add(first);
		grid.put(Point.ZERO, first.grid);
		positions.put(first.id, Point.ZERO);
		
		while (!queue.isEmpty()) {
			Tile current = queue.removeFirst();
			Point position = positions.get(current.id);
			for (Map.Entry<Direction, Side> entry : current.sides.entrySet()) {
				Direction direction = entry.getKey();
				Point targetPosition = direction.shift(position);
				if (grid.containsKey(targetPosition))
					continue;
				
				int sideBits = entry.getValue().bits;
				List<Tile> candidates = unprocessed.stream().filter(t -> t.allSides.contains(sideBits)).toList();
				if (candidates.size() == 1) {
					Tile candidate = candidates.getFirst();
					unprocessed.remove(candidate);
					
					candidate = candidate.orient(direction, sideBits);
					queue.add(candidate);
					
					grid.put(targetPosition, candidate.grid);
					positions.put(candidate.id, targetPosition);
				}
			}
		}
		if (!unprocessed.isEmpty())
			throw new RuntimeException("Unprocessed tiles!");
		return grid;
	}
	
	record Side(int bits, int bitsReversed) {
		
		Stream<Integer> all() {
			return Stream.of(bits, bitsReversed);
		}
		
		Side flip() {
			return new Side(bitsReversed, bits);
		}
		
		static Side horizontal(char[] points) {
			int bits = 0;
			for (char point : points) {
				bits <<= 1;
				bits |= (point == '#' ? 1 : 0);
			}
			return new Side(bits, reverseBits(bits, points.length));
		}
		
		static Side vertical(char[][] points, int index) {
			char[] line = new char[points[0].length];
			for (int y = 0; y < points.length; y++)
				line[y] = points[y][index];
			return horizontal(line);
		}
		
		static int reverseBits(int bits, int size) {
			int reversed = 0;
			int mask = 1;
			for (int i = 0; i < size; i++) {
				reversed <<= 1;
				reversed |= (bits & mask) > 0 ? 1 : 0;
				mask <<= 1;
			}
			return reversed;
		}
	}
	
	record Grid(char[][] points) {
		
		Grid rotateRight() {
			char[][] nextPoints = new char[points[0].length][points.length];
			for (int y = 0; y < points.length; y++)
				for (int x = 0; x < points[0].length; x++)
					nextPoints[x][points.length - y - 1] = points[y][x];
			
			return new Grid(nextPoints);
		}
		
		Grid flip() {
			char[][] nextPoints = new char[points.length][points[0].length];
			for (int y = 0; y < points.length; y++)
				nextPoints[y] = Arrays.copyOf(points[points.length - y - 1], points[0].length);
			
			return new Grid(nextPoints);
		}
		
		Grid shrink() {
			char[][] nextPoints = new char[points.length - 2][];
			for (int y = 1; y < points.length - 1; y++)
				nextPoints[y - 1] = Arrays.copyOfRange(points[y], 1, points[y].length - 1);
			
			return new Grid(nextPoints);
		}
		
		int countPoints() {
			int result = 0;
			for (char[] row : points)
				for (char c : row)
					if (c == '#')
						result++;
			return result;
		}
		
		int findMaxPatterns(Grid pattern) {
			List<Grid> current = List.of(pattern, pattern.flip());
			List<Grid> patterns = new ArrayList<>(current);
			
			for (int i = 0; i < 3; i++) {
				current = current.stream().map(Grid::rotateRight).toList();
				patterns.addAll(current);
			}
			return patterns.stream().mapToInt(this::findPattern).max().orElse(0);
		}
		
		int findPattern(Grid pattern) {
			int result = 0;
			for (int dy = 0; dy < points.length - pattern.points.length; dy++)
				for (int dx = 0; dx < points[0].length - pattern.points[0].length; dx++)
					result += patternMatches(pattern, dx, dy) ? 1 : 0;
			return result;
		}
		
		boolean patternMatches(Grid pattern, int dx, int dy) {
			for (int y = 0; y < pattern.points.length; y++)
				for (int x = 0; x < pattern.points[y].length; x++)
					if (pattern.points[y][x] == '#' && points[dy + y][dx + x] != '#')
						return false;
			return true;
		}
		
		Map<Direction, Side> sides() {
			return Map.of(
					Direction.UP, Side.horizontal(points[0]),
					Direction.RIGHT, Side.vertical(points, points[0].length - 1),
					Direction.DOWN, Side.horizontal(points[points.length - 1]).flip(),
					Direction.LEFT, Side.vertical(points, 0).flip()
			);
		}

		static Grid combine(Map<Point, Grid> grids) {
			Rect bounds = Point.bounds(grids.keySet());
			Grid example = grids.values().iterator().next();
			int width = example.points[0].length;
			int height = example.points.length;
			char[][] result = new char[(bounds.maxY() - bounds.minY() + 1) * height][(bounds.maxX() - bounds.minX() + 1) * width];
			
			for (Map.Entry<Point, Grid> entry : grids.entrySet()) {
				Point position = entry.getKey().subtract(bounds.topLeft());
				int dx = position.x() * width;
				int dy = position.y() * height;
				for (char[] row : entry.getValue().points) {
					System.arraycopy(row, 0, result[dy], dx, row.length);
					dy++;
				}
			}
			return new Grid(result);
		}
		
		static Grid parse(List<String> lines) {
			return new Grid(lines.stream().map(String::toCharArray).toArray(char[][]::new));
		}
	}
	
	record Tile(int id, Grid grid, Map<Direction, Side> sides, Set<Integer> allSides) {
		
		Tile orient(Direction direction, int sideBits) {
			if (!allSides.contains(sideBits))
				throw new RuntimeException("Side not found: " + sideBits);
			direction = direction.reverse();
			Tile result = this;
			if (result.sides.values().stream().map(Side::bits).anyMatch(b -> b == sideBits))
				result = result.flip();
			while (result.sides.get(direction).bitsReversed != sideBits)
				result = result.rotateRight();
			return result;
		}
		
		Tile rotateRight() {
			Map<Direction, Side> nextSides = sides.entrySet().stream().collect(Collectors.toMap(
					e -> e.getKey().rotate(Direction.RIGHT),
					Map.Entry::getValue));
			
			return new Tile(id, grid.rotateRight(), nextSides, allSides);
		}
		
		Tile flip() {
			Map<Direction, Side> nextSides = sides.entrySet().stream().collect(Collectors.toMap(
					e -> e.getKey().isVertical() ? e.getKey().reverse() : e.getKey(),
					e -> e.getValue().flip()));
			
			return new Tile(id, grid.flip(), nextSides, allSides);
		}
		
		static Tile parse(List<String> lines) {
			int id = Integer.parseInt(StringUtils.getDigits(lines.getFirst()));
			Grid grid = Grid.parse(lines.subList(1, lines.size()));
			Map<Direction, Side> sides = grid.sides();
			Set<Integer> allSides = sides.values().stream().flatMap(Side::all).collect(Collectors.toSet());
			if (allSides.size() != 8)
				throw new RuntimeException("Same sides: " + id);
			return new Tile(id, grid.shrink(), sides, allSides);
		}
	}
}