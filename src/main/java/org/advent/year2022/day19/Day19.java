package org.advent.year2022.day19;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19 {
	
	static final int time = 24;
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day19.class, "example.txt");
		List<Blueprints> blueprintsList = new ArrayList<>();
		while (input.hasNext()) {
			blueprintsList.add(Blueprints.parse(input.nextLine()));
		}
		
		System.out.println("Answer 1: " + part1(blueprintsList));
		System.out.println("Answer 2: " + part2(blueprintsList));
	}
	
	private static int part1(List<Blueprints> blueprintsList) {
		return blueprintsList.stream().mapToInt(b -> runSimulation(b) * b.id()).sum();
	}
	
	private static int runSimulation(Blueprints blueprints) {
		List<Simulation> simulations = List.of(Simulation.initial());
		for (int step = 0; step < time; step++) {
			long start = System.currentTimeMillis();
			
			for (Simulation simulation : simulations)
				simulation.gatherResources();
			simulations = simulations.stream().flatMap(s -> s.buildIfPossible(blueprints)).toList();
			
			System.out.println("\nStep " + step + ". Time: " + (System.currentTimeMillis() - start) + ". Simulations: " + simulations.size());
		}
		return simulations.stream().mapToInt(s -> s.resources().get(Resource.GEODE)).max().orElse(0);
	}
	
	private static int part2(List<Blueprints> blueprintsList) {
		return 0;
	}
	
	enum Resource {
		ORE, CLAY, OBSIDIAN, GEODE
	}
	
	record Blueprints(int id, Map<Resource, Map<Resource, Integer>> prices) {
		
		Stream<Resource> availableRobots(Set<Resource> currentRobots) {
			return prices.entrySet().stream()
					.filter(e -> currentRobots.containsAll(e.getValue().keySet()))
					.map(Map.Entry::getKey);
		}
		
		Map<Resource, Integer> priceFor(Resource robot) {
			return prices.get(robot);
		}
		
		static Blueprints parse(String input) {
			String[] split = input.split(": ");
			return new Blueprints(
					parseId(split[0]),
					Arrays.stream(split[1].split("\\."))
							.map(String::trim)
							.map(Blueprints::parsePrices)
							.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
			);
		}
		
		static int parseId(String input) {
			Pattern pattern = Pattern.compile("Blueprint (\\d+)");
			Matcher matcher = pattern.matcher(input);
			if (!matcher.find())
				throw new RuntimeException("Id not found");
			return Integer.parseInt(matcher.group(1));
		}
		
		static Map.Entry<Resource, Map<Resource, Integer>> parsePrices(String input) {
			Pattern pattern = Pattern.compile("Each ([a-z]+) robot costs ([a-z0-9 ]+)");
			Matcher matcher = pattern.matcher(input);
			if (!matcher.find())
				throw new RuntimeException("Prices not found");
			return Map.entry(Resource.valueOf(matcher.group(1).toUpperCase()), parsePrice(matcher.group(2)));
		}
		
		static Map<Resource, Integer> parsePrice(String input) {
			return Arrays.stream(input.split("and"))
					.map(String::trim)
					.map(s -> s.split(" "))
					.collect(Collectors.toMap(split -> Resource.valueOf(split[1].toUpperCase()), split -> Integer.valueOf(split[0])));
		}
	}
	
	record Simulation(
			Map<Resource, Integer> robots,
			Map<Resource, Integer> resources,
			Resource targetRobot,
			List<Resource> buildingRobots
	) {
	
//		Map<Resource, Integer> robots = new HashMap<>(Arrays.stream(Resource.values()).collect(Collectors.toMap(r -> r, r -> 0)));
//		Map<Resource, Integer> resources = new HashMap<>(Arrays.stream(Resource.values()).collect(Collectors.toMap(r -> r, r -> 0)));
//		Resource targetRobot;
		
		void addRobot(Resource resource) {
			robots.compute(resource, (r, n) -> n == null ? 1 : n + 1);
		}
		
		void gatherResources() {
			robots.forEach((robot, count) -> resources.compute(robot, (r, n) -> n + count));
		}
		
		Stream<Simulation> buildIfPossible(Blueprints blueprints) {
			for (Resource built : buildingRobots)
				robots.compute(built, (r, n) -> n + 1);
			
			Map<Resource, Integer> price = blueprints.priceFor(targetRobot);
			if (!price.entrySet().stream().allMatch(e -> e.getValue() <= resources.get(e.getKey())))
				return Stream.of(this);
			
			price.forEach((key, value) -> resources.compute(key, (r, n) -> n - value));
			buildingRobots.add(targetRobot);
			
			return blueprints.availableRobots(robots.keySet()).map(this::copyForTarget);
		}
		
		Simulation copyForTarget(Resource targetRobot) {
			return new Simulation(new HashMap<>(robots), new HashMap<>(resources), targetRobot, new ArrayList<>(buildingRobots));
		}
		
		static Simulation initial() {
			Simulation initial = new Simulation(
					new HashMap<>(Arrays.stream(Resource.values()).collect(Collectors.toMap(r -> r, r -> 0))),
					new HashMap<>(new HashMap<>(Arrays.stream(Resource.values()).collect(Collectors.toMap(r -> r, r -> 0)))),
					Resource.ORE,
					new ArrayList<>());
			initial.addRobot(Resource.ORE);
			return initial;
		}
	}
}