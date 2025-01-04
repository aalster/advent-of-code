package org.advent.year2021.day22;

import org.advent.common.Axis3D;
import org.advent.common.Pair;
import org.advent.common.Region3D;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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
		List<Cuboid> cuboidsCopy = cuboids.reversed();
		int radius = 50;
		long count = 0;
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					for (Cuboid cuboid : cuboidsCopy) {
						if (cuboid.region().contains(x, y, z)) {
							if (cuboid.on())
								count++;
							break;
						}
					}
				}
			}
		}
		return count;
	}
	
	@Override
	public Object part2() {
		List<Cuboid> cuboidsCopy = new ArrayList<>(cuboids);
		Set<Cuboid> mergedCuboids = new HashSet<>();
		
		while (!cuboidsCopy.isEmpty()) {
			Cuboid current = cuboidsCopy.removeFirst();
			
			Cuboid _current = current;
			Cuboid merged = mergedCuboids.stream().filter(c -> c.region.intersects(_current.region)).findAny().orElse(null);
			if (merged == null) {
				if (current.on)
					mergedCuboids.add(current);
				continue;
			}
			
			Region3D intersection = merged.region.intersection(current.region);
			
			if (!intersection.equals(current.region)) {
				for (Region3D remain : cutRemains(current.region, intersection))
					cuboidsCopy.addFirst(new Cuboid(current.on, remain));
				current = new Cuboid(current.on, intersection);
			}
			
			if (merged.on == current.on)
				continue;
			
			mergedCuboids.remove(merged);
			if (!intersection.equals(merged.region))
				for (Region3D remain : cutRemains(merged.region, intersection))
					mergedCuboids.add(new Cuboid(merged.on, remain));
			
			mergedCuboids.add(current);
		}
		return mergedCuboids.stream().filter(Cuboid::on).mapToLong(c -> c.region().volume()).sum();
	}
	
	static List<Region3D> cutRemains(Region3D target, Region3D cutoff) {
		List<Region3D> result = new ArrayList<>();
		for (Axis3D axis : Axis3D.values()) {
			if (axis.minOfRegion(target) < axis.minOfRegion(cutoff)) {
				Pair<Region3D, Region3D> cut = target.cut(axis, axis.minOfRegion(cutoff));
				result.add(cut.left());
				target = cut.right();
			}
			if (axis.maxOfRegion(cutoff) < axis.maxOfRegion(target)) {
				Pair<Region3D, Region3D> cut = target.cut(axis, axis.maxOfRegion(cutoff) + 1);
				result.add(cut.right());
				target = cut.left();
			}
		}
		return result;
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