package org.advent.common;

import java.math.BigInteger;
import java.util.Collection;

public class NumbersAdventUtils {
	
	public static long lcm(int[] element_array) {
		long lcm_of_array_elements = 1;
		int divisor = 2;
		
		while (true) {
			int counter = 0;
			boolean divisible = false;
			
			for (int i = 0; i < element_array.length; i++) {
				if (element_array[i] == 0)
					return 0;
				else if (element_array[i] < 0)
					element_array[i] = element_array[i] * (-1);
				
				if (element_array[i] == 1)
					counter++;
				
				if (element_array[i] % divisor == 0) {
					divisible = true;
					element_array[i] = element_array[i] / divisor;
				}
			}
			
			if (divisible)
				lcm_of_array_elements = lcm_of_array_elements * divisor;
			else
				divisor++;
			
			if (counter == element_array.length)
				return lcm_of_array_elements;
		}
	}
	
	public static BigInteger lcmBig(Collection<Long> numbers) {
		if (numbers.isEmpty() || numbers.stream().anyMatch(n -> n < 1))
			throw new RuntimeException("Bad numbers");
		
		BigInteger result = BigInteger.ONE;
		long divisor = 2;
		while (!numbers.isEmpty()) {
			long _divisor = divisor;
			if (numbers.stream().anyMatch(n1 -> n1 % _divisor == 0)) {
				result = result.multiply(BigInteger.valueOf(divisor));
				numbers = numbers.stream()
						.map(n -> n % _divisor == 0 ? n / _divisor : n)
						.filter(n -> n > 1)
						.toList();
				continue;
			}
			divisor++;
		}
		return result;
	}
	
	public static int gcd(int a, int b) {
		while (b != 0) {
			int temp = b;
			b = a % b;
			a = temp;
		}
		return a;
	}
}
