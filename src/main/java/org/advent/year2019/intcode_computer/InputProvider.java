package org.advent.year2019.intcode_computer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Scanner;

public interface InputProvider {
	
	boolean hasNext();
	long nextInput();
	
	
	static InputProvider empty() {
		return constant();
	}
	
	static InputProvider repeated(long value, int repeats) {
		long[] values = new long[repeats];
		Arrays.fill(values, value);
		return constant(values);
	}
	
	static InputProvider constant(long... values) {
		long[] copy = Arrays.copyOf(values, values.length);
		return new InputProvider() {
			int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < copy.length;
			}
			
			@Override
			public long nextInput() {
				if (hasNext())
					return copy[index++];
				throw new IllegalStateException("All input values are used");
			}
		};
	}
	
	static InputProvider ascii(String text) {
		return constant(text.chars().mapToLong(n -> n).toArray());
	}
	
	static InputProvider circular(int[] values) {
		int[] copy = Arrays.copyOf(values, values.length);
		return new InputProvider() {
			int index = 0;
			
			@Override
			public boolean hasNext() {
				return true;
			}
			
			@Override
			public long nextInput() {
				return copy[index++ % copy.length];
			}
		};
	}
	
	static InputProvider console() {
		Scanner scanner = new Scanner(System.in);
		Queue<Integer> queue = new LinkedList<>();
		
		return new InputProvider() {
			
			@Override
			public boolean hasNext() {
				return true;
			}
			
			@Override
			public long nextInput() {
				if (queue.isEmpty()) {
					scanner.nextLine().chars().forEach(queue::add);
					queue.add((int) '\n');
				}
				return Objects.requireNonNull(queue.poll());
			}
		};
	}
}
