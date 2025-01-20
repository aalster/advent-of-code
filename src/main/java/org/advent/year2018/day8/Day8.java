package org.advent.year2018.day8;

import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day8 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day8()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 138, 66),
				new ExpectedAnswers("input.txt", 41555, 16653)
		);
	}
	
	Node root;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		root = Node.parse(Arrays.stream(input.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray());
	}
	
	@Override
	public Object part1() {
		return root.metadataSum();
	}
	
	@Override
	public Object part2() {
		return root.value();
	}
	
	record Node(Node[] children, int[] metadata) {
		
		int metadataSum() {
			return Arrays.stream(children).mapToInt(Node::metadataSum).sum() + Arrays.stream(metadata).sum();
		}
		
		int value() {
			if (children.length == 0)
				return Arrays.stream(metadata).sum();
			
			int value = 0;
			for (int m : metadata)
				if (0 < m && m <= children.length)
					value += children[m - 1].value();
			return value;
		}
		
		static Node parse(int[] numbers) {
			return parse(numbers, 0).left();
		}
		
		static Pair<Node, Integer> parse(int[] numbers, int index) {
			int childrenCount = numbers[index++];
			int metadataCount = numbers[index++];
			
			Node[] children = new Node[childrenCount];
			for (int i = 0; i < childrenCount; i++) {
				Pair<Node, Integer> child = parse(numbers, index);
				children[i] = child.left();
				index = child.right();
			}
			
			int[] metadata = Arrays.copyOfRange(numbers, index, index + metadataCount);
			index += metadataCount;
			
			return Pair.of(new Node(children, metadata), index);
		}
	}
}