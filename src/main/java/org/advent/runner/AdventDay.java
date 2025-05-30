package org.advent.runner;

import lombok.Data;

import java.util.List;

@Data
public abstract class AdventDay {
	private final int year;
	private final int day;
	
	public AdventDay() {
		this.year = year(getClass());
		this.day = day(getClass());
	}
	
	public abstract List<ExpectedAnswers> expected();
	public abstract void prepare(String file);
	public abstract Object part1();
	public abstract Object part2();
	
	public Object part(int part) {
		return switch (part) {
			case 1 -> part1();
			case 2 -> part2();
			default -> throw new IllegalStateException("Unexpected value: " + part);
		};
	}
	
	@Override
	public String toString() {
		return year + "-" + (day < 10 ? "0" : "") + day;
	}
	
	private static int findByPrefix(String[] packages, String prefix) {
		for (String p : packages)
			if (p.startsWith(prefix))
				return Integer.parseInt(p.substring(prefix.length()));
		return 0;
	}
	
	static int year(Class<?> dayClass) {
		String[] packages = dayClass.getPackageName().split("\\.");
		return findByPrefix(packages, "year");
	}
	
	static int day(Class<?> dayClass) {
		String[] packages = dayClass.getPackageName().split("\\.");
		return findByPrefix(packages, "day");
	}
}
