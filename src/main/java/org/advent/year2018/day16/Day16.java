package org.advent.year2018.day16;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day16 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day16()).runAll();
//		new DayRunner(new Day16()).run("example.txt", 1);
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 1, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 663, 525)
		);
	}
	
	List<Test> tests;
	List<int[]> operations;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<List<String>> linesSplit = Utils.splitByEmptyLine(Utils.readLines(input));
		tests = new ArrayList<>();
		operations = new ArrayList<>();
		
		for (List<String> lines : linesSplit) {
			if (lines.getFirst().contains(":"))
				tests.add(Test.parse(lines));
			else
				operations.addAll(lines.stream()
						.map(line -> Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray())
						.toList());
		}
	}
	
	@Override
	public Object part1() {
		return tests.stream()
				.map(Test::matchingOpcodeNames)
				.map(Set::size)
				.filter(count -> count >= 3)
				.count();
	}
	
	@Override
	public Object part2() {
		if (operations.isEmpty())
			return null;
		
		Map<Integer, Set<String>> possibleOpcodes = tests.stream().collect(Collectors.toMap(
				t -> t.operation[0], Test::matchingOpcodeNames,
				(left, right) -> left.stream().filter(right::contains).collect(Collectors.toSet())));
		
		Map<Integer, String> opcodes = new HashMap<>();
		while (opcodes.size() < allOpcodeNames.size()) {
			for (Map.Entry<Integer, Set<String>> entry : new ArrayList<>(possibleOpcodes.entrySet())) {
				if (entry.getValue().isEmpty()) {
					possibleOpcodes.remove(entry.getKey());
				} else if (entry.getValue().size() == 1) {
					opcodes.put(entry.getKey(), entry.getValue().iterator().next());
					possibleOpcodes.remove(entry.getKey());
				} else {
					entry.getValue().removeAll(opcodes.values());
				}
			}
		}
		
		int[] registers = new int[4];
		for (int[] operation : operations)
			apply(registers, operation, opcodes.get(operation[0]));
		return registers[0];
	}
	
	static void apply(int[] registers, int[] operation, String opcodeName) {
		registers[operation[3]] = switch (opcodeName) {
			case "addr" -> registers[operation[1]] + registers[operation[2]];
			case "addi" -> registers[operation[1]] + operation[2];
			case "mulr" -> registers[operation[1]] * registers[operation[2]];
			case "muli" -> registers[operation[1]] * operation[2];
			case "banr" -> registers[operation[1]] & registers[operation[2]];
			case "bani" -> registers[operation[1]] & operation[2];
			case "borr" -> registers[operation[1]] | registers[operation[2]];
			case "bori" -> registers[operation[1]] | operation[2];
			case "setr" -> registers[operation[1]];
			case "seti" -> operation[1];
			case "gtir" -> operation[1] > registers[operation[2]] ? 1 : 0;
			case "gtri" -> registers[operation[1]] > operation[2] ? 1 : 0;
			case "gtrr" -> registers[operation[1]] > registers[operation[2]] ? 1 : 0;
			case "eqir" -> operation[1] == registers[operation[2]] ? 1 : 0;
			case "eqri" -> registers[operation[1]] == operation[2] ? 1 : 0;
			case "eqrr" -> registers[operation[1]] == registers[operation[2]] ? 1 : 0;
			default -> throw new IllegalArgumentException("Unknown opcode " + opcodeName);
		};
	}
	
	static final List<String> allOpcodeNames = List.of("addr", "addi", "mulr", "muli", "banr", "bani", "borr", "bori",
			"setr", "seti", "gtir", "gtri", "gtrr", "eqir", "eqri", "eqrr");
	
	record Test(int[] before, int[] operation, int[] after) {
		
		Set<String> matchingOpcodeNames() {
			return allOpcodeNames.stream()
					.filter(opCodeName -> {
						int[] result = Arrays.copyOf(before, before.length);
						apply(result, operation, opCodeName);
						return Arrays.equals(result, after);
					})
					.collect(Collectors.toSet());
		}
		
		static Test parse(List<String> lines) {
			int[][] arrays = lines.stream()
					.map(s -> s.contains("[") ? Utils.removeEach(s.split("\\[")[1], "]", ",") : s)
					.map(s -> Arrays.stream(s.split(" ")).mapToInt(Integer::parseInt).toArray())
					.toArray(int[][]::new);
			if (arrays[0].length == 0)
				return null;
			return new Test(arrays[0], arrays[1], arrays[2]);
		}
	}
}