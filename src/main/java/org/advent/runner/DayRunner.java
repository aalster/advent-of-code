package org.advent.runner;

import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public class DayRunner {
	private final AdventDay day;
	
	public PuzzleResultStats runAll() {
		return PuzzleResultStats.combineAll(day.expected().stream().map(f -> run(f, 0, false)));
	}
	
	public PuzzleResultStats run(String file) {
		return run(file, 0);
	}
	
	public PuzzleResultStats run(String file, int part) {
		return run(expected(file), part, false);
	}
	
	PuzzleResultStats runForYear(String file, boolean silent) {
		if (file != null)
			return run(expected(file), 0, silent);
		return PuzzleResultStats.combineAll(day.expected().stream().map(f -> run(f, 0, silent)));
	}
	
	private PuzzleResultStats run(ExpectedAnswers expected, int part, boolean silent) {
		day.prepare(expected.file());
		
		String title = day + " " + OutputUtils.leftPad(expected.file(), 13, OutputUtils::white) + ": ";
		return PuzzleResultStats.combineAll(Stream.of(
						new PuzzleRunner(day, expected.answer1(), day::part1, 1),
						new PuzzleRunner(day, expected.answer2(), day::part2, 2))
				.filter(PuzzleRunner::notIgnored)
				.filter(p -> part == 0 || part == p.part())
				.map(PuzzleRunner::run)
				.peek(silent ? r -> {} : r -> System.out.println(title + r.presentation()))
				.map(r -> part > 0 ? r.propagateError() : r)
				.map(PuzzleResult::stats));
	}
	
	private ExpectedAnswers expected(String file) {
		for (ExpectedAnswers expectedAnswers : day.expected())
			if (file.equals(expectedAnswers.file()))
				return expectedAnswers;
		return new ExpectedAnswers(file, null, null);
	}
}
