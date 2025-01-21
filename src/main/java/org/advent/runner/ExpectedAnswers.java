package org.advent.runner;

public record ExpectedAnswers(String file, Object answer1, Object answer2) {
	public static final ExpectedAnswers IGNORE = new ExpectedAnswers(null, null, null);
	
	Object answer(int part) {
		return switch (part) {
			case 1 -> answer1;
			case 2 -> answer2;
			default -> throw new IllegalStateException("Unexpected value: " + part);
		};
	}
}
