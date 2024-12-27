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
	
	@Override
	public String toString() {
		return "Year " + year + "\n  Day " + day;
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
