package org.advent.year2023.day15;

import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Day15 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day15.class, "input.txt");
		String line = input.nextLine();
		
		System.out.println("Answer 1: " + part1(line));
		System.out.println("Answer 2: " + part2(line));
	}
	
	private static long part1(String line) {
		return Arrays.stream(line.split(",")).mapToLong(Day15::hash).sum();
	}
	
	private static long part2(String line) {
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
	
	private static int hash(String value) {
		int hash = 0;
		for (int i = 0; i < value.length(); i++) {
			hash += value.charAt(i);
			hash = (hash * 17) % 256;
		}
		return hash;
	}
}