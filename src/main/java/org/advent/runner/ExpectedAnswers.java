package org.advent.runner;

public record ExpectedAnswers(String file, Object answer1, Object answer2) {
	public static final ExpectedAnswers IGNORE = new ExpectedAnswers(null, null, null);
}
