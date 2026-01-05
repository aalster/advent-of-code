package org.advent.year2020.day23;

import lombok.RequiredArgsConstructor;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day23 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day23()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 67384529, 149245887792L),
				new ExpectedAnswers("input.txt", 62934785, 693659135400L)
		);
	}
	
	int[] cups;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		cups = input.nextLine().chars().map(c -> c - '0').toArray();
	}
	
	@Override
	public Object part1() {
		Node one = solve(cups, 100);
		Node node = one.next;

		int result = 0;
		while (node != one) {
			result = result * 10 + node.value;
			node = node.next;
		}
		return result;
	}
	
	@Override
	public Object part2() {
		int[] extended = Arrays.copyOf(cups, 1_000_000);
		for (int i = cups.length; i < extended.length; i++)
			extended[i] = i + 1;
		
		Node one = solve(extended, 10_000_000);
		return (long) one.next.value * one.next.next.value;
	}
	
	Node solve(int[] cups, int cycles) {
		Node[] index = initIndex(cups);
		Node current = index[cups[0]];
		
		for (int i = 0; i < cycles; i++) {
			Node first = current.next;
			Node second = first.next;
			Node third = second.next;
			current.next = third.next;
			
			int target = current.value - 1;
			while (target == 0 || target == first.value || target == second.value || target == third.value) {
				target--;
				if (target <= 0)
					target = cups.length;
			}
			
			Node targetNode = index[target];
			third.next = targetNode.next;
			targetNode.next = first;
			
			current = current.next;
		}
		return index[1];
	}
	
	Node[] initIndex(int[] cups) {
		Node[] index = new Node[cups.length + 1];
		
		Node current = new Node(cups[0]);
		current.next = current;
		index[cups[0]] = current;
		
		Node tail = current;
		for (int i = 1; i < cups.length; i++) {
			int value = cups[i];
			tail.next = new Node(value);
			tail = tail.next;
			index[value] = tail;
		}
		tail.next = current;
		
		return index;
	}
	
	@RequiredArgsConstructor
	static class Node {
		final int value;
		Node next;
	}
}