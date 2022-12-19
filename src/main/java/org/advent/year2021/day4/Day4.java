package org.advent.year2021.day4;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day4 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day4.class, "input.txt");
		
		List<Integer> numbers = Arrays.stream(input.nextLine().split(",")).map(Integer::valueOf).toList();
		List<Board> boards = new ArrayList<>();
		
		List<int[]> lines = new ArrayList<>();
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty()) {
				if (!lines.isEmpty())
					boards.add(new Board(lines.toArray(int[][]::new)));
				lines = new ArrayList<>();
				continue;
			}
			lines.add(Arrays.stream(line.split("\\s+")).filter(s -> !s.isEmpty()).mapToInt(Integer::parseInt).toArray());
		}
		if (!lines.isEmpty())
			boards.add(new Board(lines.toArray(int[][]::new)));
		
		System.out.println("Answer 1: " + part1(numbers, boards));
		System.out.println("Answer 2: " + part2(numbers, boards));
	}
	
	private static int part1(List<Integer> numbers, List<Board> boards) {
		Set<Integer> playedNumbers = new HashSet<>();
		for (Integer number : numbers) {
			playedNumbers.add(number);
			for (Board board : boards)
				if (board.win(playedNumbers))
					return number * board.allNumbers().filter(n -> !playedNumbers.contains(n)).sum();
		}
		return 0;
	}
	
	private static int part2(List<Integer> numbers, List<Board> boards) {
		boards = new ArrayList<>(boards);
		Set<Integer> playedNumbers = new HashSet<>();
		for (Integer number : numbers) {
			playedNumbers.add(number);
			for (Iterator<Board> iterator = boards.iterator(); iterator.hasNext(); ) {
				Board board = iterator.next();
				if (board.win(playedNumbers)) {
					iterator.remove();
					if (boards.isEmpty())
						return number * board.allNumbers().filter(n -> !playedNumbers.contains(n)).sum();
				}
			}
		}
		return 0;
	}
	
	record Board(int[][] cells, List<Set<Integer>> winningSets) {
		Board(int[][] cells) {
			this(cells, winningSets(cells));
		}
		
		boolean win(Set<Integer> numbers) {
			return winningSets.stream().anyMatch(numbers::containsAll);
		}
		
		IntStream allNumbers() {
			return Arrays.stream(cells).flatMapToInt(Arrays::stream);
		}
		
		@Override
		public String toString() {
			return Arrays.stream(cells).map(Arrays::toString).collect(Collectors.joining("\n"));
		}
		
		static List<Set<Integer>> winningSets(int[][] cells) {
			List<Set<Integer>> winningSets = new ArrayList<>();
			for (int[] row : cells)
				winningSets.add(Arrays.stream(row).boxed().collect(Collectors.toSet()));
			for (int col = 0; col < cells[0].length; col++) {
				Set<Integer> column = new HashSet<>();
				for (int[] row : cells)
					column.add(row[col]);
				winningSets.add(column);
			}
			return winningSets;
		}
	}
}