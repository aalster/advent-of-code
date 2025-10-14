package org.advent.year2019.intcode_computer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Scanner;

public interface InputProvider {
	InputProvider EMPTY = constant();
	
	boolean hasNext();
	long nextInput();
	
	default String nextLine() {
		StringBuilder line = new StringBuilder();
		char c;
		while ((c = (char) nextInput()) != '\n')
			line.append(c);
		return line.toString();
	}
	
	
	static InputProvider empty() {
		return EMPTY;
	}
	
	static InputProvider combine(InputProvider... providers) {
		if (providers == null || providers.length == 0)
			return EMPTY;
		if (providers.length == 1)
			return providers[0];
		return new InputProvider() {
			final Queue<InputProvider> providersQueue = new LinkedList<>(List.of(providers));
			InputProvider currentProvider = providersQueue.poll();
			
			InputProvider current() {
				if (currentProvider != null && currentProvider.hasNext())
					return currentProvider;
				currentProvider = providersQueue.poll();
				return currentProvider == null ? EMPTY : currentProvider;
			}
			
			@Override
			public boolean hasNext() {
				return current().hasNext();
			}
			
			@Override
			public long nextInput() {
				return current().nextInput();
			}
		};
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
		return new BufferingInputProvider() {
			final Scanner scanner = new Scanner(System.in);
			
			@Override
			public boolean hasNext() {
				return true;
			}
			
			@Override
			public long nextInput() {
				if (!super.hasNext())
					append(scanner.nextLine() + "\n");
				return super.nextInput();
			}
		};
	}
	
	static BufferingInputProvider buffering() {
		return new BufferingInputProvider();
	}
	
	static BufferingInputProvider buffering(String text) {
		return buffering().append(text);
	}
	
	class BufferingInputProvider implements InputProvider {
		private final Queue<Integer> queue = new LinkedList<>();
		
		public BufferingInputProvider append(String content) {
			content.chars().forEach(queue::add);
			return this;
		}
		
		@Override
		public boolean hasNext() {
			return !queue.isEmpty();
		}
		
		@Override
		public long nextInput() {
			return Objects.requireNonNull(queue.poll());
		}
	}
}
