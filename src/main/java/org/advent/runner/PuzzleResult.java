package org.advent.runner;

import lombok.SneakyThrows;

public record PuzzleResult(String day, int part, Object expected, Object answer, Exception exception, long time, boolean passed) {
	
	PuzzleResult print(int pad) {
		if (expected != ExpectedAnswers.IGNORE) {
			String result = exception == null
					? (passed ? "✅" : "❌") + " " + OutputUtils.white(answer) + (expected != null && !passed ? " Expected: " + expected : "")
					: "❌ " + OutputUtils.red("Error: " + exception.getMessage());
			System.out.println(" ".repeat(pad) + "Answer " + part + " " + Timer.formatTime(time, 7) + ": " + result);
		}
		return this;
	}
	
	@SneakyThrows
	PuzzleResult propagateError() {
		if (exception != null)
			throw exception;
		return this;
	}
	
	PuzzleResultStats stats() {
		return expected == ExpectedAnswers.IGNORE ? null : new PuzzleResultStats(day, passed, exception != null, time);
	}
	
	public static PuzzleResult result(AdventDay day, int part, Object expected, Object answer, long time) {
		return new PuzzleResult(dayKey(day), part, expected, answer, null, time, passed(expected, answer));
	}
	
	public static PuzzleResult error(AdventDay day, int part, Object expected, Exception exception, long time) {
		return new PuzzleResult(dayKey(day), part, expected, null, exception, time, false);
	}
	
	public static PuzzleResult ignored(AdventDay day, int part, Object expected) {
		return new PuzzleResult(dayKey(day), part, expected, null, null, 0, false);
	}
	
	static String dayKey(AdventDay day) {
		return day.getYear() + "-" + day.getDay();
	}
	
	static boolean passed(Object expected, Object answer) {
		if (expected == null || answer == null)
			return false;
		if (expected instanceof Number en && answer instanceof Number an)
			return en.doubleValue() == an.doubleValue();
		return expected.equals(answer);
	}
}
