package org.advent.runner;

import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class DayRunner {
	private final AdventDay day;
	
	public PuzzleResultStats runAll() {
		System.out.println(day);
		return PuzzleResultStats.combineAll(day.expected().stream().map(f -> run(f, true, true)));
	}
	
	public PuzzleResultStats run(String file) {
		return run(expected(file), false, false);
	}
	
	PuzzleResultStats runForYear(String file) {
		if (file != null)
			return run(expected(file), false, true);
		return PuzzleResultStats.combineAll(day.expected().stream().map(f -> run(f, true, true)));
	}
	
	public PuzzleResult run(String file, int part) {
		ExpectedAnswers expected = expected(file);
		day.prepare(expected.file());
		PuzzleResult result = switch (part) {
			case 1 -> runPart(day, expected.answer1(), day::part1, part);
			case 2 -> runPart(day, expected.answer2(), day::part2, part);
			default -> throw new IllegalStateException("Unexpected value: " + part);
		};
		return result.print(0).propagateError();
	}
	
	public PuzzleResultStats run(ExpectedAnswers expected, boolean printInfo, boolean pad) {
		Timer timer = new Timer();
		day.prepare(expected.file());
		if (printInfo)
			System.out.println("    " + OutputUtils.white(expected.file()) + " (prepare " + timer.stepFormatted() + "):");
		
		PuzzleResult part1 = runPart(day, expected.answer1(), day::part1, 1).print(pad ? 6 : 0);
		PuzzleResult part2 = runPart(day, expected.answer2(), day::part2, 2).print(pad ? 6 : 0);
		return PuzzleResultStats.combineAll(part1.stats(), part2.stats());
	}
	
	private PuzzleResult runPart(AdventDay day, Object expected, Supplier<Object> solution, int part) {
		if (expected == ExpectedAnswers.IGNORE)
			return PuzzleResult.ignored(day, part, expected);
		
		Timer timer = new Timer();
		try {
			Object answer = solution.get();
			return PuzzleResult.result(day, part, expected, answer, timer.time());
		} catch (Exception e) {
			return PuzzleResult.error(day, part, expected, e, timer.time());
		}
	}
	
	private ExpectedAnswers expected(String file) {
		for (ExpectedAnswers expectedAnswers : day.expected())
			if (file.equals(expectedAnswers.file()))
				return expectedAnswers;
		return new ExpectedAnswers(file, null, null);
	}
}
