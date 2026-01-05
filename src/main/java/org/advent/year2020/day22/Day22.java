package org.advent.year2020.day22;

import lombok.RequiredArgsConstructor;
import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Day22 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day22()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 306, 291),
				new ExpectedAnswers("input.txt", 34324, 33259)
		);
	}
	
	IntLinkedList deck1;
	IntLinkedList deck2;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<IntLinkedList> decks = Utils.splitByEmptyLine(Utils.readLines(input)).stream()
				.map(l -> l.stream().skip(1).mapToInt(Integer::parseInt).toArray())
				.map(IntLinkedList::new)
				.toList();
		deck1 = decks.getFirst();
		deck2 = decks.getLast();
	}
	
	@Override
	public Object part1() {
		while (!deck1.isEmpty() && !deck2.isEmpty()) {
			int card1 = deck1.removeFirst();
			int card2 = deck2.removeFirst();
			if (card1 > card2) {
				deck1.add(card1);
				deck1.add(card2);
			} else {
				deck2.add(card2);
				deck2.add(card1);
			}
		}
		return score(deck2.isEmpty() ? deck1 : deck2);
	}
	
	@Override
	public Object part2() {
		return play(deck1, deck2, true).score;
	}
	
	static int score(IntLinkedList deck) {
		int result = 0;
		IntNode node = deck.head;
		int value = deck.size;
		while (node != null) {
			result += value * node.value;
			node = node.next;
			value--;
		}
		return result;
	}
	
	static GameResult play(IntLinkedList deck1, IntLinkedList deck2, boolean returnScore) {
		Set<Pair<IntLinkedList, IntLinkedList>> states = new HashSet<>();
		while (!deck1.isEmpty() && !deck2.isEmpty()) {
			if (!states.add(Pair.of(deck1, deck2)))
				return new GameResult(true, returnScore ? score(deck1) : 0);
			
			int card1 = deck1.removeFirst();
			int card2 = deck2.removeFirst();
			
			boolean player1Wins = card1 > deck1.size || card2 > deck2.size ? card1 > card2
					: play(deck1.limit(card1), deck2.limit(card2), false).player1Wins;
			
			if (player1Wins) {
				deck1.add(card1);
				deck1.add(card2);
			} else {
				deck2.add(card2);
				deck2.add(card1);
			}
		}
		return new GameResult(deck2.isEmpty(), returnScore ? score(deck2.isEmpty() ? deck1 : deck2) : 0);
	}
	
	record GameResult(boolean player1Wins, int score) {
	}
	
	@RequiredArgsConstructor
	static class IntNode {
		final int value;
		IntNode next;
	}
	
	static class IntLinkedList {
		IntNode head;
		IntNode tail;
		int size;
		int hashCode = 0;
		
		IntLinkedList() {
		}
		
		IntLinkedList(int[] values) {
			for (int value : values)
				add(value);
		}
		
		void add(int value) {
			IntNode node = new IntNode(value);
			if (head == null)
				head = node;
			else
				tail.next = node;
			tail = node;
			size++;
			hashCode = 0;
		}
		
		int removeFirst() {
			int result = head.value;
			head = head.next;
			size--;
			hashCode = 0;
			return result;
		}
		
		boolean isEmpty() {
			return size == 0;
		}
		
		IntLinkedList limit(int size) {
			IntLinkedList result = new IntLinkedList();
			IntNode node = head;
			while (node != null && size-- > 0) {
				result.add(node.value);
				node = node.next;
			}
			return result;
		}
		
		@Override
		public int hashCode() {
			if (hashCode == 0) {
				IntNode node = head;
				while (node != null) {
					hashCode = hashCode * 37 + node.value;
					node = node.next;
				}
			}
			return hashCode;
		}
		
		@SuppressWarnings("EqualsDoesntCheckParameterClass")
		@Override
		public boolean equals(Object obj) {
			IntLinkedList other = (IntLinkedList) obj;
			if (other.size != size)
				return false;
			IntNode left = head;
			IntNode right = other.head;
			while (left != null) {
				if (left.value != right.value)
					return false;
				left = left.next;
				right = right.next;
			}
			return true;
		}
	}
}