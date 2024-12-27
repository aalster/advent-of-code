package org.advent.runner;

import org.reflections.Reflections;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class YearRunner {
	
	public static void main(String[] args) {
		YearRunner runner = new YearRunner();
		runner.runAll();
	}
	
	public void runAll() {
		run(dayClasses().toList());
	}
	
	public void runYear(int year) {
		run(dayClasses().filter(c -> AbstractDay.year(c) == year).toList());
	}
	
	private void run(List<Class<? extends AbstractDay>> dayClasses) {
		int prevYear = 0;
		for (Class<? extends AbstractDay> dayClass : dayClasses) {
			try {
				AbstractDay day = dayClass.getDeclaredConstructor().newInstance();
				if (prevYear == 0 || prevYear != day.getYear())
					System.out.println("\n" + OutputUtils.white("Year " + day.getYear()));
				System.out.println(OutputUtils.white("  Day " + day.getDay()));
				
				new DayRunner(day).runForYear("example.txt");
				
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
