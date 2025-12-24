package org.advent.year2020.day14;

import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day14 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day14()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 165, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 208),
				new ExpectedAnswers("input.txt", 3059488894985L, 2900994392308L)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
	}
	
	@Override
	public Object part1() {
		Map<Integer, Long> memory = new HashMap<>();
		Mask mask = Mask.parse("0");
		for (String line : lines) {
			String[] split = line.split(" = ");
			if (split[0].equals("mask")) {
				mask = Mask.parse(split[1]);
			} else {
				int address = Integer.parseInt(StringUtils.substringBetween(split[0], "[", "]"));
				long value = Long.parseLong(split[1]);
				memory.put(address, mask.apply(value));
			}
		}
		return memory.values().stream().mapToLong(Long::longValue).sum();
	}
	
	@Override
	public Object part2() {
		List<Pair<Mask2, Long>> operations = new ArrayList<>();
		Mask2 mask = Mask2.parse("0");
		for (String line : lines) {
			String[] split = line.split(" = ");
			if (split[0].equals("mask")) {
				mask = Mask2.parse(split[1]);
			} else {
				int address = Integer.parseInt(StringUtils.substringBetween(split[0], "[", "]"));
				long value = Long.parseLong(split[1]);
				operations.add(Pair.of(mask.apply(address), value));
			}
		}
		
		long result = 0;
		List<Mask2> countedMasks = new ArrayList<>();
		for (Pair<Mask2, Long> operation : operations.reversed()) {
			Mask2 current = operation.left();
			List<Mask2> currentMasks = current.removeAll(countedMasks);
			result += currentMasks.stream().mapToLong(m -> operation.right() * m.valuesCount()).sum();
			countedMasks.addAll(currentMasks);
		}
		
		return result;
	}
	
	record Mask(String value, long base, long mask) {
		
		long apply(long value) {
			return value & mask | base;
		}
		
		static Mask parse(String value) {
			long base = Long.parseLong(StringUtils.replaceChars(value, 'X', '0'), 2);
			long mask = Long.parseLong(StringUtils.replaceChars(value, "10X", "001"), 2);
			return new Mask(value, base, mask);
		}
	}
	
	record Mask2(char[] value) {
		
		Mask2 apply(long number) {
			char[] result = Arrays.copyOf(value, value.length);
			int bit = 1;
			for (int i = result.length - 1; i >= 0; i--) {
				if (result[i] == '0')
					result[i] = (number & bit) > 0 ? '1' : '0';
				bit <<= 1;
			}
			return new Mask2(result);
		}
		
		long valuesCount() {
			int result = 1;
			for (char c : value)
				if (c == 'X')
					result <<= 1;
			return result;
		}
		
		List<Mask2> removeAll(List<Mask2> masks) {
			List<Mask2> result = List.of(this);
			for (Mask2 mask : masks)
				result = result.stream().flatMap(m -> m.remove(mask).stream()).toList();
			return result;
		}
		
		// Получаем разницу масок (вторая маска перекрывает значения первой, эти адреса нужно удалить из текущей маски).
		// Если у масок один из битов отличается и при этом не X - маски не пересекаются.
		// Если пересекаются, вырезаем из X-битов первой маски значения битов второй маски. Для каждого такого бита
		// нужно создавать отдельную маску. При этом, если будет создано несколько масок, у них будут пересекаться адреса:
		// XXX - 11X = 0XX + X0X <- тут дублируется значение 00X, его нужно удалить рекурсивно
		List<Mask2> remove(Mask2 mask) {
			for (int i = 0; i < value.length; i++) {
				char left = value[i];
				char right = mask.value[i];
				if (left != 'X' && right != 'X' && left != right)
					return List.of(this);
			}
			
			List<Mask2> result = new ArrayList<>();
			for (int i = 0; i < value.length; i++) {
				char left = value[i];
				char right = mask.value[i];
				if (left == 'X' && right != 'X') {
					char[] res = Arrays.copyOf(value, value.length);
					res[i] = right == '0' ? '1' : '0';
					result.addAll(new Mask2(res).removeAll(result));
				}
			}
			return result;
		}
		
		static Mask2 parse(String value) {
			return new Mask2(value.toCharArray());
		}
	}
}