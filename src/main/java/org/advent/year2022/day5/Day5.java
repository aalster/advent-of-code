package org.advent.year2022.day5;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day5 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day5()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "CMZ", "MCD"),
				new ExpectedAnswers("input.txt", "ZSQVCCJLL", "QZFJRWHGS")
		);
	}
	
	List<Stack<String>> stacks;
	List<Move> moves;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		
		stacks = new ArrayList<>();
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
		moves = Utils.readLines(input).stream().map(Move::parse).toList();
	}
	
	@Override
	public Object part1() {
		List<Stack<String>> stacksCopy = stacks.stream().map(Stack::copy).toList();
		for (Move move : moves)
			stacksCopy.get(move.from()).moveTo(stacksCopy.get(move.to()), move.count());
		return stacksCopy.stream().map(Stack::getTop).collect(Collectors.joining());
	}
	
	@Override
	public Object part2() {
		List<Stack<String>> stacksCopy = stacks.stream().map(Stack::copy).toList();
		for (Move move : moves)
			stacksCopy.get(move.from()).moveToOrdered(stacksCopy.get(move.to()), move.count());
		return stacksCopy.stream().map(Stack::getTop).collect(Collectors.joining());
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