package org.advent.year2019.intcode_computer;

import java.util.Arrays;

public interface InputProvider {
	
	boolean hasNext();
	long nextInput();
	
	
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
}
