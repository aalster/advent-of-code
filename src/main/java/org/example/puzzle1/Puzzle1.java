package org.example.puzzle1;

import org.example.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Puzzle1 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Puzzle1.class,"input.txt");
		List<Long> allCalories = new ArrayList<>();
		int n = 3;
		
		while (input.hasNext()) {
			long calories = 0;
			while (input.hasNext()) {
				String next = input.nextLine();
				if (next.isEmpty())
					break;
				calories += Long.parseLong(next);
			}
			allCalories.add(calories);
		}
		allCalories.sort(Comparator.<Long>naturalOrder().reversed());
		System.out.println("all: " + allCalories);
		System.out.println("top n: " + allCalories.subList(0, n).stream().mapToLong(c -> c).sum());
	}
}