package org.advent.year2023.day24;

import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day24 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day24()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 2, 47),
				new ExpectedAnswers("input.txt", 15558, 765636044333842L)
		);
	}
	
	List<String> lines;
	long min;
	long max;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
		min = switch (file) {
			case "example.txt" -> 7;
			case "input.txt" -> 200000000000000L;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
		max = switch (file) {
			case "example.txt" -> 27;
			case "input.txt" -> 400000000000000L;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		Hailstone[] hailstones = lines.stream().map(Hailstone::parse).toArray(Hailstone[]::new);
		long result = 0;
		
		for (int i = 0; i < hailstones.length; i++) {
			Hailstone hailstone = hailstones[i];
			for (int j = i + 1; j < hailstones.length; j++) {
				Hailstone other = hailstones[j];
				if (hailstone == other)
					continue;
				
				DoublePoint intersection = hailstone.intersection(other);
				if (intersection == null)
					continue;
				if (hailstone.inFuture(intersection) && other.inFuture(intersection) && intersection.inSquareInclusive(min, max))
					result++;
			}
		}
		return result;
	}
	
	// Взято с https://github.com/SimonBaars/AdventOfCode-Java
	@Override
	public Object part2() {
		Hailstone3D[] hailstones = lines.stream().map(Hailstone3D::parse).toArray(Hailstone3D[]::new);
		
		int attempts = 1;
		
		for (int i = 0; i < hailstones.length; i++) {
			Hailstone3D h1 = hailstones[i];
			for (int j = i + 1; j < hailstones.length; j++) {
				Hailstone3D h2 = hailstones[j];
				for (int k = j + 1; k < hailstones.length; k++) {
					Hailstone3D h3 = hailstones[k];
					findRockPosition(h1, h2, h3);
					attempts--;
					if (attempts <= 0)
						return 0;
				}
			}
		}
		return 0;
	}
	
	void findRockPosition(Hailstone3D h1, Hailstone3D h2, Hailstone3D h3) {
		System.out.println("\nVisit https://live.sympy.org/\n");
		Hailstone3D[] hailstones = new Hailstone3D[] {h1, h2, h3};
		for (int i = 0; i < hailstones.length; i++) {
			List<Pair<String, Double>> variables = hailstones[i].variables();
			for (Pair<String, Double> entry : variables)
				System.out.println(entry.left() + (i + 1) + " = " + entry.right());
		}
		
		System.out.println("""
				variables = (symbols('res, x0, y0, z0, vx0, vy0, vz0, t1, t2, t3'))
				
				equations = [
					Eq(x0 + vx0*t1, x1 + vx1*t1),
					Eq(y0 + vy0*t1, y1 + vy1*t1),
					Eq(z0 + vz0*t1, z1 + vz1*t1),
					Eq(x0 + vx0*t2, x2 + vx2*t2),
					Eq(y0 + vy0*t2, y2 + vy2*t2),
					Eq(z0 + vz0*t2, z2 + vz2*t2),
					Eq(x0 + vx0*t3, x3 + vx3*t3),
					Eq(y0 + vy0*t3, y3 + vy3*t3),
					Eq(z0 + vz0*t3, z3 + vz3*t3),
					Eq(res, x0 + y0 + z0)
				]
				
				solution = solve(equations, variables)
				print(solution)
				print("Answer 2: ", solution[0][0])
				""");
	}
	
//	static Point findRockPosition(Hailstone3D h1, Hailstone3D h2, Hailstone3D h3) {
//		ExprEvaluator evaluator = new ExprEvaluator();
//		Hailstone3D[] hailstones = new Hailstone3D[] {h1, h2, h3};
//		for (int i = 0; i < hailstones.length; i++) {
//			Map<String, Double> variables = hailstones[i].variables();
//			for (Map.Entry<String, Double> entry : variables.entrySet())
//				evaluator.defineVariable(entry.getKey() + (i + 1), entry.getValue());
//		}
//
//		String equations = "{"
//				+ "x0 + vx0*t1 == x1 + vx1*t1, "
//				+ "y0 + vy0*t1 == y1 + vy1*t1, "
//				+ "z0 + vz0*t1 == z1 + vz1*t1, "
//				+ "x0 + vx0*t2 == x2 + vx2*t2, "
//				+ "y0 + vy0*t2 == y2 + vy2*t2, "
//				+ "z0 + vz0*t2 == z2 + vz2*t2, "
//				+ "x0 + vx0*t3 == x3 + vx3*t3, "
//				+ "y0 + vy0*t3 == y3 + vy3*t3, "
//				+ "z0 + vz0*t3 == z3 + vz3*t3"
//				+ "}";
//		String variables = "{x0, y0, z0, vx0, vy0, vz0, t1, t2, t3}";
//
//		IExpr result = evaluator.eval("Solve(" + equations + ", " + variables + ")");
//		System.out.println(result);
//		System.out.println(result.isList());
//		return null;
//	}
	
	
	record Hailstone(DoublePoint p, DoublePoint v, double a, double b) {
		
		DoublePoint intersection(Hailstone other) {
			if (isParallel(other))
				return null;
			double x = - (b - other.b) / (a - other.a);
			double y = a * x + b;
			return new DoublePoint(x, y);
		}
		
		boolean isParallel(Hailstone other) {
			return v.x * other.v.y == v.y * other.v.x;
		}
		
		boolean inFuture(DoublePoint point) {
			return 0 < v.x == p.x < point.x;
		}
		
		static Hailstone create(DoublePoint p, DoublePoint v) {
			double a = v.y / v.x;
			double b = p.y - p.x * a;
			return new Hailstone(p, v, a, b);
		}
		
		@Override
		public String toString() {
			return p.x + ", " + p.y + " @ " + v.x + ", " + v.y;
		}
		
		static Hailstone parse(String line) {
			String[] split = line.replace(" ", "").split("@");
			return Hailstone.create(DoublePoint.parse(split[0]), DoublePoint.parse(split[1]));
		}
	}
	
	record Hailstone3D(DoublePoint3D p, DoublePoint3D v) {
		
		List<Pair<String, Double>> variables() {
			return List.of(
					Pair.of("x", p.x), Pair.of("y", p.y), Pair.of("z", p.z),
					Pair.of("vx", v.x), Pair.of("vy", v.y), Pair.of("vz", v.z));
		}
		
		static Hailstone3D parse(String line) {
			String[] split = line.replace(" ", "").split("@");
			return new Hailstone3D(DoublePoint3D.parse(split[0]), DoublePoint3D.parse(split[1]));
		}
	}
	
	record DoublePoint(double x, double y) {
		boolean inSquareInclusive(long min, long max) {
			return min <= x && x <= max && min <= y && y <= max;
		}
		
		static DoublePoint parse(String line) {
			String[] split = line.split(",");
			return new DoublePoint(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
		}
	}
	
	record DoublePoint3D(double x, double y, double z) {
		
		static DoublePoint3D parse(String line) {
			double[] array = Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray();
			return new DoublePoint3D(array[0], array[1], array[2]);
		}
	}
}