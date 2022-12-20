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
	
	static final boolean debug = false;
	static final boolean part2Optimizations = true;
	static final int time1 = 24;
	static final int time2 = 32;
	static final int part2Limit = 3;
//	static final Random random = new Random();
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day19.class, "input.txt");
		List<Blueprints> blueprintsList = new ArrayList<>();
		while (input.hasNext()) {
			blueprintsList.add(Blueprints.parse(input.nextLine()));
		}
		
		// wrong with part2Optimizations
//		System.out.println("Answer 1: " + part1(blueprintsList, time1));
		System.out.println("Answer 2: " + part2(blueprintsList.subList(0, Math.min(part2Limit, blueprintsList.size())), time2));
	}
	
	private static int part1(List<Blueprints> blueprintsList, int time) {
		return (debug ? blueprintsList.stream() : blueprintsList.parallelStream())
				.mapToInt(b -> {
					int simulationResult = runSimulationRecursive(b, time);
					System.out.println("Simulation result for " + b.id() + ": " + simulationResult);
					return b.id() * simulationResult;
				})
				.sum();
	}
	
	private static int part2(List<Blueprints> blueprintsList, int time) {
		return (debug ? blueprintsList.stream() : blueprintsList.parallelStream())
				.mapToInt(b -> {
					int simulationResult = runSimulationRecursive(b, time);
					System.out.println("Simulation result for " + b.id() + ": " + simulationResult);
					return simulationResult;
				})
				.reduce(1, (l, r) -> l * r);
	}
	
//	private static int runSimulation(Blueprints blueprints) {
//		List<Simulation> simulations = Simulation.initial(blueprints);
//		for (int step = 0; step < time; step++) {
//			long start = System.currentTimeMillis();
//
//			for (Simulation simulation : simulations)
//				simulation.gatherResources();
//			simulations = simulations.stream()
//					.flatMap(s -> s.buildIfPossible(blueprints))
////					.sorted(Comparator.comparing(s -> - Integer.compare(s.robots.get(Resource.CLAY), s.robots.get(Resource.ORE))))
////					.limit(500_000)
//					.toList();
//
//			System.out.println("\nStep " + step + ". Time: " + (System.currentTimeMillis() - start) + ". Simulations: " + simulations.size());
//		}
//		return simulations.stream().mapToInt(s -> s.resources().get(Resources.GEODE)).max().orElse(0);
//	}
	
	private static int runSimulationRecursive(Blueprints blueprints, int time) {
		int max = 0;
		for (Simulation simulation : Simulation.initial(blueprints, time)) {
			int simulationResult = simulateRecursive(time, simulation, blueprints);
			if (max < simulationResult)
				max = simulationResult;
		}
		return max;
	}
	
	static int simulateRecursive(int remainingTime, Simulation simulation, Blueprints blueprints) {
		if (remainingTime <= 0)
			return simulation.resources().get(Resources.GEODE);
		
		if (debug) {
			System.out.println("\n== Minute " + (remainingTime) + " ==");
		}
		
		int max = 0;
		
		List<Simulation> nextSimulations = simulation.buildIfPossible(blueprints, remainingTime)
//				.sorted((s1, s2) -> random.nextInt(2) - 1).limit(1)
				.toList();
		
		for (Simulation next : nextSimulations) {
			next.gatherResources();
			int result = simulateRecursive(remainingTime - 1, next, blueprints);
			if (max < result)
				max = result;
		}
		return max;
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
		
		static String resourceName(int resource) {
			return switch (resource) {
				case ORE -> "ore";
				case CLAY -> "clay";
				case OBSIDIAN -> "obsidian";
				case GEODE -> "geode";
				default -> throw new IllegalArgumentException();
			};
		}
	}
	
	static List<Integer> debugSteps = new ArrayList<>(List.of(
			Resources.CLAY,
			Resources.CLAY,
			Resources.CLAY,
			Resources.OBSIDIAN,
			Resources.CLAY,
			Resources.OBSIDIAN,
			Resources.GEODE,
			Resources.GEODE,
			Resources.GEODE,
			Resources.GEODE,
			Resources.GEODE
	));
	
	record Blueprints(int id, Resources[] prices) {
		
		IntStream availableRobots(Resources currentRobots, int remainingTime) {
			if (debug)
				return IntStream.of(debugSteps.remove(0));
			int start = Resources.ORE;
			int end = Resources.GEODE;
			if (part2Optimizations) {
				start = remainingTime > 27 ? Resources.ORE : Resources.CLAY;
				end = remainingTime < 16 ? Resources.GEODE : Resources.OBSIDIAN;
			}
			return IntStream.rangeClosed(start, end).filter(robot -> hasRobotsForBlueprint(currentRobots, prices[robot]));
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
			if (debug) {
				for (int i = 0; i < robots.values.length; i++) {
					int robotsCount = robots.values[i];
					String name = Resources.resourceName(i);
					if (robotsCount > 0)
						System.out.println(robotsCount + " " + name + "-collecting " +
								(robotsCount > 1 ? "robots collect " : "robot collects ") +
								robotsCount + " " + name + "; you now have " + resources.values[i] + " " + name + ".");
				}
			}
		}
		
		Stream<Simulation> buildIfPossible(Blueprints blueprints, int remainingTime) {
			if (building[0] >= 0) {
				robots.add(building[0]);
				if (debug) {
					String name = Resources.resourceName(building[0]);
					System.out.println("The new " + name + "-collecting robot is ready; you now have " + robots.get(building[0]) + " of them.");
				}
			}
			building[0] = -1;
			
			Resources price = blueprints.priceFor(targetRobot);
			if (!resources.containsAll(price))
				return Stream.of(this);
			
			if (debug) {
				String priceString = "";
				for (int i = 0; i < price.values.length; i++) {
					int count = price.values[i];
					if (count > 0)
						priceString += " and " + count + " " + Resources.resourceName(i);
				}
				priceString = priceString.substring(5);
				System.out.println("Spend " + priceString + " to start building a " + Resources.resourceName(targetRobot) + "-collecting robot.");
			}
			
			resources.removeAll(price);
			building[0] = targetRobot;
			
			return blueprints.availableRobots(robots, remainingTime).mapToObj(this::copyForTarget);
		}
		
		Simulation copyForTarget(int targetRobot) {
			return new Simulation(robots.copy(), resources.copy(), targetRobot, new int[] {building[0]});
		}
		
		@Override
		public String toString() {
			return "Robots: " + Arrays.toString(robots.values) +
					", resources: " + Arrays.toString(resources.values) +
					", target: " + targetRobot +
					(building[0] >= 0 ? (", building: " + building[0]) : "");
		}
		
		static List<Simulation> initial(Blueprints blueprints, int remainingTime) {
			Simulation initial = new Simulation(new Resources(), new Resources(), -1, new int[] {-1});
			initial.robots.add(Resources.ORE, 1);
			return blueprints.availableRobots(initial.robots, remainingTime).mapToObj(initial::copyForTarget).toList();
		}
	}
}