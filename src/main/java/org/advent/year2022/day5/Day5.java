package org.advent.year2022.day5;

import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day5 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day5.class,"input.txt");
		
		List<Stack<String>> stacks = new ArrayList<>();
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty())
				break;
			if (stacks.isEmpty()) {
				int count = (line.length() + 1) / 4;
				while (count > 0) {
					stacks.add(new Stack<>());
					count--;
				}
			}
			
			int index = 0;
			for (Stack<String> stack : stacks) {
				String crate = line.substring(index * 4 + 1, index * 4 + 2);
				if (StringUtils.isNumeric(crate))
					break;
				if (!crate.isBlank())
					stack.addBottom(crate);
				index++;
			}
		}
		
		List<Move> moves = new ArrayList<>();
		while (input.hasNext())
			moves.add(Move.parse(input.nextLine()));
		
		System.out.println("Answer 1: " + part1(stacks, moves));
		System.out.println("Answer 2: " + part2(stacks, moves));
	}
	
	private static String part1(List<Stack<String>> stacks, List<Move> moves) {
		stacks = stacks.stream().map(Stack::copy).toList();
		for (Move move : moves)
			stacks.get(move.from()).moveTo(stacks.get(move.to()), move.count());
		return stacks.stream().map(Stack::getTop).collect(Collectors.joining());
	}
	
	private static String part2(List<Stack<String>> stacks, List<Move> moves) {
		stacks = stacks.stream().map(Stack::copy).toList();
		for (Move move : moves)
			stacks.get(move.from()).moveToOrdered(stacks.get(move.to()), move.count());
		return stacks.stream().map(Stack::getTop).collect(Collectors.joining());
	}
	
	static class Stack<T> {
		private final List<T> elements = new ArrayList<>();
		
		void add(T element) {
			elements.add(element);
		}
		
		void addBottom(T element) {
			elements.addFirst(element);
		}
		
		T removeTop() {
			return elements.removeLast();
		}
		
		T getTop() {
			return elements.getLast();
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
		
		Stack<T> copy() {
			Stack<T> copy = new Stack<>();
			copy.elements.addAll(elements);
			return copy;
		}
		
		@Override
		public String toString() {
			return elements.toString();
		}
	}
	
	record Move(int count, int from, int to) {
		
		static Pattern pattern = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)");
		
		static Move parse(String line) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.find())
				throw new RuntimeException("Invalid move: " + line);
			
			int count = Integer.parseInt(matcher.group(1));
			int from = Integer.parseInt(matcher.group(2)) - 1;
			int to = Integer.parseInt(matcher.group(3)) - 1;
			return new Move(count, from, to);
		}
	}
}