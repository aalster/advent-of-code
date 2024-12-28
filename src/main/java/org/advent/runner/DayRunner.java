package org.advent.runner;

import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class DayRunner {
	private final AdventDay day;
	
	public boolean runAll() {
		System.out.println(day);
		return day.expected().stream()
				.map(f -> run(f, true, true))
				.reduce(true, (a, b) -> a && b);
	}
	
	public boolean run(String file) {
		return run(expected(file), false, false);
	}
	
	boolean runForYear(String file) {
		if (file != null)
			return run(expected(file), false, true);
		
		return day.expected().stream()
				.map(f -> run(f, true, true))
				.reduce(true, (a, b) -> a && b);
	}
	
	public boolean run(String file, int part) {
		ExpectedAnswers expected = expected(file);
		expected = new ExpectedAnswers(expected.file(),
				part == 1 ? expected.answer1() : ExpectedAnswers.IGNORE,
				part == 2 ? expected.answer2() : ExpectedAnswers.IGNORE);
		return run(expected, false, false);
	}
	
	public boolean run(ExpectedAnswers expected, boolean printInfo, boolean pad) {
		Timer timer = new Timer();
		day.prepare(expected.file());
		if (printInfo)
			System.out.println("    " + OutputUtils.white(expected.file()) + " (prepare " + timer.stepFormatted() + "):");
		
		boolean part1Passed = runPart(expected.answer1(), day::part1, 1, timer, pad ? 6 : 0);
		boolean part2Passed = runPart(expected.answer2(), day::part2, 2, timer, pad ? 6 : 0);
		return part1Passed && part2Passed;
	}
	
	private boolean runPart(Object expected, Supplier<Object> part, int partNumber, Timer timer, int pad) {
		if (expected == ExpectedAnswers.IGNORE)
			return true;
		try {
			Object answer = part.get();
			String time = timer.stepFormatted(7);
			boolean passes = passes(expected, answer);
			System.out.println(" ".repeat(pad) + "Answer " + partNumber + " " + time + ": "
					+ (passes ? "✅" : "❌") + " " + OutputUtils.white(answer)
					+ (expected != null && !passes ? " Expected: " + expected : ""));
			return passes;
		} catch (Exception e) {
			String time = timer.stepFormatted(7);
			System.out.println(" ".repeat(pad) + "Answer " + partNumber + " " + time + ": "
					+ "❌ " + OutputUtils.red("Error: " + e.getMessage()));
			return false;
		}
	}
	
	private boolean passes(Object expected, Object answer) {
		if (expected == null || answer == null)
			return false;
		if (expected instanceof Number en && answer instanceof Number an)
			return en.doubleValue() == an.doubleValue();
		return expected.equals(answer);
	}
	
	private ExpectedAnswers expected(String file) {
		for (ExpectedAnswers expectedAnswers : day.expected())
			if (file.equals(expectedAnswers.file()))
				return expectedAnswers;
		return new ExpectedAnswers(file, null, null);
	}
}
