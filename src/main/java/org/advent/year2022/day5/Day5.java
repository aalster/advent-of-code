package org.advent.year2022.day5;

import org.apache.commons.lang3.StringUtils;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5 {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day5.class,"input.txt");
		Stack<String>[] stacks = null;
		
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty())
				break;
			if (stacks == null) {
				stacks = (Stack<String>[]) new Stack[(line.length() + 1) / 4];
				for (int i = 0; i < stacks.length; i++)
					stacks[i] = new Stack<>();
			}
			int stackNumber = 0;
			while (true) {
				String crate = line.substring(1, 2);
				if (StringUtils.isNumeric(crate))
					break;
				if (!crate.isBlank())
					stacks[stackNumber].addBottom(crate);
				line = line.substring(3);
				if (line.isEmpty())
					break;
				line = line.substring(1);
				stackNumber++;
			}
		}
		
		for (Stack<String> stack : stacks) {
			System.out.println(stack.toString());
		}
		
		Pattern pattern = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)");
		while (input.hasNext()) {
			Matcher matcher = pattern.matcher(input.nextLine());
			if (matcher.find()) {
				System.out.println(matcher.group());
				int count = Integer.parseInt(matcher.group(1));
				int from = Integer.parseInt(matcher.group(2)) - 1;
				int to = Integer.parseInt(matcher.group(3)) - 1;
//				stacks[from].moveTo(stacks[to], count);
				stacks[from].moveToOrdered(stacks[to], count);
			}
		}
		
		for (Stack<String> stack : stacks) {
			System.out.println(stack.toString());
		}
		
		System.out.println(Stream.of(stacks).map(Stack::getTop).collect(Collectors.joining()));
	}
	
	static class Stack<T> {
		private List<T> elements = new ArrayList<>();
		
		void add(T element) {
			elements.add(element);
		}
		
		void addAll(Collection<T> elements) {
			elements.addAll(elements);
		}
		
		void addBottom(T element) {
			elements.add(0, element);
		}
		
		T removeTop() {
			return elements.remove(size() - 1);
		}
		
		T getTop() {
			return elements.get(size() - 1);
		}
		
		void moveTo(Stack<T> other, int count) {
			int i = count;
			while (i > 0) {
				other.add(removeTop());
				i--;
			}
		}
		
		void moveToOrdered(Stack<T> other, int count) {
			Stack<T> temp = new Stack<>();
			moveTo(temp, count);
			temp.moveTo(other, count);
		}
		
		int size() {
			return elements.size();
		}
		
		@Override
		public String toString() {
			return elements.toString();
		}
	}
}