package org.advent.year2021.day22;

import org.advent.common.Axis3D;
import org.advent.common.Pair;
import org.advent.common.Region3D;
import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

public class Day22 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day22.class, "input.txt");
		List<Cuboid> cuboids = Utils.readLines(input).stream().map(Cuboid::parse).toList();
		
		System.out.println("Answer 1: " + part1(cuboids));
		System.out.println("Answer 2: " + part2(cuboids));
	}
	
	private static long part1(List<Cuboid> cuboids) {
		cuboids = cuboids.reversed();
		int radius = 50;
		long count = 0;
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					for (Cuboid cuboid : cuboids) {
						if (cuboid.region().containsInclusive(x, y, z)) {
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
	
	private static long part2(List<Cuboid> cuboids) {
		cuboids = new ArrayList<>(cuboids);
		Set<Cuboid> mergedCuboids = new HashSet<>();
		
		while (!cuboids.isEmpty()) {
			Cuboid current = cuboids.removeFirst();
			
			Cuboid _current = current;
			Cuboid merged = mergedCuboids.stream().filter(c -> c.region.intersects(_current.region)).findAny().orElse(null);
			if (merged == null) {
				if (current.on)
					mergedCuboids.add(current);
				continue;
			}
			
			Region3D intersection = Objects.requireNonNull(merged.region.intersection(current.region));
			
			if (!intersection.equals(current.region)) {
				for (Region3D remain : cutRemains(current.region, intersection))
					cuboids.addFirst(new Cuboid(current.on, remain));
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