package org.advent.year2019.day12;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.advent.common.Axis3D;
import org.advent.common.NumbersAdventUtils;
import org.advent.common.Point3D;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

public class Day12 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day12()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 179, 2772),
				new ExpectedAnswers("example2.txt", 1940, 4686774924L),
				new ExpectedAnswers("input.txt", 5350, 467034091553512L)
		);
	}
	
	Moon[] moons;
	int steps;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		moons = Utils.readLines(input).stream()
				.map(l -> Utils.removeEach(l, "<", ">", " ", "=", "x", "y", "z"))
				.map(Point3D::parse)
				.map(p -> new Moon(p, Point3D.ZERO))
				.toArray(Moon[]::new);
		steps = switch (file) {
			case "example.txt" -> 10;
			case "example2.txt" -> 100;
			case "input.txt" -> 1000;
			default -> throw new IllegalStateException("Unexpected value: " + file);
		};
	}
	
	@Override
	public Object part1() {
		for (int step = 0; step < steps; step++) {
			Moon.applyGravity(moons);
			Moon.applyVelocity(moons);
		}
		return Stream.of(moons)
				.mapToInt(m -> m.position.distanceTo(Point3D.ZERO) * m.velocity.distanceTo(Point3D.ZERO))
				.sum();
	}
	
	@Override
	public Object part2() {
		Point3D repeats = Point3D.ZERO;
		for (Axis3D axis : Axis3D.values()) {
			List<Moon1D> dimension = Stream.of(moons).map(m -> m.getDimension(axis)).toList();
			Set<List<Moon1D>> history = new HashSet<>();
			int steps = 0;
			while (true) {
				dimension = Moon1D.step(dimension);
				if (!history.add(dimension)) {
					repeats = axis.shift(repeats, steps);
					break;
				}
				steps++;
			}
		}
		return NumbersAdventUtils.lcm(repeats.toArray());
	}
	
	@Data
	@AllArgsConstructor
	static class Moon {
		Point3D position;
		Point3D velocity;
		
		Moon1D getDimension(Axis3D axis) {
			return new Moon1D(axis.ofPoint(position), axis.ofPoint(velocity));
		}
		
		static void applyGravity(Moon[] moons) {
			for (int i = 0; i < moons.length; i++) {
				Moon left = moons[i];
				for (int j = i + 1; j < moons.length; j++) {
					Moon right = moons[j];
					for (Axis3D axis : Axis3D.values()) {
						int compare = Integer.compare(axis.ofPoint(left.position), axis.ofPoint(right.position));
						if (compare != 0) {
							left.velocity = axis.shift(left.velocity, -compare);
							right.velocity = axis.shift(right.velocity, compare);
						}
					}
				}
			}
		}
		
		static void applyVelocity(Moon[] moons) {
			for (Moon moon : moons)
				moon.position = moon.position.shift(moon.velocity);
		}
	}
	
	record Moon1D(int position, int velocity) {
		
		int applyGravity(List<Moon1D> moons) {
			return moons.stream().mapToInt(moon -> Integer.compare(moon.position, position)).sum();
		}
		
		static List<Moon1D> step(List<Moon1D> moons) {
			List<Moon1D> nextMoons = new ArrayList<>();
			for (Moon1D moon : moons) {
				int nextVelocity = moon.velocity + moon.applyGravity(moons);
				nextMoons.add(new Moon1D(moon.position + nextVelocity, nextVelocity));
			}
			return nextMoons;
		}
	}
}