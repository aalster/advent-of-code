package org.advent.year2017.day20;

import org.advent.common.Point3D;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Day20 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day20()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 0, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 1),
				new ExpectedAnswers("input.txt", 457, 448)
		);
	}
	
	List<Particle> particles;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		particles = new ArrayList<>();
		int index = 0;
		for (String line : Utils.readLines(input)) {
			particles.add(Particle.parse(index, line));
			index++;
		}
	}
	
	@Override
	public Object part1() {
		Comparator<Point3D> pointComparator = Comparator.comparing(Point3D.ZERO::distanceTo);
		Comparator<Particle> particleComparator = Comparator.comparing(Particle::acc, pointComparator)
				.thenComparing(Particle::vel, pointComparator)
				.thenComparing(Particle::pos, pointComparator);
		return particles.stream().min(particleComparator).map(Particle::id).orElseThrow();
	}
	
	@Override
	public Object part2() {
		List<Particle> unprocessed = new ArrayList<>(particles);
		int unharmed = 0;
		while (!unprocessed.isEmpty()) {
			Particle particle = unprocessed.removeFirst();
			if (!unprocessed.removeIf(particle::collides))
				unharmed++;
		}
		return unharmed;
	}
	
	record Particle(int id, Point3D pos, Point3D vel, Point3D acc) {
		
		boolean collides(Particle other) {
			double[] collisionsX = collisionTime(pos.x() - other.pos.x(), vel.x() - other.vel.x(), acc.x() - other.acc.x());
			double[] collisionsY = collisionTime(pos.y() - other.pos.y(), vel.y() - other.vel.y(), acc.y() - other.acc.y());
			double[] collisionsZ = collisionTime(pos.z() - other.pos.z(), vel.z() - other.vel.z(), acc.z() - other.acc.z());
			
			for (double possibleCollision : ArrayUtils.addAll(collisionsX, ArrayUtils.addAll(collisionsY, collisionsZ))) {
				if (possibleCollision >= 0) {
					int time = (int) possibleCollision;
					if (time == possibleCollision && positionAt(time).equals(other.positionAt(time)))
						return true;
				}
			}
			return false;
		}
		
		double[] collisionTime(int deltaPos, int deltaVel, int deltaAcc) {
			// S = pos + vel * t + acc * (t + 1) * t / 2
			// S = acc / 2 * t^2 + (acc / 2 + vel) * t + pos
			double a = 0.5 * deltaAcc;
			double b = a + deltaVel;
			double c = deltaPos;
			
			if (deltaAcc == 0)
				return deltaVel == 0 ? new double[]{} : new double[]{-c / b};
			
			return solveQuadratic(a, b, c);
		}
		
		public static double[] solveQuadratic(double a, double b, double c) {
			double discriminant = b * b - 4 * a * c;
			if (discriminant < 0)
				return new double[]{};
			
			if (Math.abs(discriminant) < 1e-6)
				return new double[]{-b / (2 * a)};
			
			double sqrtD = Math.sqrt(discriminant);
			return new double[]{(-b - sqrtD) / (2 * a), (-b + sqrtD) / (2 * a)};
		}
		
		Point3D positionAt(int time) {
			return new Point3D(
				coordinateAt(pos.x(), vel.x(), acc.x(), time),
				coordinateAt(pos.y(), vel.y(), acc.y(), time),
				coordinateAt(pos.z(), vel.z(), acc.z(), time)
			);
		}
		
		int coordinateAt(int pos, int vel, int acc, int time) {
			return pos + vel * time + acc * (time + 1) * time / 2;
		}
		
		static Particle parse(int id, String line) {
			Point3D[] points = Arrays.stream(line.split(", "))
					.map(s -> s.split("=")[1])
					.map(s -> s.substring(1, s.length() - 1).replace(" ", ""))
					.map(Point3D::parse)
					.toArray(Point3D[]::new);
			return new Particle(id, points[0], points[1], points[2]);
		}
	}
}