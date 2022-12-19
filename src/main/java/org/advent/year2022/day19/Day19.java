package org.advent.year2022.day19;

import org.advent.common.Pair;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day19 {
	
	static final int time = 20;
	
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
		int sum = 0;
		for (Blueprints blueprints : blueprintsList) {
			int simulationResult = runSimulation(blueprints);
			System.out.println("Simulation result for " + blueprints.id() + ": " + simulationResult);
			sum += simulationResult * blueprints.id();
		}
		return sum;
	}
	
	private static int runSimulation(Blueprints blueprints) {
		List<Simulation> simulations = List.of(Simulation.initial());
		for (int step = 0; step < time; step++) {
			long start = System.currentTimeMillis();
			
			for (Simulation simulation : simulations)
				simulation.gatherResources();
			simulations = simulations.stream()
					.flatMap(s -> s.buildIfPossible(blueprints))
//					.sorted(Comparator.comparing(s -> - Integer.compare(s.robots.get(Resource.CLAY), s.robots.get(Resource.ORE))))
//					.limit(500_000)
					.toList();
			
			System.out.println("\nStep " + step + ". Time: " + (System.currentTimeMillis() - start) + ". Simulations: " + simulations.size());
		}
		return simulations.stream().mapToInt(s -> s.resources().get(Resources.GEODE)).max().orElse(0);
	}
	
	private static int part2(List<Blueprints> blueprintsList) {
		return 0;
	}
	
	static class Resources {
		static final int COUNT = 4;
		static final int ORE = 0;
		static final int CLAY = 1;
		static final int OBSIDIAN = 2;
		static final int GEODE = 3;
		
		final int[] values;
		
		Resources() {
			this(new int[COUNT]);
		}
		
		Resources(int[] values) {
			this.values = Arrays.copyOf(values, COUNT);
		}
		
		int get(int resource) {
			return values[resource];
		}
		
		void add(int resource) {
			values[resource]++;
		}
		
		void add(int resource, int count) {
			values[resource] += count;
		}
		
		void addAll(Resources resources) {
			for (int i = 0; i < COUNT; i++)
				values[i] += resources.values[i];
		}
		
		void removeAll(Resources resources) {
			for (int i = 0; i < COUNT; i++)
				values[i] -= resources.values[i];
		}
		
		boolean containsAll(Resources resources) {
			for (int i = 0; i < COUNT; i++)
				if (values[i] < resources.values[i])
					return false;
			return true;
		}
		
		Resources copy() {
			return new Resources(Arrays.copyOf(values, COUNT));
		}
		
		static int parseResource(String value) {
			return switch (value.toUpperCase()) {
				case "ORE" -> ORE;
				case "CLAY" -> CLAY;
				case "OBSIDIAN" -> OBSIDIAN;
				case "GEODE" -> GEODE;
				default -> throw new IllegalArgumentException();
			};
		}
	}
	
	record Blueprints(int id, Resources[] prices) {
		
		IntStream availableRobots(Resources currentRobots) {
			return IntStream.range(0, Resources.COUNT)
					.filter(robot -> hasRobotsForBlueprint(currentRobots, prices[robot]));
		}
		
		static boolean hasRobotsForBlueprint(Resources currentRobots, Resources price) {
			for (int i = 0; i < Resources.COUNT; i++)
				if (price.get(i) > 0 && currentRobots.get(i) == 0)
					return false;
			return true;
		}
		
		Resources priceFor(int robot) {
			return prices[robot];
		}
		
		static Blueprints parse(String input) {
			String[] split = input.split(": ");
			int id = parseId(split[0]);
			Resources[] prices = new Resources[Resources.COUNT];
			for (String s : split[1].split("\\.")) {
				String trim = s.trim();
				Pair<Integer, Resources> pair = parsePrices(trim);
				prices[pair.left()] = pair.right();
			}
			return new Blueprints(id, prices);
		}
		
		static int parseId(String input) {
			Pattern pattern = Pattern.compile("Blueprint (\\d+)");
			Matcher matcher = pattern.matcher(input);
			if (!matcher.find())
				throw new RuntimeException("Id not found");
			return Integer.parseInt(matcher.group(1));
		}
		
		static Pair<Integer, Resources> parsePrices(String input) {
			Pattern pattern = Pattern.compile("Each ([a-z]+) robot costs ([a-z0-9 ]+)");
			Matcher matcher = pattern.matcher(input);
			if (!matcher.find())
				throw new RuntimeException("Prices not found");
			return Pair.of(Resources.parseResource(matcher.group(1)), parsePrice(matcher.group(2)));
		}
		
		static Resources parsePrice(String input) {
			Resources resources = new Resources();
			for (String oneResource : input.split("and")) {
				String[] split = oneResource.trim().split(" ");
				resources.add(Resources.parseResource(split[1]), Integer.parseInt(split[0]));
			}
			return resources;
		}
	}
	
	record Simulation(
			Resources robots,
			Resources resources,
			int targetRobot,
			int[] building
	) {
		
		void gatherResources() {
			resources.addAll(robots);
		}
		
		Stream<Simulation> buildIfPossible(Blueprints blueprints) {
			if (building[0] >= 0)
				robots.add(building[0]);
			building[0] = -1;
			
			Resources price = blueprints.priceFor(targetRobot);
			if (!resources.containsAll(price))
				return Stream.of(this);
			
			resources.removeAll(price);
			building[0] = targetRobot;
			
			return blueprints.availableRobots(robots).mapToObj(this::copyForTarget);
		}
		
		Simulation copyForTarget(int targetRobot) {
			return new Simulation(robots.copy(), resources.copy(), targetRobot, new int[] {building[0]});
		}
		
		static Simulation initial() {
			Simulation initial = new Simulation(new Resources(), new Resources(), Resources.ORE, new int[] {-1});
			initial.robots.add(Resources.ORE, 1);
			return initial;
		}
	}
}