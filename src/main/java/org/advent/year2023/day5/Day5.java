package org.advent.year2023.day5;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

public class Day5 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day5()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 35, 46),
				new ExpectedAnswers("input.txt", 165788812, 1928058)
		);
	}
	
	
	static final String STARTING_RESOURCE = "seed";
	long[] seeds;
	Map<String, Mapping> mappings;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		seeds = Arrays.stream(input.nextLine().split(": ")[1].split(" ")).mapToLong(Long::parseLong).toArray();
		mappings = new HashMap<>();
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
	}
	
	@Override
	public Object part1() {
		return solve(Resource.ofSinglesSeed(seeds), mappings);
	}
	
	@Override
	public Object part2() {
		List<Range> ranges = new ArrayList<>();
		for (int i = 0; i < seeds.length; i += 2)
			ranges.add(new Range(seeds[i], seeds[i + 1]));
		return solve(new Resource(STARTING_RESOURCE, ranges), mappings);
	}
	
	static long solve(Resource resource, Map<String, Mapping> mappings) {
		while (true) {
			Mapping mapping = mappings.get(resource.name());
			if (mapping == null)
				break;
			resource = mapping.nextResource(resource);
		}
		return resource.ranges().stream().mapToLong(Range::sourceStart).min().orElse(0);
	}
	
	record Resource(String name, List<Range> ranges) {
		static Resource ofSinglesSeed(long[] values) {
			return new Resource(STARTING_RESOURCE, Arrays.stream(values).mapToObj(v -> new Range(v, 1)).toList());
		}
	}
	
	record Mapping(String sourceCategory, String destinationCategory, List<RangeMapping> rangesMapping) {
		
		Stream<Range> nextRanges(Range resource) {
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
		
		Resource nextResource(Resource resource) {
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
	
	record Range(long sourceStart, long length) {
		long endExclusive() {
			return sourceStart + length;
		}
		boolean overlaps(Range other) {
			return sourceStart < other.endExclusive() && other.sourceStart < endExclusive();
		}
		Range overlapping(Range other) {
			long overlappingStart = Math.max(sourceStart, other.sourceStart);
			long overlappingEnd = Math.min(endExclusive(), other.endExclusive());
			return new Range(overlappingStart, overlappingEnd - overlappingStart);
		}
		Range shift(long delta) {
			return new Range(sourceStart + delta, length);
		}
		static Range ofLimits(long start, long endExclusive) {
			return new Range(start, endExclusive - start);
		}
	}
	
	record RangeMapping(Range range, long delta) {
		boolean overlaps(Range source) {
			return source.overlaps(range);
		}
	}
}