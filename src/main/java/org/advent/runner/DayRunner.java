package org.advent.runner;

import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class DayRunner {
	private final AbstractDay day;
	
	public void runAll() {
		System.out.println(day);
		day.expected().forEach(file -> run(file, true, true));
	}
	
	public void run(String file) {
		run(expected(file), false, false);
	}
	
	void runForYear(String file) {
		run(expected(file), false, true);
	}
	
	public void run(String file, int part) {
		ExpectedAnswers expected = expected(file);
		expected = new ExpectedAnswers(expected.file(),
				part == 1 ? expected.answer1() : ExpectedAnswers.IGNORE,
				part == 2 ? expected.answer2() : ExpectedAnswers.IGNORE);
		run(expected, false, false);
	}
	
	public void run(ExpectedAnswers expected, boolean printInfo, boolean pad) {
		Timer timer = new Timer();
		day.prepare(expected.file());
		if (printInfo)
			System.out.println("  " + OutputUtils.white(expected.file()) + " (prepare " + timer.stepFormatted() + "):");
		
		runPart(expected.answer1(), day::part1, 1, timer, pad ? 4 : 0);
		runPart(expected.answer2(), day::part2, 2, timer, pad ? 4 : 0);
	}
	
	private void runPart(Object expected, Supplier<Object> part, int partNumber, Timer timer, int pad) {
		if (expected == ExpectedAnswers.IGNORE)
			return;
		Object answer = part.get();
		System.out.print(" ".repeat(pad) + "Answer " + partNumber + " " + timer.stepFormatted(7) + ": ");
		boolean passes = passes(expected, answer);
		System.out.println((passes ? "✅" : "❌") + OutputUtils.white(" " + answer)
				+ (expected != null && !passes ? " Expected: " + expected : ""));
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
