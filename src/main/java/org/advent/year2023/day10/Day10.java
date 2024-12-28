package org.advent.year2023.day10;

import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day10 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day10()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 4, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 8, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", ExpectedAnswers.IGNORE, 4),
				new ExpectedAnswers("example4.txt", ExpectedAnswers.IGNORE, 8),
				new ExpectedAnswers("example5.txt", ExpectedAnswers.IGNORE, 10),
				new ExpectedAnswers("input.txt", 6820, 337)
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
		Map<Point, Integer> steps = walkField(field);
		return steps.values().stream().mapToInt(i -> i).max().orElse(0);
	}
	
	@Override
	public Object part2() {
		Field field = Field.parse(lines);
		Set<Point> loop = walkField(field).keySet();
		Map<Point, Cell> fieldCells = field.field();
		
		int minY = Point.minY(loop);
		Point current = loop.stream().filter(p -> p.y() == minY).min(Comparator.comparing(Point::x)).orElseThrow();
		Direction forward = Direction.UP;
		
		Set<Point> enclosedCells = new HashSet<>();
		Set<Point> visited = new HashSet<>();
		
		while (visited.size() < loop.size()) {
			Direction forward_ = forward;
			Cell currentCell = fieldCells.get(current);
			Direction nextDirectionRelational = currentCell.directions().stream()
					.map(d -> d.rotate(forward_.mirror()))
					.filter(d -> d != Direction.DOWN)
					.findAny()
					.orElseThrow();
			Stream<Direction> insideDirections = switch (nextDirectionRelational) {
				case RIGHT -> Stream.of();
				case UP -> Stream.of(Direction.RIGHT);
				case LEFT -> Stream.of(Direction.RIGHT, Direction.UP);
				default -> throw new IllegalArgumentException("impossible");
			};
			insideDirections.map(forward::rotate)
					.map(current::shift)
					.filter(p -> !loop.contains(p))
					.forEach(enclosedCells::add);
			
			visited.add(current);
			forward = forward.rotate(nextDirectionRelational);
			current = current.shift(forward);
		}
		
		while (true) {
			Set<Point> neighbors = enclosedCells.stream()
					.flatMap(p -> Direction.stream().map(p::shift))
					.filter(p -> !loop.contains(p))
					.filter(p -> !enclosedCells.contains(p))
					.collect(Collectors.toSet());
			if (neighbors.isEmpty())
				break;
			enclosedCells.addAll(neighbors);
		}
		
		return enclosedCells.size();
	}
	
	Map<Point, Integer> walkField(Field field) {
		Cell start = field.start();
		Set<Cell> cells = Set.of(start);
		Map<Point, Integer> steps = new HashMap<>(Map.of(start.position(), 0));
		int step = 0;
		while (!cells.isEmpty()) {
			step++;
			cells = field.nextCells(cells, steps, step);
		}
		return steps;
	}
	
	private record Field(Map<Point, Cell> field, Cell start) {
		
		Set<Cell> nextCells(Set<Cell> cells, Map<Point, Integer> steps, int step) {
			return cells.stream()
					.flatMap(cell -> cell.directions().stream().map(d -> d.shift(cell.position())))
					.filter(p -> !steps.containsKey(p))
					.map(field::get)
					.filter(Objects::nonNull)
					.peek(cell -> steps.put(cell.position(), step))
					.collect(Collectors.toSet());
		}
		
		static Field parse(List<String> lines) {
			Map<Point, Cell> field = new HashMap<>();
			Cell start = null;
			int y = 0;
			for (String line : lines) {
				for (int x = 0; x < line.length(); x++) {
					Point position = new Point(x, y);
					Cell cell = Cell.parse(position, line.charAt(x));
					if (cell.symbol() == 'S')
						start = cell;
					field.put(position, cell);
				}
				y++;
			}
			
			if (start == null)
				throw new IllegalStateException("Start not found");
			
			Point startPosition = start.position();
			Set<Direction> startDirections = Direction.stream()
					.map(d -> Pair.of(d, d.shift(startPosition)))
					.map(pair -> Pair.of(pair.left(), field.get(pair.right())))
					.filter(pair -> pair.right() != null)
					.filter(pair -> pair.right().directions().contains(pair.left().reverse()))
					.map(Pair::left)
					.collect(Collectors.toSet());
			
			start = new Cell(start.position(), start.symbol(), startDirections);
			field.put(start.position(), start);
			
			return new Field(field, start);
		}
	}
	
	record Cell(Point position, char symbol, Set<Direction> directions) {
		
		static Cell parse(Point position, char symbol) {
			Set<Direction> directions = switch (symbol) {
				case '|' -> Set.of(Direction.UP, Direction.DOWN);
				case '-' -> Set.of(Direction.LEFT, Direction.RIGHT);
				case 'L' -> Set.of(Direction.UP, Direction.RIGHT);
				case 'J' -> Set.of(Direction.UP, Direction.LEFT);
				case '7' -> Set.of(Direction.LEFT, Direction.DOWN);
				case 'F' -> Set.of(Direction.RIGHT, Direction.DOWN);
				default -> Set.of();
			};
			return new Cell(position, symbol, directions);
		}
	}
}