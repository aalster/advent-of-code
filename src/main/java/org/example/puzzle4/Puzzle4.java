package org.example.puzzle4;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.Utils;

import java.util.Scanner;

public class Puzzle4 {
	
	public static void main1(String[] args) {
		Scanner input = Utils.scanFileNearClass(Puzzle4.class,"input.txt");
		int result = 0;
		while (input.hasNext()) {
			String[] pairs = input.nextLine().split(",");
			Section first = Section.parse(pairs[0]);
			Section second = Section.parse(pairs[1]);
			
			if (first.start() == second.start()) {
				result++;
				continue;
			}
			if (first.start() < second.start()) {
				if (second.end() <= first.end())
					result++;
			} else {
				if (first.end() <= second.end())
					result++;
			}
		}
		System.out.println(result);
	}
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Puzzle4.class,"input.txt");
		int result = 0;
		while (input.hasNext()) {
			String[] pairs = input.nextLine().split(",");
			Section first = Section.parse(pairs[0]);
			Section second = Section.parse(pairs[1]);
			
			if (first.start() <= second.end() && second.start() <= first.end())
				result++;
		}
		System.out.println(result);
	}
	
	record Section(int start, int end) {
		static Section parse(String value) {
			String[] split = value.split("-");
			int start = Integer.parseInt(split[0]);
			int end = Integer.parseInt(split[1]);
			return new Section(start, end);
		}
	}
}