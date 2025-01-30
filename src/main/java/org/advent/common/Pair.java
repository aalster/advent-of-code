package org.advent.common;

import java.util.stream.Stream;

public record Pair<L, R>(L left, R right) {
	
	public static <L, R> Pair<L, R> of(L left, R right) {
		return new Pair<>(left, right);
	}
	
	public static <T> Stream<T> stream(Pair<T, T> pair) {
		return Stream.of(pair.left(), pair.right());
	}
}