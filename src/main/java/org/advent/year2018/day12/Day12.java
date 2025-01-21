package org.advent.year2018.day12;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day12 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day12()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 325, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 3890, 4800000001087L)
		);
	}
	
	String initialPlants;
	Set<String> rules;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		initialPlants = input.nextLine().split(": ")[1];
		input.nextLine();
		rules = Utils.readLines(input).stream()
				.map(line -> line.split(" => "))
				.filter(split -> "#".equals(split[1]))
				.map(split -> split[0])
				.collect(Collectors.toSet());
	}
	
	@Override
	public Object part1() {
		return new State(initialPlants).nextGenerations(rules, 20);
	}
	
	@Override
	public Object part2() {
		long generations = 50000000000L;
		int batch = 100;
		int checkSteps = 10;
		int repetitionDelta;
		
		State state = new State(initialPlants);
		nextBatch: while (true) {
			int batchSum = state.nextGenerations(rules, batch);
			int currentSum = state.nextGenerations(rules, 1);
			int delta = currentSum - batchSum;
			generations -= batch + 1;
			
			for (int i = 0; i < checkSteps; i++) {
				int nextSum = state.nextGenerations(rules, 1);
				generations--;
				if (nextSum - currentSum != delta)
					continue nextBatch;
				currentSum = nextSum;
			}
			repetitionDelta = delta;
			break;
		}
		
		return state.indexesSum() + generations * repetitionDelta;
	}
	
	static class State {
		String plants;
		int indexShift = 0;
		
		State(String plants) {
			this.plants = plants;
		}
		
		int nextGenerations(Set<String> rules, int generations) {
			while (generations-- > 0)
				nextGeneration(rules);
			return indexesSum();
		}
		
		void nextGeneration(Set<String> rules) {
			shift();
			
			StringBuilder nextPlants = new StringBuilder();
			for (int index = 2; index < plants.length() - 2; index++)
				nextPlants.append(rules.contains(plants.substring(index - 2, index + 3)) ? '#' : '.');
			
			plants = nextPlants.toString();
		}
		
		void shift() {
			// С каждой стороны должно быть минимум 4 пустых клетки
			int prefixLength = Math.max(4 - plants.indexOf('#'), 0);
			int suffixLength = Math.max(4 - plants.length() + plants.lastIndexOf('#'), 0);
			plants = ".".repeat(prefixLength) + plants + ".".repeat(suffixLength);
			indexShift += 2 - prefixLength;
		}
		
		int indexesSum() {
			int sum = 0;
			char[] chars = plants.toCharArray();
			for (int i = 0; i < chars.length; i++)
				if (chars[i] == '#')
					sum += i + indexShift;
			return sum;
		}
	}
}