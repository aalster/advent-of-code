package org.advent.year2019.intcode_computer;

import java.util.Arrays;

public interface InputProvider {
	
	boolean hasNext();
	int nextInput();
	
	
	static InputProvider constant(int... values) {
		int[] copy = Arrays.copyOf(values, values.length);
		return new InputProvider() {
			int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < copy.length;
			}
			
			@Override
			public int nextInput() {
				if (hasNext())
					return copy[index++];
				throw new IllegalStateException("All input values are used");
			}
		};
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
			public int nextInput() {
				return copy[index++ % copy.length];
			}
		};
	}
}
