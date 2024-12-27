package org.advent.year2024.day23;

import org.advent.common.Utils;
import org.advent.runner.AbstractDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day23 extends AbstractDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day23()).run("input.txt");
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 7, "co,de,ka,ta"),
				new ExpectedAnswers("input.txt", 1156, "bx,cx,dr,dx,is,jg,km,kt,li,lt,nh,uf,um")
		);
	}
	
	Map<String, Set<String>> connections;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		connections = new HashMap<>();
		for (String line : Utils.readLines(input)) {
			String[] split = line.split("-");
			connections.computeIfAbsent(split[0], k -> new HashSet<>()).add(split[1]);
			connections.computeIfAbsent(split[1], k -> new HashSet<>()).add(split[0]);
		}
	}
	
	@Override
	public Object part1() {
		Set<Set<String>> triples = new HashSet<>();
		for (Map.Entry<String, Set<String>> entry : connections.entrySet())
			if (entry.getKey().startsWith("t") && entry.getValue().size() > 1)
				for (String first : entry.getValue())
					for (String second : entry.getValue())
						if (connections.get(first).contains(second))
							triples.add(Set.of(entry.getKey(), first, second));
		return triples.size();
	}
	
	@Override
	public Object part2() {
		List<Set<String>> parties = new ArrayList<>();
		for (String player : connections.keySet())
			parties.add(new HashSet<>(List.of(player)));
		
		for (Map.Entry<String, Set<String>> entry : connections.entrySet())
			for (Set<String> party : parties)
				if (entry.getValue().containsAll(party))
					party.add(entry.getKey());
		
		Set<String> maxParty = new HashSet<>(parties).stream().max(Comparator.comparing(Set::size)).orElseThrow();
		return maxParty.stream().sorted(Comparator.naturalOrder()).collect(Collectors.joining(","));
	}
}