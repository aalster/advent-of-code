package org.advent.runner;

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DayRunner {
	private final AbstractDay day;
	
	public void runAll() {
		System.out.println(day);
		day.expected().forEach(file -> run(file, true, true));
	}
	
	public void run(String file) {
		ExpectedAnswers expected = Objects.requireNonNull(expectedMap().get(file), "Expected answers does not contain " + file);
		run(expected, false, false);
	}
	
	void run(String file, boolean pad) {
		ExpectedAnswers expected = Objects.requireNonNull(expectedMap().get(file), "Expected answers does not contain " + file);
		run(expected, false, pad);
	}
	
	public void run(String file, int part) {
		ExpectedAnswers expected = Objects.requireNonNull(expectedMap().get(file), "Expected answers does not contain " + file);
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
		String prefix = expected != null && expected.equals(answer) ? "✅" : "❌";
		System.out.println(prefix + OutputUtils.white(" " + answer) + (expected != null && prefix.equals("❌") ? " Expected: " + expected : ""));
	}
	
	private Map<String, ExpectedAnswers> expectedMap() {
		return day.expected().stream().collect(Collectors.toMap(ExpectedAnswers::file, e -> e));
	}
}
