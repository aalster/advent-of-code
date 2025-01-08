package org.advent.runner;

import java.util.function.Supplier;

public record PuzzleRunner(AdventDay day, Object expected, Supplier<Object> solution, int part) {
	
	public boolean notIgnored() {
		return expected != ExpectedAnswers.IGNORE;
	}
	
	public PuzzleResult run() {
		Timer timer = new Timer();
		try {
			Object answer = solution.get();
			return PuzzleResult.result(day, part, expected, answer, timer.time());
		} catch (Exception e) {
			return PuzzleResult.error(day, part, expected, e, timer.time());
		}
	}
}
