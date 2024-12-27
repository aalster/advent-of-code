package org.advent.runner;

import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class DayRunner {
	private final AbstractDay day;
	
	public void runAll() {
		System.out.println(day);
		day.expected().forEach(f -> run(f, true, true));
	}
	
	public void run(String file) {
		run(expected(file), false, false);
	}
	
	void runForYear(String file) {
		if (file == null)
			day.expected().forEach(f -> run(f, true, true));
		else
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
			System.out.println("    " + OutputUtils.white(expected.file()) + " (prepare " + timer.stepFormatted() + "):");
		
		runPart(expected.answer1(), day::part1, 1, timer, pad ? 6 : 0);
		runPart(expected.answer2(), day::part2, 2, timer, pad ? 6 : 0);
	}
	
	private void runPart(Object expected, Supplier<Object> part, int partNumber, Timer timer, int pad) {
		if (expected == ExpectedAnswers.IGNORE)
			return;
		try {
			Object answer = part.get();
			String time = timer.stepFormatted(7);
			boolean passes = passes(expected, answer);
			System.out.println(" ".repeat(pad) + "Answer " + partNumber + " " + time + ": "
					+ (passes ? "✅" : "❌") + OutputUtils.white(" " + answer)
					+ (expected != null && !passes ? " Expected: " + expected : ""));
		} catch (Exception e) {
			String time = timer.stepFormatted(7);
			System.out.println(" ".repeat(pad) + "Answer " + partNumber + " " + time + ": "
					+ "❌" + OutputUtils.red("Error: " + e.getMessage()));
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
