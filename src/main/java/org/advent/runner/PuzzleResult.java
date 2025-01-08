package org.advent.runner;

import lombok.SneakyThrows;

public record PuzzleResult(String day, int part, Object expected, Object answer, Exception exception, long time, boolean passed) {
	
	PuzzleResult print(int pad) {
		if (expected != ExpectedAnswers.IGNORE)
			System.out.println(" ".repeat(pad) + presentation());
		return this;
	}
	
	public String presentation() {
		String result = exception == null
				? (passed ? "✅" : "❌") + " " + OutputUtils.white(answer) + (expected != null && !passed ? " Expected: " + expected : "")
				: "❌ " + OutputUtils.red("Error: " + exception.getMessage());
		return "Answer " + part + " " + Timer.formatTime(time, 7) + ": " + result;
	}
	
	@SneakyThrows
	PuzzleResult propagateError() {
		if (exception != null)
			throw exception;
		return this;
	}
	
	PuzzleResultStats stats() {
		return expected == ExpectedAnswers.IGNORE ? PuzzleResultStats.EMPTY : new PuzzleResultStats(day, passed, exception != null, time);
	}
	
	public static PuzzleResult result(AdventDay day, int part, Object expected, Object answer, long time) {
		return new PuzzleResult(day.toString(), part, expected, answer, null, time, passed(expected, answer));
	}
	
	public static PuzzleResult error(AdventDay day, int part, Object expected, Exception exception, long time) {
		return new PuzzleResult(day.toString(), part, expected, null, exception, time, false);
	}
	
	static boolean passed(Object expected, Object answer) {
		if (expected == null || answer == null)
			return false;
		if (expected instanceof Number en && answer instanceof Number an)
			return en.doubleValue() == an.doubleValue();
		return expected.equals(answer);
	}
}
