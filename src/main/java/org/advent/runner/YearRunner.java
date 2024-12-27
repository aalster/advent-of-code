package org.advent.runner;

import org.reflections.Reflections;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class YearRunner {
	
	public static void main(String[] args) {
//		new YearRunner().runAll("input.txt");
		new YearRunner().runYear(2024);
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
		run(dayClasses().filter(c -> AbstractDay.year(c) == year).toList(), file);
	}
	
	private void run(List<Class<? extends AbstractDay>> dayClasses, String file) {
		int prevYear = 0;
		for (Class<? extends AbstractDay> dayClass : dayClasses) {
			try {
				AbstractDay day = dayClass.getDeclaredConstructor().newInstance();
				if (prevYear == 0 || prevYear != day.getYear())
					System.out.println("\n" + OutputUtils.white("Year " + day.getYear()));
				System.out.println(OutputUtils.white("  Day " + day.getDay()));
				
				new DayRunner(day).runForYear(file);
				
				prevYear = day.getYear();
			} catch (Exception e) {
				System.out.println(OutputUtils.red(e.getMessage()));
			}
		}
	}
	
	private Stream<Class<? extends AbstractDay>> dayClasses() {
		System.setProperty("org.slf4j.simpleLogger.log.org.reflections.Reflections", "warn");
		return new Reflections("org.advent").getSubTypesOf(AbstractDay.class).stream()
				.filter(type -> !type.getName().toLowerCase().contains("template"))
				.sorted(Comparator.comparing(AbstractDay::year).thenComparing(AbstractDay::day));
	}
}
