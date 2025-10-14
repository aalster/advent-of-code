package org.advent.runner;

import org.advent.common.Utils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

public record PuzzleResultStats(Set<String> days, int total, int failed, int errors, long totalTime) {
	public static final PuzzleResultStats EMPTY = new PuzzleResultStats(Set.of(), 0, 0, 0, 0);
	
	public PuzzleResultStats(String day, boolean passed, boolean error, long time) {
		this(Set.of(day), 1, passed ? 0 : 1, error ? 1 : 0, time);
	}
	
	int passed() {
		return total - failed - errors;
	}
	
	private PuzzleResultStats combine(PuzzleResultStats other) {
		return new PuzzleResultStats(
				Utils.combineToSet(days, other.days),
				total + other.total,
				failed + other.failed,
				errors + other.errors,
				totalTime + other.totalTime);
	}
	
	public String dayResult() {
		return total == 0 ? "➖" : failed + errors > 0 ? "❌" :
				totalTime / total < 500 ? "✅" : totalTime / total < 5000 ? "☑️" : "⚠️";
	}
	
	public String summary() {
		return OutputUtils.white("Finished after " + totalTime + "ms.") +
				" Days: " + OutputUtils.white(days.size()) + "." +
				" Tests passed: " + OutputUtils.green(passed()) + "." +
				" Failed: " + (failed > 0 ? OutputUtils.red(failed) : failed) + "." +
				" Errors: " + (errors > 0 ? OutputUtils.red(errors) : errors) + ".";
	}
	
	public static PuzzleResultStats combineAll(PuzzleResultStats... stats) {
		return combineAll(Arrays.stream(stats));
	}
	
	public static PuzzleResultStats combineAll(Stream<PuzzleResultStats> stats) {
		return stats.reduce(EMPTY, PuzzleResultStats::combine);
	}
}
