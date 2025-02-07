package org.advent.year2019.day6;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day6 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day6()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 42, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 4),
				new ExpectedAnswers("input.txt", 171213, 292)
		);
	}
	
	Map<String, Planet> planets;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		planets = Planet.parse(Utils.readLines(input));
	}
	
	@Override
	public Object part1() {
		return planets.values().stream().mapToInt(Planet::orbitLevel).sum();
	}
	
	@Override
	public Object part2() {
		Planet you = planets.get("YOU");
		Planet san = planets.get("SAN");
		int distance = 0;
		while (you.orbitLevel > san.orbitLevel) {
			you = planets.get(you.parent);
			distance++;
		}
		while (san.orbitLevel > you.orbitLevel) {
			san = planets.get(san.parent);
			distance++;
		}
		while (!you.name.equals(san.name)) {
			you = planets.get(you.parent);
			san = planets.get(san.parent);
			distance += 2;
		}
		return distance - 2;
	}
	
	record Planet(String name, String parent, int orbitLevel) {
		
		static Map<String, Planet> parse(List<String> lines) {
			Map<String, List<String>> orbits = new HashMap<>();
			for (String line : lines) {
				String[] split = line.split("\\)");
				orbits.computeIfAbsent(split[0], k -> new ArrayList<>()).add(split[1]);
			}
			
			List<String> current = List.of("COM");
			int level = 0;
			Map<String, Planet> planets = new HashMap<>();
			planets.put("COM", new Planet("COM", null, level));
			level++;
			
			while (!current.isEmpty()) {
				List<String> next = new ArrayList<>();
				for (String c : current) {
					List<String> children = orbits.getOrDefault(c, List.of());
					for (String child : children)
						planets.put(child, new Planet(child, c, level));
					next.addAll(children);
				}
				current = next;
				level++;
			}
			return planets;
		}
	}
}