package org.advent.year2021.day4;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day4 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day4()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 4512, 1924),
				new ExpectedAnswers("input.txt", 16716, 4880)
		);
	}
	
	List<Integer> numbers;
	List<Board> boards;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		
		numbers = Arrays.stream(input.nextLine().split(",")).map(Integer::valueOf).toList();
		boards = new ArrayList<>();
		
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
	}
	
	@Override
	public Object part1() {
		Set<Integer> playedNumbers = new HashSet<>();
		for (Integer number : numbers) {
			playedNumbers.add(number);
			for (Board board : boards)
				if (board.win(playedNumbers))
					return number * board.allNumbers().filter(n -> !playedNumbers.contains(n)).sum();
		}
		return 0;
	}
	
	@Override
	public Object part2() {
		List<Board> currentBoards = new ArrayList<>(boards);
		Set<Integer> playedNumbers = new HashSet<>();
		for (Integer number : numbers) {
			playedNumbers.add(number);
			for (Iterator<Board> iterator = currentBoards.iterator(); iterator.hasNext(); ) {
				Board board = iterator.next();
				if (board.win(playedNumbers)) {
					iterator.remove();
					if (currentBoards.isEmpty())
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