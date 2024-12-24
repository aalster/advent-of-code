package org.advent.year2024.day24;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day24.class, "input.txt");
		Map<String, Boolean> initialValues = new HashMap<>();
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty())
				break;
			String[] split = line.split(": ");
			initialValues.put(split[0], "1".equals(split[1]));
		}
		Algorithm algorithm = Algorithm.parse(Utils.readLines(input));
		
		System.out.println("Answer 1: " + part1(initialValues, algorithm));
		System.out.println("Answer 2: " + part2(algorithm));
	}
	
	private static long part1(Map<String, Boolean> initialValues, Algorithm algorithm) {
		return algorithm.computeAll(initialValues);
	}
	
	private static String part2(Algorithm algorithm) {
		int maxBit = algorithm.operations.keySet().stream()
				.filter(n -> n.startsWith("z"))
				.max(Comparator.naturalOrder())
				.map(n -> Integer.parseInt(n.substring(1)))
				.orElseThrow();
		
		List<Algorithm> algorithms = List.of(algorithm);
		Map<String, Boolean> zeroValues = new HashMap<>();
		for (int i = 0; i <= maxBit; i++) {
			int index = i;
			algorithms = algorithms.stream().flatMap(a -> a.fixForIndex(zeroValues, index)).toList();
			
			zeroValues.put(name("x", i), false);
			zeroValues.put(name("y", i), false);
		}
		
		if (algorithms.size() != 1)
			throw new RuntimeException("Not 1 solution");
		return algorithms.getFirst().swappedTargets.stream().sorted().collect(Collectors.joining(","));
	}
	
	record Operation(String name, SequencedSet<String> inputs) {
		
		String left() {
			return inputs.getFirst();
		}
		
		String right() {
			return inputs.getLast();
		}
		
		static Operation parse(String line) {
			String[] split = line.split(" ");
			return new Operation(split[1], new LinkedHashSet<>(Set.of(split[0], split[2])));
		}
	}
	
	record Algorithm(Map<String, Operation> operations, Set<String> fixedTargets, Set<String> swappedTargets, String overflowTarget) {
		
		Stream<Algorithm> fixForIndex(Map<String, Boolean> zeroValues, int index) {
			String xName = name("x", index);
			String yName = name("y", index);
			String zName = name("z", index);
			
			Map<String, Operation> currentOperations = operationsTree(zName);
			Set<String> nextFixedTargets = currentOperations.keySet();
			List<SequencedSet<String>> possibleSwaps = findFixingSwaps(currentOperations, zeroValues, xName, yName, zName);
			
			if (possibleSwaps.isEmpty()) {
				String nextOverflowTarget = findOverflowOperation(operations, zName);
				return Stream.of(new Algorithm(operations, nextFixedTargets, swappedTargets, nextOverflowTarget));
			}
			
			return possibleSwaps.stream().map(swap -> {
				Set<String> nextSwappedTargets = new HashSet<>(swappedTargets);
				nextSwappedTargets.addAll(swap);
				Map<String, Operation> nextOperations = swap(swap.getFirst(), swap.getLast());
				String nextOverflowTarget = findOverflowOperation(nextOperations, zName);
				return new Algorithm(nextOperations, nextFixedTargets, nextSwappedTargets, nextOverflowTarget);
			});
		}
		
		
		List<SequencedSet<String>> findFixingSwaps(Map<String, Operation> currentOperations, Map<String, Boolean> zeroValues,
		                                           String xName, String yName, String zName) {
			List<SequencedSet<String>> possibleSwaps = new ArrayList<>();
			if (testsPassed(zeroValues, currentOperations, xName, yName, zName))
				return List.of();
			
			for (String currentTarget : currentOperations.keySet()) {
				if (fixedTargets.contains(currentTarget))
					continue;
				
				for (String replacementTarget : operations.keySet()) {
					if (currentTarget.equals(replacementTarget) || fixedTargets.contains(replacementTarget))
						continue;
					
					Map<String, Operation> swapped = swap(currentTarget, replacementTarget);
					if (testsPassed(zeroValues, swapped, xName, yName, zName))
						possibleSwaps.add(new LinkedHashSet<>(Set.of(currentTarget, replacementTarget)));
				}
			}
			return possibleSwaps;
		}
		
		
		boolean testsPassed(Map<String, Boolean> zeroValues, Map<String, Operation> operations,
		                    String xName, String yName, String zName) {
			Map<String, Boolean> values = new HashMap<>(zeroValues);
			for (TestCase test : TestCase.xorTests) {
				values.putAll(Map.of(xName, test.x, yName, test.y));
				if (computeValue(values, operations, zName) != test.expected)
					return false;
			}
			
			if (overflowTarget != null) {
				operations.remove(overflowTarget);
				values.put(overflowTarget, true);
				
				for (TestCase test : TestCase.xorOverflowTests) {
					values.putAll(Map.of(xName, test.x, yName, test.y));
					if (computeValue(values, operations, zName) != test.expected)
						return false;
				}
			}
			return true;
		}
		
		Map<String, Operation> operationsTree(String target) {
			SequencedMap<String, Operation> result = new LinkedHashMap<>();
			List<String> currentTargets = new ArrayList<>(List.of(target));
			while (!currentTargets.isEmpty()) {
				String current = currentTargets.removeFirst();
				Operation operation = operations.get(current);
				if (operation == null)
					continue;
				result.putFirst(current, operation);
				currentTargets.addAll(operation.inputs);
			}
			return result;
		}
		
		String findOverflowOperation(Map<String, Operation> operations, String zName) {
			operations = new HashMap<>(operations);
			Set<String> used = new HashSet<>();
			List<String> checkArgs = new ArrayList<>(List.of(zName));
			while (!checkArgs.isEmpty()) {
				String arg = checkArgs.removeFirst();
				if (operations.values().stream().anyMatch(op -> op.inputs.contains(arg))) {
					used.add(arg);
					continue;
				}
				Operation operation = operations.remove(zName);
				if (operation != null)
					checkArgs.addAll(operation.inputs);
			}
			if (used.isEmpty())
				return null;
			return operations.entrySet().stream()
					.filter(e -> e.getValue().inputs.equals(used))
					.map(Map.Entry::getKey)
					.findAny()
					.orElseThrow();
		}
		
		Map<String, Operation> swap(String left, String right) {
			Map<String, Operation> swapped = new HashMap<>(operations);
			swapped.put(left, operations.get(right));
			swapped.put(right, operations.get(left));
			return swapped;
		}
		
		
		long computeAll(Map<String, Boolean> initialValues) {
			Map<String, Boolean> values = compute(initialValues, operations, null);
			List<Boolean> zValues = values.entrySet().stream()
					.filter(e -> e.getKey().startsWith("z"))
					.sorted(Map.Entry.<String, Boolean>comparingByKey().reversed())
					.map(Map.Entry::getValue)
					.toList();
			long result = 0;
			for (Boolean value : zValues)
				result = (result << 1) + (value ? 1 : 0);
			return result;
		}
		
		Boolean computeValue(Map<String, Boolean> initialValues, Map<String, Operation> operations, String target) {
			return compute(initialValues, operations, target).get(target);
		}
		
		Map<String, Boolean> compute(Map<String, Boolean> initialValues, Map<String, Operation> operations, String target) {
			Map<String, Boolean> values = new HashMap<>(initialValues);
			SequencedMap<String, Operation> operationsQueue = new LinkedHashMap<>(operations);
			
			int skippedSteps = 0;
			while (!operationsQueue.isEmpty()) {
				if (skippedSteps > operationsQueue.size())
					return Map.of();
				
				Map.Entry<String, Operation> entry = operationsQueue.pollFirstEntry();
				Operation operation = entry.getValue();
				Boolean left = values.get(operation.left());
				Boolean right = values.get(operation.right());
				if (left == null || right == null) {
					operationsQueue.putLast(entry.getKey(), operation);
					skippedSteps++;
					continue;
				}
				skippedSteps = 0;
				
				values.put(entry.getKey(), switch (operation.name) {
					case "AND" -> left && right;
					case "OR" -> left || right;
					case "XOR" -> left != right;
					default -> throw new IllegalStateException("Unexpected operation: " + operation);
				});
				if (entry.getKey().equals(target))
					break;
			}
			return values;
		}
		
		static Algorithm parse(List<String> lines) {
			Map<String, Operation> operations = lines.stream()
					.map(line -> line.split(" -> "))
					.collect(Collectors.toMap(split -> split[1], split -> Operation.parse(split[0])));
			return new Algorithm(operations, Set.of(), Set.of(), null);
		}
	}
	
	record TestCase(boolean x, boolean y, Boolean expected) {
		static final List<TestCase> xorTests = List.of(
				new TestCase(false, false, false),
				new TestCase(false, true, true),
				new TestCase(true, false, true),
				new TestCase(true, true, false)
		);
		static final List<TestCase> xorOverflowTests = xorTests.stream()
				.map(t -> new TestCase(t.x, t.y, !t.expected))
				.toList();
	}
	
	static String name(String prefix, int bit) {
		return prefix + (bit < 10 ? "0" : "") + bit;
	}
}