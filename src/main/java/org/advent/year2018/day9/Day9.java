package org.advent.year2018.day9;

import lombok.Data;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day9 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day9()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 32, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 8317, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 146373, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example4.txt", 2764, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example5.txt", 54718, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example6.txt", 37305, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 398371, 3212830280L)
		);
	}
	
	int playersCount;
	int lastMarble;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		String[] split = input.nextLine().split(" ");
		playersCount = Integer.parseInt(split[0]);
		lastMarble = Integer.parseInt(split[6]);
	}
	
	@Override
	public Object part1() {
		return maxScore(playersCount, lastMarble);
	}
	
	@Override
	public Object part2() {
		return maxScore(playersCount, lastMarble * 100);
	}
	
	long maxScore(int playersCount, int lastMarbleScore) {
		long[] scores = new long[playersCount];
		Circle<Integer> marbles = new Circle<>(0);
		for (int marble = 1; marble <= lastMarbleScore; marble++) {
			if (marble % 23 == 0) {
				marbles.rotate(-7);
				scores[marble % playersCount] += marble + marbles.remove();
			} else {
				marbles.rotate(1);
				marbles.add(marble);
			}
		}
		return Arrays.stream(scores).max().orElse(0);
	}
	
	static class Circle<T> {
		Node<T> current;
		
		public Circle(T element) {
			current = new Node<>(element);
			Node.chain(current, current);
		}
		
		void rotate(int len) {
			while (len > 0) {
				current = current.next;
				len--;
			}
			while (len < 0) {
				current = current.prev;
				len++;
			}
		}
		
		void add(T element) {
			Node<T> prev = current;
			Node<T> next = current.next;
			current = new Node<>(element);
			Node.chain(prev, current);
			Node.chain(current, next);
		}
		
		T remove() {
			T result = current.element;
			Node<T> prev = current.prev;
			current = current.next;
			Node.chain(prev, current);
			return result;
		}
	}
	
	@Data
	static class Node<T> {
		final T element;
		Node<T> next;
		Node<T> prev;
		
		static <T> void chain(Node<T> prev, Node<T> next) {
			prev.next = next;
			next.prev = prev;
		}
	}
}