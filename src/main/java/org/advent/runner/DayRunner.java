package org.advent.runner;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
	
	public void time(String file, int part, int iterations) {
		ExpectedAnswers expected = expected(file);
		List<PuzzleResultStats> stats = new ArrayList<>(iterations);
		for (int i = 0; i < iterations; i++)
			stats.add(run(expected, part, true));
		PuzzleResultStats result = PuzzleResultStats.combineAll(stats.stream());
		System.out.println(result.summary());
		System.out.printf(Locale.US, "Average time: %.3fms\n", (float) result.totalTime() / iterations);
	}
	
	PuzzleResultStats runForYear(String file, boolean silent) {
		if (file != null)
			return run(expected(file), 0, silent);
		return PuzzleResultStats.combineAll(day.expected().stream().map(f -> run(f, 0, silent)));
	}
	
	private PuzzleResultStats run(ExpectedAnswers expected, int part, boolean silent) {
		String title = day + " " + OutputUtils.leftPad(expected.file(), 13, OutputUtils::white) + ": ";
		return PuzzleResultStats.combineAll(PuzzleRunner.allParts(day, expected)
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
