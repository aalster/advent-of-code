package org.advent.year2021.day12;

import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day12 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day12.class, "input.txt");
		List<String> lines = new ArrayList<>();
		while (input.hasNext()) {
			lines.add(input.nextLine());
		}
		Graph graph = Graph.parse(lines);
		
		System.out.println("Answer 1: " + part1(graph));
		System.out.println("Answer 2: " + part2());
	}
	
	private static long part1(Graph graph) {
		List<Path> paths = List.of(Path.init());
		List<Path> finished = new ArrayList<>();
		while (!paths.isEmpty()) {
			paths = paths.stream().flatMap(p -> p.step(graph)).toList();
			Map<Boolean, List<Path>> split = paths.stream().collect(Collectors.groupingBy(p -> p.path().getLast().equals("end")));
			List<Path> currentFinished = split.get(true);
			if (currentFinished != null)
				finished.addAll(currentFinished);
			paths = split.getOrDefault(false, List.of());
		}
		return finished.size();
	}
	
	private static long part2() {
		return 0;
	}
	
	private record Path(List<String> path, Set<String> visitedSmall) {
		
		public Stream<Path> step(Graph graph) {
			return graph.paths().get(path.getLast()).stream()
					.filter(next -> !visitedSmall.contains(next))
					.map(this::go);
		}
		
		private Path go(String next) {
			return new Path(concat(path, next), StringUtils.isAllLowerCase(next) ? concat(visitedSmall, next) : visitedSmall);
		}
		
		static Path init() {
			return new Path(new ArrayList<>(), new HashSet<>()).go("start");
		}
	}
	
	private record Graph(Map<String, List<String>> paths) {
		static Graph parse(List<String> lines) {
			Map<String, List<String>> paths = new HashMap<>();
			for (String line : lines) {
				String[] split = line.split("-");
				String left = split[0];
				String right = split[1];
				paths.computeIfAbsent(left, k -> new ArrayList<>()).add(right);
				paths.computeIfAbsent(right, k -> new ArrayList<>()).add(left);
			}
			return new Graph(paths);
		}
	}
	
	private static <T> List<T> concat(List<T> list, T item) {
		list = new ArrayList<>(list);
		list.add(item);
		return list;
	}
	
	private static <T> Set<T> concat(Set<T> set, T item) {
		set = new HashSet<>(set);
		set.add(item);
		return set;
	}
}