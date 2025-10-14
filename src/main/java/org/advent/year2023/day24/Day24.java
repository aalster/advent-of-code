package org.advent.year2023.day24;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.math.BigInteger;
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
		
		Rational minR = Rational.of(min);
		Rational maxR = Rational.of(max);
		
		for (int i = 0; i < hailstones.length; i++) {
			Hailstone hailstone = hailstones[i];
			for (int j = i + 1; j < hailstones.length; j++) {
				Hailstone other = hailstones[j];
				
				RationalPoint intersection = hailstone.intersection(other);
				if (intersection == null)
					continue;
				if (hailstone.inFuture(intersection) && other.inFuture(intersection) && intersection.inSquareInclusive(minR, maxR))
					result++;
			}
		}
		return result;
	}
	
	@Override
	public Object part2() {
		Hailstone3D[] hailstones = lines.stream().map(Hailstone3D::parse).toArray(Hailstone3D[]::new);
		
		Hailstone3D base = hailstones[0];
		Hailstone3D h1 = hailstones[1];
		Hailstone3D h2 = hailstones[2];
		Hailstone3D h1Relative = h1.sub(base);
		Hailstone3D h2Relative = h2.sub(base);
		
		Rational t1 = intersectionTime(h1Relative, h2Relative);
		Rational t2 = intersectionTime(h2Relative, h1Relative);
		
		// c1 = position_1 + t1 * velocity_1
		// c2 = position_2 + t2 * velocity_2
		// vel = (c2 - c1) / (t2 - t1)
		// pos = c1 - t1 * vel
		RationalPoint3D intersection1 = h1.pos.add(h1.vel.mul(t1));
		RationalPoint3D intersection2 = h2.pos.add(h2.vel.mul(t2));
		
		RationalPoint3D rockVel = intersection2.sub(intersection1).div(t2.sub(t1));
		RationalPoint3D rockPos = intersection1.sub(rockVel.mul(t1));
		
		return rockPos.x.add(rockPos.y).add(rockPos.z).longValueExact();
	}
	
	Rational intersectionTime(Hailstone3D h1, Hailstone3D h2) {
//		t1 = -((p1 x p2) * v2) / ((v1 x p2) * v2);
		return h1.pos.cross(h2.pos).scalarMul(h2.vel)
				.div(h1.vel.cross(h2.pos).scalarMul(h2.vel))
				.mul(Rational.of(-1));
	}
	
	
	record Hailstone(RationalPoint p, RationalPoint v, Rational a, Rational b) {
		
		RationalPoint intersection(Hailstone other) {
			Rational x = b.sub(other.b).div(a.sub(other.a)).mul(Rational.of(-1));
			if (!x.isValid())
				return null;
			Rational y = a.mul(x).add(b);
			return new RationalPoint(x, y);
		}
		
		boolean inFuture(RationalPoint point) {
			return -v.x.signum() == p.x.compareTo(point.x);
		}
		
		@Override
		public String toString() {
			return p.x + ", " + p.y + " @ " + v.x + ", " + v.y;
		}
		
		static Hailstone create(RationalPoint p, RationalPoint v) {
			Rational a = v.y.div(v.x);
			Rational b = p.y.sub(p.x.mul(a));
			return new Hailstone(p, v, a, b);
		}
		
		static Hailstone parse(String line) {
			String[] split = line.replace(" ", "").split("@");
			return Hailstone.create(RationalPoint.parse(split[0]), RationalPoint.parse(split[1]));
		}
	}
	
	record Hailstone3D(RationalPoint3D pos, RationalPoint3D vel) {
		
		Hailstone3D sub(Hailstone3D delta) {
			return new Hailstone3D(pos.sub(delta.pos), vel.sub(delta.vel));
		}
		
		static Hailstone3D parse(String line) {
			String[] split = line.replace(" ", "").split("@");
			return new Hailstone3D(RationalPoint3D.parse(split[0]), RationalPoint3D.parse(split[1]));
		}
	}
	
	record RationalPoint(Rational x, Rational y) {
		
		boolean inSquareInclusive(Rational min, Rational max) {
			return min.compareTo(x) <= 0 && x.compareTo(max) <= 0
					&& min.compareTo(y) <= 0 && y.compareTo(max) <= 0;
		}
		
		static RationalPoint parse(String line) {
			long[] array = Arrays.stream(line.split(",")).mapToLong(Long::parseLong).toArray();
			return new RationalPoint(Rational.of(array[0]), Rational.of(array[1]));
		}
	}
	
	record RationalPoint3D(Rational x, Rational y, Rational z) {
		
		RationalPoint3D mul(Rational value) {
			return new RationalPoint3D(x.mul(value), y.mul(value), z.mul(value));
		}
		
		RationalPoint3D div(Rational value) {
			return new RationalPoint3D(x.div(value), y.div(value), z.div(value));
		}
		
		RationalPoint3D add(RationalPoint3D delta) {
			return new RationalPoint3D(x.add(delta.x), y.add(delta.y), z.add(delta.z));
		}
		
		RationalPoint3D sub(RationalPoint3D delta) {
			return new RationalPoint3D(x.sub(delta.x), y.sub(delta.y), z.sub(delta.z));
		}
		
		RationalPoint3D cross(RationalPoint3D other) {
			return new RationalPoint3D(
					y.mul(other.z).sub(z.mul(other.y)),
					z.mul(other.x).sub(x.mul(other.z)),
					x.mul(other.y).sub(y.mul(other.x)));
		}
		
		Rational scalarMul(RationalPoint3D other) {
			return x.mul(other.x).add(y.mul(other.y)).add(z.mul(other.z));
		}
		
		static RationalPoint3D parse(String line) {
			long[] array = Arrays.stream(line.split(",")).mapToLong(Long::parseLong).toArray();
			return new RationalPoint3D(Rational.of(array[0]), Rational.of(array[1]), Rational.of(array[2]));
		}
	}
	
	record Rational(BigInteger numerator, BigInteger denominator) implements Comparable<Rational> {
		
		Rational add(Rational other) {
			return new Rational(
					numerator.multiply(other.denominator).add(other.numerator.multiply(denominator)),
					denominator.multiply(other.denominator));
		}
		
		Rational sub(Rational other) {
			return new Rational(
					numerator.multiply(other.denominator).subtract(other.numerator.multiply(denominator)),
					denominator.multiply(other.denominator));
		}
		
		Rational mul(Rational other) {
			return new Rational(numerator.multiply(other.numerator), denominator.multiply(other.denominator));
		}
		
		Rational div(Rational other) {
			return new Rational(numerator.multiply(other.denominator), denominator.multiply(other.numerator));
		}
		
		boolean isValid() {
			return denominator.signum() != 0;
		}
		
		long longValueExact() {
			if (!isValid() || !numerator.mod(denominator.abs()).equals(BigInteger.ZERO))
				throw new ArithmeticException("Not an integer");
			return numerator.divide(denominator).longValue();
		}
		
		static Rational of(long numerator) {
			return new Rational(BigInteger.valueOf(numerator), BigInteger.ONE);
		}
		
		int signum() {
			return numerator.signum() * denominator.signum();
		}
		
		@Override
		public int compareTo(Rational other) {
			return sub(other).signum();
		}
		
		@Override
		public String toString() {
			return numerator + "/" + denominator;
		}
	}
	
	/*
	https://www.reddit.com/r/adventofcode/comments/18pnycy/comment/kxqjg33/
	
	DaveBaum
	
	A little linear algebra makes part 2 very straightforward. You don't even need to solve a system of equations. It helps to view everything relative to hailstone 0. Let position_x and velocity_x be the position and velocity of hailstone x.
	
	Stones 1 and 2, relative to stone 0:
	p1 = position_1 - position_0
	v1 = velocity_1 - velocity_0
	p2 = position_2 - position_0
	v2 = velocity_2 - velocity_0
	
	Let t1 and t2 be the times that the rock collides with hailstones 1 and 2 respectively.
	
	Viewed from hailstone 0, the two collisions are thus at
	p1 + t1 * v1
	p2 + t2 * v2
	
	Hailstone 0 is always at the origin, thus its collision is at 0. Since all three collisions must form a straight line, the above two collision vectors must be collinear, and their cross product will be 0:
	
	(p1 + t1 * v1) x (p2 + t2 * v2) = 0
	
	Cross product is distributive with vector addition and compatible with scalar multiplication, so the above can be expanded:
	
	(p1 x p2) + t1 * (v1 x p2) + t2 * (p1 x v2) + t1 * t2 * (v1 x v2) = 0
	
	This is starting to look like a useful linear equation, except for that t1 * t2 term. Let's try to get rid of it. Dot product and cross product interact in a useful way. For arbitrary vectors a and b:
	
	(a x b) * a = (a x b) * b = 0.
	
	We can use this property to get rid of the t1 * t2 term. Let's take the dot product with v2. Note that dot product is also distributive with vector addition and compatible with scalar multiplication. The dot product zeros out both the t2 and t1*t2 terms, leaving a simple linear equation for t1:
	
	(p1 x p2) * v2 + t1 * (v1 x p2) * v2 = 0
	
	t1 = -((p1 x p2) * v2) / ((v1 x p2) * v2)
	
	If we use v1 instead of v2 for the dot product, we get this instead:
	
	(p1 x p2) * v1 + t2 * (p1 x v2) * v1 = 0
	
	t2 = -((p1 x p2) * v1) / ((p1 x v2) * v1)
	
	Once we have t1 and t2 we can compute the locations (in absolute coordinates) of the two collisions and work backwards to find the velocity and initial position of the rock.
	
	c1 = position_1 + t1 * velocity_1
	c2 = position_2 + t2 * velocity_2
	vel = (c2 - c1) / (t2 - t1)
	pos = c1 - t1 * vel
	 */
}