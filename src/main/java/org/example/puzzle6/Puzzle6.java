package org.example.puzzle6;

import org.example.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class Puzzle6 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Puzzle6.class,"input.txt");
//		int size = 4;
		int size = 14;
		while (input.hasNext()) {
			System.out.println(start(input.nextLine(), size));
		}
	}
	
	private static int start(String line, int size) {
		if (line.length() < size)
			return 0;
		
		Function<List<Integer>, Boolean> different = list -> list.size() == new HashSet<>(list).size();
		
		List<Integer> chars = new ArrayList<>(line.substring(0, size).chars().boxed().toList());
		if (different.apply(chars))
			return size + 1;
		for (int i = size; i < line.length(); i++) {
			int c = line.charAt(i);
			chars.add(c);
			chars.remove(0);
			if (different.apply(chars))
				return i + 1;
		}
		return 0;
	}
}