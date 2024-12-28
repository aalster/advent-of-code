package org.advent.year2023.day15;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day15 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day15()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 1320, 145),
				new ExpectedAnswers("input.txt", 506869, 271384)
		);
	}
	
	String line;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		line = input.nextLine();
	}
	
	@Override
	public Object part1() {
		return Arrays.stream(line.split(",")).mapToLong(this::hash).sum();
	}
	
	@Override
	public Object part2() {
		Map<Integer, Map<String, Integer>> boxes = new HashMap<>();
		Arrays.stream(line.split(",")).forEach(value -> {
			String label = StringUtils.substringBefore(StringUtils.substringBefore(value, "="), "-");
			int focalLength = value.contains("=") ? Integer.parseInt(StringUtils.substringAfter(value, "=")) : 0;
			Map<String, Integer> box = boxes.computeIfAbsent(hash(label), LinkedHashMap::new);
			if (focalLength == 0) {
				box.remove(label);
			} else {
				if (box.containsKey(label))
					box.replace(label, focalLength);
				else
					box.put(label, focalLength);
			}
		});
		long result = 0;
		for (Map.Entry<Integer, Map<String, Integer>> box : boxes.entrySet()) {
			long slotNumber = 1;
			for (Map.Entry<String, Integer> lens : box.getValue().entrySet()) {
				result += (1 + box.getKey()) * slotNumber * lens.getValue();
				slotNumber++;
			}
		}
		return result;
	}
	
	int hash(String value) {
		int hash = 0;
		for (int i = 0; i < value.length(); i++) {
			hash += value.charAt(i);
			hash = (hash * 17) % 256;
		}
		return hash;
	}
}