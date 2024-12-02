package org.advent.year2022.day13;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day13 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day13.class, "input.txt");
		List<Element> elements = new ArrayList<>();
		while (input.hasNext()) {
			String line = input.nextLine();
			if (!line.isEmpty())
				elements.add(Element.parse(line));
		}
		
		System.out.println("Answer 1: " + part1(elements));
		System.out.println("Answer 2: " + part2(elements));
	}
	
	private static int part1(List<Element> elements) {
		int result = 0;
		int pair = 1;
		Iterator<Element> iterator = elements.iterator();
		while (iterator.hasNext()) {
			Element left = iterator.next();
			Element right = iterator.next();
			int compare = left.compareTo(right);
			
			if (compare <= 0)
				result += pair;
			pair++;
		}
		return result;
	}
	
	private static int part2(List<Element> elements) {
		Set<Element> dividers = Set.of(divider(2), divider(6));
		
		elements = new ArrayList<>(elements);
		elements.addAll(dividers);
		elements.sort(Comparator.naturalOrder());
		
		int result = 1;
		int pair = 1;
		for (Element element : elements) {
			if (dividers.contains(element))
				result *= pair;
			pair++;
		}
		return result;
	}
	
	static ListElement divider(int value) {
		return new ListElement(List.of(new ListElement(List.of(new NumberElement(value)))));
	}
	
	sealed interface Element extends Comparable<Element> {
		
		static Element parse(String value) {
			if (value.startsWith("["))
				return ListElement.parse(value.substring(1, value.length() - 1));
			return NumberElement.parse(value);
		}
	}
	
	record NumberElement(int value) implements Element {
			@Override
			public int compareTo(Element other) {
				if (other instanceof NumberElement(int v))
					return Integer.compare(value, v);
				if (other instanceof ListElement otherList)
					return - otherList.compareTo(new ListElement(List.of(this)));
				throw new RuntimeException("Unknown type: " + other.getClass());
			}
			
			@Override
			public String toString() {
				return "" + value;
			}
			
			static Element parse(String value) {
				return new NumberElement(Integer.parseInt(value));
			}
		}
	
	record ListElement(List<Element> children) implements Element {
		
		@Override
		public int compareTo(Element other) {
			if (other instanceof ListElement(List<Element> c)) {
				Iterator<Element> left = children.iterator();
				Iterator<Element> right = c.iterator();
				while (true) {
					if (!left.hasNext())
						return right.hasNext() ? -1 : 0;
					if (!right.hasNext())
						return 1;
					int compare = left.next().compareTo(right.next());
					if (compare != 0)
						return compare;
				}
			} else if (other instanceof NumberElement otherNumber) {
				return compareTo(new ListElement(List.of(otherNumber)));
			} else {
				throw new RuntimeException("Not supported for " + other.getClass());
			}
		}
		
		@Override
		public String toString() {
			return children.stream().map(Objects::toString).collect(Collectors.joining(", ", "[", "]"));
		}
		
		static ListElement parse(String value) {
			if (value.isEmpty())
				return new ListElement(List.of());
			
			List<String> parts = new ArrayList<>();
			int bracketsCount = 0;
			int start = 0;
			for (int i = 0; i < value.length(); i++) {
				char c = value.charAt(i);
				switch (c) {
					case ',' -> {
						if (bracketsCount == 0) {
							parts.add(value.substring(start, i));
							start = i + 1;
						}
					}
					case '[' -> bracketsCount++;
					case ']' -> bracketsCount--;
					default -> {}
				}
			}
			parts.add(value.substring(start));
			return new ListElement(parts.stream().map(Element::parse).toList());
		}
	}
}