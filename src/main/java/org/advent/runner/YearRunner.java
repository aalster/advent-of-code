package org.advent.runner;

import org.reflections.Reflections;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class YearRunner {
	
	public static void main(String[] args) {
//		new YearRunner().runAll();
//		new YearRunner().runAll("input.txt");
		new YearRunner().runYear(2022);
	}
	
	public void runAll() {
		runAll(null);
	}
	
	public void runAll(String file) {
		run(dayClasses().toList(), file);
	}
	
	public void runYear(int year) {
		runYear(year, null);
	}
	
	public void runYear(int year, String file) {
		run(dayClasses().filter(c -> AdventDay.year(c) == year).toList(), file);
	}
	
	private void run(List<Class<? extends AdventDay>> dayClasses, String file) {
		int passedCount = 0;
		int prevYear = 0;
		Timer timer = new Timer();
		for (Class<? extends AdventDay> dayClass : dayClasses) {
			try {
				AdventDay day = dayClass.getDeclaredConstructor().newInstance();
				if (prevYear == 0 || prevYear != day.getYear())
					System.out.println("\n" + OutputUtils.white("Year " + day.getYear()));
				System.out.println(OutputUtils.white("  Day " + day.getDay()));
				
				boolean passed = new DayRunner(day).runForYear(file);
				if (passed)
					passedCount++;
				
				prevYear = day.getYear();
			} catch (Exception e) {
				System.out.println(OutputUtils.red(e.getMessage()));
			}
		}
		int failedCount = dayClasses.size() - passedCount;
		System.out.println("\n" + OutputUtils.white("Finished after " + timer.time() / 1000 + "s.") +
				" Days passed: " + OutputUtils.green(passedCount) + "." +
				" Failed: " + (failedCount > 0 ? OutputUtils.red(failedCount) : failedCount) + ".");
	}
	
	private Stream<Class<? extends AdventDay>> dayClasses() {
		System.setProperty("org.slf4j.simpleLogger.log.org.reflections.Reflections", "warn");
		return new Reflections("org.advent").getSubTypesOf(AdventDay.class).stream()
				.filter(type -> !type.getName().toLowerCase().contains("template"))
				.sorted(Comparator.comparing(AdventDay::year).thenComparing(AdventDay::day));
	}
}
