package org.advent.year2020.day4;

import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day4 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day4.class, "input.txt");
		List<Passport> passports = new ArrayList<>();
		Map<String, String> fields = new HashMap<>();
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty()) {
				passports.add(new Passport(fields));
				fields = new HashMap<>();
				continue;
			}
			fields.putAll(Arrays.stream(line.split(" "))
					.map(s -> s.split(":"))
					.collect(Collectors.toMap(pair -> pair[0], pair -> pair[1])));
		}
		if (!fields.isEmpty())
			passports.add(new Passport(fields));
		
		System.out.println("Answer 1: " + part1(passports));
		System.out.println("Answer 2: " + part2(passports));
	}
	
	private static long part1(List<Passport> passports) {
		Set<String> requiredFields = Set.of("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid");
		return passports.stream().filter(p -> p.isValid(requiredFields)).count();
	}
	
	private static long part2(List<Passport> passports) {
		Map<String, Validation<String>> requiredFields = Map.of(
				"byr", number(range(1920, 2002)),
				"iyr", number(range(2010, 2020)),
				"eyr", number(range(2020, 2030)),
				"hgt", any(
						suffix("cm", number(range(150, 193))),
						suffix("in", number(range(59, 76)))
				),
				"hcl", all(
						length(7),
						prefix("#", chars(any(range('0', '9'), range('a', 'f'))))
				),
				"ecl", values("amb", "blu", "brn", "gry", "grn", "hzl", "oth"),
				"pid", all(
						length(9),
						chars(range('0', '9'))
				));
		return passports.stream().filter(p -> p.isValid(requiredFields)).count();
	}
	
	record Passport(Map<String, String> fields) {
		
		boolean isValid(Set<String> requiredFields) {
			return fields.keySet().containsAll(requiredFields);
		}
		
		boolean isValid(Map<String, Validation<String>> requiredFields) {
			for (Map.Entry<String, Validation<String>> requiredField : requiredFields.entrySet()) {
				String value = fields.get(requiredField.getKey());
				if (StringUtils.isEmpty(value) || !requiredField.getValue().isValid(value))
					return false;
			}
			return true;
		}
	}
	
	interface Validation<T> {
		boolean isValid(T value);
	}
	
	@SafeVarargs
	static <K> Validation<K> all(Validation<K>... validators) {
		return value -> Arrays.stream(validators).allMatch(v -> v.isValid(value));
	}
	
	@SafeVarargs
	static <K> Validation<K> any(Validation<K>... validators) {
		return value -> Arrays.stream(validators).anyMatch(v -> v.isValid(value));
	}
	
	static Validation<String> number(Validation<Integer> validation) {
		return value -> {
			try {
				return validation.isValid(Integer.parseInt(value));
			} catch (NumberFormatException e) {
				return false;
			}
		};
	}
	
	static Validation<String> suffix(String suffix, Validation<String> validation) {
		return value -> value.endsWith(suffix) && validation.isValid(StringUtils.removeEnd(value, suffix));
	}
	
	static Validation<String> prefix(String prefix, Validation<String> validation) {
		return value -> value.startsWith(prefix) && validation.isValid(StringUtils.removeStart(value, prefix));
	}
	
	static Validation<Integer> range(int min, int max) {
		return value -> min <= value && value <= max;
	}
	
	static Validation<String> chars(Validation<Integer> validation) {
		return value -> value.chars().allMatch(validation::isValid);
	}
	
	static Validation<String> values(String... values) {
		return value -> Set.of(values).contains(value);
	}
	
	static Validation<String> length(int length) {
		return value -> value.length() == length;
	}
}