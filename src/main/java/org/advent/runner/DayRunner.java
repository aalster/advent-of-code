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
		
		day.prepare(expected.file());
		
		Object expectedAnswer = switch (part) {
			case 1 -> expected.answer1();
			case 2 -> expected.answer2();
			default -> ExpectedAnswers.IGNORE;
		};
		Supplier<Object> solution = switch (part) {
			case 1 -> day::part1;
			case 2 -> day::part2;
			default -> throw new IllegalStateException("Unexpected value: " + part);
		};
		
		return runPart(expectedAnswer, solution, part, 0, true);
	}
	
	public boolean run(ExpectedAnswers expected, boolean printInfo, boolean pad) {
		Timer timer = new Timer();
		day.prepare(expected.file());
		if (printInfo)
			System.out.println("    " + OutputUtils.white(expected.file()) + " (prepare " + timer.stepFormatted() + "):");
		
		boolean part1Passed = runPart(expected.answer1(), day::part1, 1, pad ? 6 : 0, false);
		boolean part2Passed = runPart(expected.answer2(), day::part2, 2, pad ? 6 : 0, false);
		return part1Passed && part2Passed;
	}
	
	private boolean runPart(Object expected, Supplier<Object> solution, int partNumber, int pad, boolean propagateError) {
		if (expected == ExpectedAnswers.IGNORE)
			return true;
		Timer timer = new Timer();
		try {
			Object answer = solution.get();
			String time = timer.stepFormatted(7);
			boolean passes = passes(expected, answer);
			System.out.println(" ".repeat(pad) + "Answer " + partNumber + " " + time + ": "
					+ (passes ? "✅" : "❌") + " " + OutputUtils.white(answer)
					+ (expected != null && !passes ? " Expected: " + expected : ""));
			return passes;
		} catch (Exception e) {
			if (propagateError)
				throw e;
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
