package org.advent.year2022.day16;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day16 extends AdventDay {

	public static void main(String[] args) {
		new DayRunner(new Day16()).runAll();
	}

	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 1651, 1707),
				new ExpectedAnswers("input.txt", 2077, 2741)
		);
	}

	int[] rates;
	int[][] distances;

	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		Map<String, Valve> valves = Utils.readLines(input).stream()
				.map(Valve::parse)
				.collect(Collectors.toMap(Valve::name, v -> v));

		List<Valve> useful = valves.values().stream()
				.filter(v -> v.name.equals("AA") || v.rate > 0)
				.sorted(Comparator.comparing(Valve::name))
				.toList();

		Map<String, Integer> index = new HashMap<>();
		int pos = 0;
		for (Valve valve : useful)
			index.put(valve.name, pos++);

		rates = useful.stream().mapToInt(Valve::rate).toArray();
		distances = distances(index, valves);
	}
	
	private int[][] distances(Map<String, Integer> index, Map<String, Valve> valves) {
		int[][] distances = new int[index.size()][index.size()];
		for (Map.Entry<String, Integer> from : index.entrySet()) {
			Map<String, Integer> bfs = bfs(from.getKey(), valves);
			for (Map.Entry<String, Integer> to : index.entrySet())
				distances[from.getValue()][to.getValue()] = bfs.getOrDefault(to.getKey(), Integer.MAX_VALUE / 4);
		}
		return distances;
	}
	
	static Map<String, Integer> bfs(String start, Map<String, Valve> valves) {
		Map<String, Integer> distances = new HashMap<>();
		distances.put(start, 0);
		Deque<String> queue = new ArrayDeque<>();
		queue.add(start);
		while (!queue.isEmpty()) {
			String cur = queue.poll();
			int d = distances.get(cur);
			for (String next : valves.get(cur).availableValves) {
				if (distances.putIfAbsent(next, d + 1) == null)
					queue.add(next);
			}
		}
		return distances;
	}

	@Override
	public Object part1() {
		return IntStream.of(dfs(30)).max().orElse(0);
	}

	@Override
	public Object part2() {
		int[] bestForMask = dfs(26);

		int full = (1 << rates.length) - 1;
		int best = 0;
		for (int a = 0; a <= full; a++) {
			int aScore = bestForMask[a];
			if (aScore == 0 && a != 0)
				continue;
			
			int complement = full ^ a;
			int b = complement;
			while (true) {
				best = Math.max(best, aScore + bestForMask[b]);
				if (b == 0)
					break;
				b = (b - 1) & complement;
			}
		}
		return best;
	}

	int[] dfs(int timeLeft) {
		int[] bestForMask = new int[1 << rates.length];
		return dfs(bestForMask, 0, timeLeft, 0, 0);
	}

	int[] dfs(int[] bestForMask, int pos, int timeLeft, int mask, int released) {
		bestForMask[mask] = Math.max(bestForMask[mask], released);
		
		for (int next = 0; next < rates.length; next++) {
			int bit = 1 << next;
			if ((mask & bit) != 0)
				continue;
			int newTime = timeLeft - distances[pos][next] - 1;
			if (newTime <= 0)
				continue;
			dfs(bestForMask, next, newTime, mask | bit, released + rates[next] * newTime);
		}
		return bestForMask;
	}

	record Valve(String name, int rate, List<String> availableValves) {
		static final Pattern pattern = Pattern.compile("Valve (.+) has flow rate=(.+); tunnels? leads? to valves? (.+)");

		static Valve parse(String line) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.find())
				throw new IllegalArgumentException("Invalid line: " + line);
			return new Valve(matcher.group(1),
					Integer.parseInt(matcher.group(2)),
					List.of(matcher.group(3).split(", ")));
		}
	}
}
