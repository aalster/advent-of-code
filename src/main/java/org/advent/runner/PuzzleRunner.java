package org.advent.runner;

import java.util.stream.Stream;

public record PuzzleRunner(AdventDay day, ExpectedAnswers expected, int part) {
	
	public boolean notIgnored() {
		return expected.answer(part) != ExpectedAnswers.IGNORE;
	}
	
	public PuzzleResult run() {
		day.prepare(expected.file());
		Timer timer = new Timer();
		try {
			Object answer = day.part(part);
			return PuzzleResult.result(day, part, expected.answer(part), answer, timer.time());
		} catch (Exception e) {
			return PuzzleResult.error(day, part, expected.answer(part), e, timer.time());
		}
	}
	
	public static Stream<PuzzleRunner> allParts(AdventDay day, ExpectedAnswers expected) {
		return Stream.of(new PuzzleRunner(day, expected, 1), new PuzzleRunner(day, expected, 2));
	}
}
