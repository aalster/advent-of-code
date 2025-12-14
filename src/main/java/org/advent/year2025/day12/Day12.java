package org.advent.year2025.day12;

import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day12 extends AdventDay {
	
	public static void main(String[] args) {
//		new DayRunner(new Day12()).runAll();
		new DayRunner(new Day12()).run("example.txt", 1);
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 2, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 569, ExpectedAnswers.IGNORE)
		);
	}
	
	Shape[] shapes;
	List<Region> regions;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<List<String>> groups = Utils.splitByEmptyLine(Utils.readLines(input));
		shapes = new Shape[groups.size() - 1];
		for (List<String> group : groups) {
			if (group.getFirst().contains("x")) {
				regions = group.stream().map(Region::parse).toList();
			} else {
				int index = Integer.parseInt(group.getFirst().replace(":", ""));
				shapes[index] = Shape.parse(group.subList(1, group.size()));
			}
		}
	}
	
	@Override
	public Object part1() {
//		for (Shape shape : shapes) {
//			shape.print();
//			System.out.println("variations:");
//			shape.variations().forEach(Shape::print);
//			System.out.println("-----------------------");
//		}
		Map<Integer, List<Shape>> variationsCache = new HashMap<>();
		return regions.stream()
//				.limit(1)
				.filter(r -> r.fits(shapes, variationsCache))
				.count();
	}
	
	@Override
	public Object part2() {
		return null;
	}
	
	record Shape(Set<Point> points, Rect bounds, char repr, Map<Point, Character> shapesRepr) {
		
		Shape(Set<Point> points) {
			this(points, 'A', new HashMap<>());
		}
		
		Shape(Set<Point> points, char repr, Map<Point, Character> shapesRepr) {
			this(points, points.isEmpty() ? null : Point.bounds(points), repr, shapesRepr);
		}
		
		boolean intersects(Shape other) {
			return bounds != null && other.bounds != null && bounds.intersectsInclusive(other.bounds)
					&& other.points.stream().anyMatch(points::contains);
		}
		
		Shape add(Shape other) {
			Map<Point, Character> nextShapesRepr = new HashMap<>(shapesRepr);
			other.points.forEach(p -> nextShapesRepr.put(p, repr));
			return new Shape(Utils.combineToSet(points, other.points), (char) (repr + 1), nextShapesRepr);
		}
		
		Shape move(Point delta) {
			return new Shape(points.stream().map(delta::shift).collect(Collectors.toSet()));
		}
		
		Shape moveToZero() {
			Point min = bounds.topLeft();
			return min.equals(Point.ZERO) ? this : move(min.scale(-1));
		}
		
		List<Shape> variations() {
			List<Set<Point>> temp = List.of(
					points,
					points.stream().map(p -> new Point(bounds.maxX() - p.x(), p.y())).collect(Collectors.toSet()));
			
			List<Set<Point>> variations = new ArrayList<>(temp);
			for (int i = 0; i < 3; i++) {
				temp = temp.stream()
						.map(points -> points.stream().map(p -> new Point(p.y(), -p.x())).collect(Collectors.toSet()))
						.toList();
				variations.addAll(temp);
			}
			
			return variations.stream().map(Shape::new).map(Shape::moveToZero).distinct().toList();
		}
		
		void print() {
			if (shapesRepr.isEmpty())
				Point.printField(points, '#', '.');
			else
				Point.printField(points, p -> shapesRepr.getOrDefault(p, '.'));
			System.out.println();
		}
		
		static Shape parse(List<String> lines) {
			return new Shape(new HashSet<>(Point.readField(lines).get('#'))).moveToZero();
		}
	}
	
	record Region(int width, int height, int[] presents) {
		
		public boolean fits(Shape[] shapes, Map<Integer, List<Shape>> variationsCache) {
			if ((width / 3) * (height / 3) >= Arrays.stream(presents).sum())
				return true;
			if (width * height < IntStream.range(0, presents.length).map(p -> presents[p] * shapes[p].points.size()).sum())
				return false;
			
			return fitRecursive(new Shape(Set.of()), shapes, 0, presents, variationsCache);
		}
		
		boolean fitRecursive(Shape field, Shape[] shapes, int index, int[] target, Map<Integer, List<Shape>> variationsCache) {
//			field.print();
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				throw new RuntimeException(e);
//			}
//			System.out.println("\n\n\n");
			
			if (field.bounds != null && (field.bounds.maxX() >= width || field.bounds.maxY() >= height))
				return false;
			if (index >= shapes.length) {
				field.print();
				return true;
			}
			if (target[index] == 0)
				return fitRecursive(field, shapes, index + 1, target, variationsCache);
			
			Shape shape = shapes[index];
			int[] nextTarget = Arrays.copyOf(target, target.length);
			nextTarget[index]--;
			
			for (int x = 0; x < width - 2; x++) {
				for (int y = 0; y < height - 2; y++) {
					Point delta = new Point(x, y);
					
					List<Shape> variations = variationsCache.computeIfAbsent(index, k -> shape.variations());
					for (Shape variation : variations) {
						Shape current = variation.move(delta);
						if (field.intersects(current))
							continue;
						
						if (fitRecursive(field.add(current), shapes, index, nextTarget, variationsCache))
							return true;
					}
				}
			}
			
			return false;
		}
		
		static Region parse(String line) {
			String[] split = line.split(": ");
			String[] size = split[0].split("x");
			int[] presents = Arrays.stream(split[1].split(" ")).mapToInt(Integer::parseInt).toArray();
			return new Region(Integer.parseInt(size[0]), Integer.parseInt(size[1]), presents);
		}
	}
}