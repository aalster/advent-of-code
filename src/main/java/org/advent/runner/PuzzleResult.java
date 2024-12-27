package org.advent.runner;

import java.util.function.Supplier;

public record PuzzleResult(Object answer, long time) {
	
	static PuzzleResult run(Supplier<Object> puzzle) {
		long start = System.currentTimeMillis();
		Object answer = puzzle.get();
		return new PuzzleResult(answer, System.currentTimeMillis() - start);
	}
}
