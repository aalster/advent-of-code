package org.advent.year2021.day12;

import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day12 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day12.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		Graph graph = Graph.parse(lines);
		
		System.out.println("Answer 1: " + part1(graph));
		System.out.println("Answer 2: " + part2(graph));
	}
	
	private static long part1(Graph graph) {
		List<Path> paths = List.of(Path.init(graph.paths().keySet(), ""));
		return findPath(graph, paths).size();
	}
	
	private static long part2(Graph graph) {
		List<Path> paths = graph.paths().keySet().stream()
				.filter(StringUtils::isAllLowerCase)
				.filter(cave -> !cave.equals("start") && !cave.equals("end"))
				.map(cave -> Path.init(graph.paths().keySet(), cave))
				.toList();
		List<Path> finished = findPath(graph, paths);
		return finished.stream().map(Path::path).distinct().count();
	}
	
	private static List<Path> findPath(Graph graph, List<Path> paths) {
		List<Path> finished = new ArrayList<>();
		while (!paths.isEmpty()) {
			paths = paths.stream().flatMap(p -> p.step(graph)).toList();
			Map<Boolean, List<Path>> split = paths.stream().collect(Collectors.groupingBy(p -> p.path().getLast().equals("end")));
			List<Path> currentFinished = split.get(true);
			if (currentFinished != null)
				finished.addAll(currentFinished);
			paths = split.getOrDefault(false, List.of());
		}
		return finished;
	}
	
	private record Path(List<String> path, Map<String, Integer> visitLimit) {
		
		public Stream<Path> step(Graph graph) {
			return graph.paths().get(path.getLast()).stream()
					.filter(next -> visitLimit.get(next) > 0)
					.map(this::go);
		}
		
		private Path go(String next) {
			Map<String, Integer> nextVisited = new HashMap<>(visitLimit);
			nextVisited.computeIfPresent(next, (k, v) -> v - 1);
			return new Path(concat(path, next), nextVisited);
		}
		
		static Path init(Collection<String> allCaves, String visitingTwice) {
			Map<String, Integer> visitLimit = allCaves.stream().collect(Collectors.toMap(cave -> cave,
					cave -> visitingTwice.equals(cave) ? 2 : (StringUtils.isAllLowerCase(cave) ? 1 : Integer.MAX_VALUE)));
			return new Path(new ArrayList<>(), visitLimit).go("start");
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
}