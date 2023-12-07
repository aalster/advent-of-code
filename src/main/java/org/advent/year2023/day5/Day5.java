package org.advent.year2023.day5;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

public class Day5 {
	
	public static final String STARTING_RESOURCE = "seed";
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day5.class, "input.txt");
		long[] seeds = Arrays.stream(input.nextLine().split(": ")[1].split(" ")).mapToLong(Long::parseLong).toArray();
		Map<String, Mapping> mappings = new HashMap<>();
		List<String> lines = new ArrayList<>();
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty()) {
				if (!lines.isEmpty()) {
					Mapping mapping = Mapping.parse(lines);
					mappings.put(mapping.sourceCategory(), mapping);
					lines = new ArrayList<>();
				}
				continue;
			}
			lines.add(line);
		}
		Mapping mapping = Mapping.parse(lines);
		mappings.put(mapping.sourceCategory(), mapping);
		
		System.out.println("Answer 1: " + part1(seeds, mappings));
		System.out.println("Answer 2: " + part2(seeds, mappings));
	}
	
	private static long part1(long[] seeds, Map<String, Mapping> mappings) {
		return solve(Resource.ofSinglesSeed(seeds), mappings);
	}
	
	private static long part2(long[] seeds, Map<String, Mapping> mappings) {
		List<Range> ranges = new ArrayList<>();
		for (int i = 0; i < seeds.length; i += 2)
			ranges.add(new Range(seeds[i], seeds[i + 1]));
		return solve(new Resource(STARTING_RESOURCE, ranges), mappings);
	}
	
	private static long solve(Resource resource, Map<String, Mapping> mappings) {
		while (true) {
			Mapping mapping = mappings.get(resource.name());
			if (mapping == null)
				break;
			resource = mapping.nextResource(resource);
		}
		return resource.ranges().stream().mapToLong(Range::sourceStart).min().orElse(0);
	}
	
	private record Resource(String name, List<Range> ranges) {
		static Resource ofSinglesSeed(long[] values) {
			return new Resource(STARTING_RESOURCE, Arrays.stream(values).mapToObj(v -> new Range(v, 1)).toList());
		}
	}
	
	private record Mapping(String sourceCategory, String destinationCategory, List<RangeMapping> rangesMapping) {
		
		private Stream<Range> nextRanges(Range resource) {
			List<RangeMapping> overlapped = rangesMapping.stream().filter(r -> r.overlaps(resource)).toList();
			List<Range> nextRanges = new ArrayList<>();
			Range shrinkedResource = resource;
			for (RangeMapping current : overlapped) {
				if (shrinkedResource.sourceStart() < current.range().sourceStart()) {
					nextRanges.add(Range.ofLimits(shrinkedResource.sourceStart(), current.range().sourceStart()));
					shrinkedResource = Range.ofLimits(current.range().sourceStart(), shrinkedResource.endExclusive());
				}
				Range overlapping = shrinkedResource.overlapping(current.range());
				nextRanges.add(overlapping.shift(current.delta()));
				shrinkedResource = Range.ofLimits(overlapping.endExclusive(), shrinkedResource.endExclusive());
			}
			if (shrinkedResource.length() > 0)
				nextRanges.add(shrinkedResource);
			return nextRanges.stream();
		}
		
		public Resource nextResource(Resource resource) {
			if (!resource.name().equals(sourceCategory))
				throw new IllegalArgumentException("Bad resource type");
			List<Range> values = resource.ranges().stream().flatMap(this::nextRanges).toList();
			return new Resource(destinationCategory, values);
		}
		
		static Mapping parse(List<String> lines) {
			String[] split = null;
			boolean first = true;
			List<RangeMapping> ranges = new ArrayList<>();
			for (String line : lines) {
				if (first) {
					split = lines.getFirst().split(" ")[0].split("-to-");
					first = false;
					continue;
				}
				long[] values = Arrays.stream(line.split(" ")).mapToLong(Long::parseLong).toArray();
				long destStart = values[0];
				long srcStart = values[1];
				long length = values[2];
				ranges.add(new RangeMapping(new Range(srcStart, length), destStart - srcStart));
			}
			if (split == null)
				throw new NullPointerException();
			return new Mapping(split[0], split[1], ranges.stream().sorted(Comparator.comparing(r -> r.range().sourceStart())).toList());
		}
	}
	
	private record Range(long sourceStart, long length) {
		public long endExclusive() {
			return sourceStart + length;
		}
		public boolean overlaps(Range other) {
			return sourceStart < other.endExclusive() && other.sourceStart < endExclusive();
		}
		public Range overlapping(Range other) {
			long overlappingStart = Math.max(sourceStart, other.sourceStart);
			long overlappingEnd = Math.min(endExclusive(), other.endExclusive());
			return new Range(overlappingStart, overlappingEnd - overlappingStart);
		}
		public Range shift(long delta) {
			return new Range(sourceStart + delta, length);
		}
		public static Range ofLimits(long start, long endExclusive) {
			return new Range(start, endExclusive - start);
		}
	}
	
	private record RangeMapping(Range range, long delta) {
		public boolean overlaps(Range source) {
			return source.overlaps(range);
		}
	}
}