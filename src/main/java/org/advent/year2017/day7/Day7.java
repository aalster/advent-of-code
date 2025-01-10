package org.advent.year2017.day7;

import lombok.Data;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day7()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "tknk", 60),
				new ExpectedAnswers("input.txt", "airlri", 1206)
		);
	}
	
	Program root;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		root = Program.parseTree(Utils.readLines(input));
	}
	
	@Override
	public Object part1() {
		return root.name;
	}
	
	@Override
	public Object part2() {
		return root.fixedWeight(0);
	}
	
	@Data
	static class Program {
		String parent;
		final String name;
		final int weight;
		final List<Program> children = new ArrayList<>();
		int totalWeightCache = 0;
		
		void addChild(Program child) {
			children.add(child);
			child.parent = name;
		}
		
		int getTotalWeight() {
			if (totalWeightCache == 0)
				totalWeightCache = weight + children.stream().mapToInt(Program::getTotalWeight).sum();
			return totalWeightCache;
		}
		
		int fixedWeight(int expected) {
			Map<Integer, List<Program>> childrenByWeight = children.stream().collect(Collectors.groupingBy(Program::getTotalWeight));
			if (childrenByWeight.size() == 1)
				return expected - children.size() * childrenByWeight.keySet().iterator().next();
			Program badChild = childrenByWeight.values().stream().filter(c -> c.size() == 1).findAny().map(List::getFirst).orElseThrow();
			return badChild.fixedWeight(childrenByWeight.keySet().stream().filter(w -> w != badChild.getTotalWeight()).findAny().orElseThrow());
		}
		
		static Program parseTree(List<String> lines) {
			Map<String, Program> programs = lines.stream()
					.map(line -> line.split(" -> ")[0].split(" "))
					.map(split -> new Program(split[0], Integer.parseInt(Utils.removeEach(split[1], "(", ")"))))
					.collect(Collectors.toMap(Program::getName, p -> p));
			
			for (String line : lines) {
				String[] split = line.split(" -> ");
				if (split.length == 1)
					continue;
				Program program = programs.get(split[0].split(" ")[0]);
				Stream.of(split[1].split(", ")).map(programs::get).forEach(program::addChild);
			}
			
			Program root = programs.values().iterator().next();
			while (root.parent != null)
				root = programs.get(root.parent);
			return root;
		}
	}
}