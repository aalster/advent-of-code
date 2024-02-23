package org.advent.year2023.day19;

import org.advent.common.Pair;
import org.advent.common.Utils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day19.class, "input.txt");
		Map<String, Workflow> workflows = new HashMap<>();
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty())
				break;
			Workflow workflow = Workflow.parse(line);
			workflows.put(workflow.name(), workflow);
		}
		List<Part> parts = new ArrayList<>();
		while (input.hasNext()) {
			parts.add(Part.parse(input.nextLine()));
		}
		
		System.out.println("Answer 1: " + part1(workflows, parts));
		System.out.println("Answer 2: " + part2(workflows));
	}
	
	private static long part1(Map<String, Workflow> workflows, List<Part> parts) {
		long result = 0;
		for (Part part : parts) {
			if (isAccepted(part, workflows))
				result += Arrays.stream(part.values).sum();
		}
		return result;
	}
	
	private static BigInteger part2(Map<String, Workflow> workflows) {
		PartRange initial = new PartRange(new Range[]{new Range(1, 4000), new Range(1, 4000), new Range(1, 4000), new Range(1, 4000)});
		List<PartRange> accepted = new ArrayList<>();
		List<Pair<PartRange, String>> parts = List.of(Pair.of(initial, "in"));
		while (!parts.isEmpty()) {
			Map<Boolean, List<Pair<PartRange, String>>> nextPartsRanges = parts.stream()
					.flatMap(pair -> workflows.get(pair.right()).split(pair.left()))
					.filter(pair -> !pair.right().equals("R"))
					.collect(Collectors.groupingBy(pair -> pair.right().equals("A")));
			accepted.addAll(nextPartsRanges.getOrDefault(true, List.of()).stream().map(Pair::left).toList());
			parts = nextPartsRanges.getOrDefault(false, List.of());
		}
		return accepted.stream().map(PartRange::combinations).reduce(BigInteger.ZERO, BigInteger::add);
	}
	
	static boolean isAccepted(Part part, Map<String, Workflow> workflows) {
		String name = "in";
		while (true) {
			name = workflows.get(name).run(part);
			if (name.equals("R"))
				return false;
			if (name.equals("A"))
				return true;
		}
	}
	
	record Part(int[] values) {
		static Part parse(String line) {
			Map<String, Integer> values = Arrays.stream(StringUtils.removeEnd(StringUtils.removeStart(line, "{"), "}").split(","))
					.map(s -> s.split("="))
					.collect(Collectors.toMap(split -> split[0], split -> Integer.parseInt(split[1])));
			return new Part(new int[] {values.get("x"), values.get("m"), values.get("a"), values.get("s")});
		}
	}
	
	record PartRange(Range[] ranges) {
		PartRange replace(int fieldIndex, Range range) {
			Range[] rangesCopy = new Range[ranges.length];
			for (int i = 0; i < rangesCopy.length; i++)
				rangesCopy[i] = i == fieldIndex ? range : ranges[i];
			return new PartRange(rangesCopy);
		}
		
		BigInteger combinations() {
			return Arrays.stream(ranges)
					.map(range -> BigInteger.valueOf(range.max() - range.min() + 1))
					.reduce(BigInteger.ONE, BigInteger::multiply);
		}
		
		@Override
		public String toString() {
			return Arrays.toString(ranges);
		}
	}
	
	record Range(int min, int max) {
		Range {
			if (min > max)
				throw new IllegalArgumentException("Bad range: " + min + ", " + max);
		}
		
		Map<Boolean, Range> split(Operation operation, int number) {
			return switch (operation) {
				case LOWER -> max < number ? Map.of(true, this) : number < min ? Map.of(false, this)
						: Map.of(true, new Range(min, number - 1), false, new Range(number, max));
				case HIGHER -> number < min ? Map.of(true, this) : max < number ? Map.of(false, this)
						: Map.of(false, new Range(min, number), true, new Range(number + 1, max));
			};
		}
		
		@Override
		public String toString() {
			return "[" + min + ", " + max + "]";
		}
	}
	
	record Condition(Operation operation, int fieldIndex, int number, String destination) {
		boolean applies(Part part) {
			if (operation == null)
				return true;
			int value = part.values()[fieldIndex];
			return operation == Operation.LOWER ? value < number : value > number;
		}
		
		Map<String, PartRange> split(PartRange partRange) {
			if (operation == null)
				return Map.of(destination, partRange);
			
			HashMap<String, PartRange> result = new HashMap<>();
			Map<Boolean, Range> split = partRange.ranges()[fieldIndex].split(operation, number);
			Range appliedRange = split.get(true);
			if (appliedRange != null)
				result.put(destination, partRange.replace(fieldIndex, appliedRange));
			Range notAppliedRange = split.get(false);
			if (notAppliedRange != null)
				result.put("", partRange.replace(fieldIndex, notAppliedRange));
			return result;
		}
		
		static Condition parse(String value) {
			String[] split = value.split(":");
			if (split.length == 1)
				return new Condition(null, 0, 0, value);
			
			Operation operation = value.charAt(1) == '<' ? Operation.LOWER : Operation.HIGHER;
			int number = Integer.parseInt(split[0].substring(2));
			int fieldIndex = ArrayUtils.indexOf(new String[]{"x", "m", "a", "s"}, value.substring(0, 1));
			return new Condition(operation, fieldIndex, number, split[1]);
		}
	}
	
	record Workflow(String name, List<Condition> conditions) {
		String run(Part part) {
			for (Condition condition : conditions)
				if (condition.applies(part))
					return condition.destination();
			throw new RuntimeException("No conditions applied");
		}
		
		Stream<Pair<PartRange, String>> split(PartRange partRange) {
			List<Pair<PartRange, String>> result = new ArrayList<>();
			for (Condition condition : conditions) {
				Map<String, PartRange> split = condition.split(partRange);
				for (Map.Entry<String, PartRange> entry : split.entrySet()) {
					if (!entry.getKey().isEmpty())
						result.add(Pair.of(entry.getValue(), entry.getKey()));
				}
				PartRange nextPartRange = split.get("");
				if (nextPartRange == null)
					break;
				partRange = nextPartRange;
			}
			return result.stream();
		}
		
		static Workflow parse(String line) {
			String[] split = StringUtils.removeEnd(line, "}").split("\\{");
			List<Condition> conditions = Arrays.stream(split[1].split(",")).map(Condition::parse).toList();
			return new Workflow(split[0], conditions);
		}
	}
	
	enum Operation {
		LOWER, HIGHER
	}
}