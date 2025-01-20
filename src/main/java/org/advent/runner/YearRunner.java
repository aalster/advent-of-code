package org.advent.runner;

import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class YearRunner {
	
	public static void main(String[] args) {
//		new YearRunner().runAll();
//		new YearRunner().runAllHorizontal();
		new YearRunner().runYear(2023);
//		new YearRunner().runAll("input.txt");
	}
	
	public void runAll() {
		run(dayClasses().toList(), null, false);
	}
	
	public void runAllHorizontal() {
		run(dayClasses().toList(), null, true);
	}
	
	public void runYear(int year) {
		runYear(year, null, false);
	}
	
	public void runYearHorizontal(int year) {
		runYear(year, null, true);
	}
	
	public void runYear(int year, String file, boolean horizontal) {
		run(dayClasses().filter(c -> AdventDay.year(c) == year).toList(), file, horizontal);
	}
	
	private void run(List<Class<? extends AdventDay>> dayClasses, String file, boolean horizontal) {
		if (horizontal) {
			System.out.print("      ");
			for (int i = 1; i <= 25; i++)
				System.out.print(OutputUtils.WIDE_SPACE + OutputUtils.leftPad(i, 2));
		}
		
		int prevYear = 0;
		List<PuzzleResultStats> stats = new ArrayList<>();
		for (Class<? extends AdventDay> dayClass : dayClasses) {
			try {
				AdventDay day = dayClass.getDeclaredConstructor().newInstance();
				if (horizontal) {
					if (prevYear != day.getYear())
						System.out.print("\n" + day.getYear() + "  ");
				} else {
					if (prevYear != 0 && prevYear != day.getYear())
						System.out.println();
				}
				
				PuzzleResultStats dayStats = new DayRunner(day).runForYear(file, horizontal);
				if (horizontal)
					System.out.print("  " + dayStats.dayResult());
				stats.add(dayStats);
				
				prevYear = day.getYear();
			} catch (Exception e) {
				System.out.println(OutputUtils.red(e.getMessage()));
			}
		}
		System.out.println();
		System.out.println(PuzzleResultStats.combineAll(stats.stream()).summary());
	}
	
	private Stream<Class<? extends AdventDay>> dayClasses() {
		System.setProperty("org.slf4j.simpleLogger.log.org.reflections.Reflections", "warn");
		return new Reflections("org.advent").getSubTypesOf(AdventDay.class).stream()
				.filter(type -> !type.getName().toLowerCase().contains("template"))
				.sorted(Comparator.comparing(AdventDay::year).thenComparing(AdventDay::day));
	}
}
