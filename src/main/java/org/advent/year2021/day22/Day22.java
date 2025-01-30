package org.advent.year2021.day22;

import org.advent.common.Point3D;
import org.advent.common.Region3D;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day22 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day22()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 590784, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 474140, 2758514936282235L),
				new ExpectedAnswers("input.txt", 611378, 1214313344725528L)
		);
	}
	
	List<Cuboid> cuboids;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		cuboids = Utils.readLines(input).stream().map(Cuboid::parse).toList();
	}
	
	@Override
	public Object part1() {
		Region3D initializationArea = Region3D.fromCenter(Point3D.ZERO, 50);
		List<Cuboid> initializationCuboids = cuboids.stream()
				.map(c -> new Cuboid(c.on, initializationArea.intersection(c.region)))
				.filter(c -> c.region != null)
				.toList();
		return solve(initializationCuboids);
	}
	
	@Override
	public Object part2() {
		return solve(cuboids);
	}
	
	private long solve(List<Cuboid> cuboids) {
		List<Cuboid> merged = new ArrayList<>();
		
		for (Cuboid current : cuboids) {
			for (Cuboid m : new ArrayList<>(merged)) {
				Region3D intersection = current.region.intersection(m.region);
				if (intersection != null)
					merged.add(new Cuboid(!m.on, intersection));
			}
			if (current.on)
				merged.add(current);
		}
		return merged.stream().mapToLong(c -> c.region.volume() * (c.on ? 1 : -1)).sum();
	}
	
	record Cuboid(boolean on, Region3D region) {
		
		static Cuboid parse(String line) {
			String[] split = line.split(" ");
			int[] c = Arrays.stream(split[1].split(","))
					.map(s -> s.split("=")[1])
					.flatMap(s -> Arrays.stream(StringUtils.split(s, "..")))
					.mapToInt(Integer::parseInt)
					.toArray();
			return new Cuboid("on".equals(split[0]), new Region3D(c[0], c[1], c[2], c[3], c[4], c[5]));
		}
	}
}