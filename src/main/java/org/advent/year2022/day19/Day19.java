package org.advent.year2022.day19;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day19 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day19()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 33, 56 * 62),
				new ExpectedAnswers("input.txt", 1009, 18816)
		);
	}
	
	List<Blueprint> blueprints;
	
	@Override
	public void prepare(String file) {
		String input = String.join("\n", Utils.readLines(Utils.scanFileNearClass(getClass(), file)));
		blueprints = Stream.of(input.replace("\n  ", " ").split("\n"))
				.filter(StringUtils::isNotEmpty)
				.map(Blueprint::parse)
				.toList();
	}
	
	@Override
	public Object part1() {
		return blueprints.stream()
				.mapToInt(b -> b.id * maxGeodes(b, 24))
				.sum();
	}
	
	@Override
	public Object part2() {
		return blueprints.parallelStream()
				.limit(3)
				.mapToInt(b -> maxGeodes(b, 32))
				.reduce(1, (a, b) -> a * b);
	}
	
	int maxGeodes(Blueprint blueprint, int minutes) {
		Resources resources = new Resources(0, 0, 0, 0);
		Resources robots = new Resources(1, 0, 0, 0);
		Resources[] bestFutures = new Resources[minutes + 1];
		return new State(resources, robots).maxGeodes(blueprint, bestFutures, minutes);
	}
	
	record State(Resources resources, Resources robots) {
		
		int maxGeodes(Blueprint blueprint, Resources[] bestFutures, int time) {
			if (time == 0)
				return resources.values[3];
			
			// Кол-во ресурсов если не строить роботов
			Resources future = resources.add(robots, time);
			Resources best = bestFutures[time];
			if (best != null && best.moreValuable(future))
				// Уже было другое состояние с лучшим результатом
				return 0;
			
			int maxGeodes = future.values[3];
			
			for (int i = 0; i < blueprint.robotCosts.length; i++) {
				if (blueprint.robotsLimit.values[i] * time - resources.values[i] <= robots.values[i] * time)
					continue;
				Resources cost = blueprint.robotCosts[i];
				int robotWaitTime = cost.waitTime(resources, robots);
				if (robotWaitTime < 0)
					continue;
				int nextTime = time - robotWaitTime - 1;
				if (nextTime <= 0)
					continue;

				State nextState = new State(
						resources.add(robots, robotWaitTime + 1).sub(cost),
						robots.inc(i));
				maxGeodes = Math.max(maxGeodes, nextState.maxGeodes(blueprint, bestFutures, nextTime));
			}
			
			bestFutures[time] = future;
			return maxGeodes;
		}
	}
	
	record Resources(int[] values) {
		
		public Resources(int ore, int clay, int obsidian, int geode) {
			this(new int[] {ore, clay, obsidian, geode});
		}
		
		Resources add(Resources other, int times) {
			int[] nextResources = Arrays.copyOf(values, values.length);
			for (int i = 0; i < nextResources.length; i++)
				nextResources[i] += other.values[i] * times;
			return new Resources(nextResources);
		}
		
		Resources inc(int type) {
			int[] nextResources = Arrays.copyOf(values, values.length);
			nextResources[type]++;
			return new Resources(nextResources);
		}
		
		Resources sub(Resources other) {
			int[] nextResources = Arrays.copyOf(values, values.length);
			for (int i = 0; i < nextResources.length; i++)
				nextResources[i] -= other.values[i];
			return new Resources(nextResources);
		}
		
		Resources maxEach(Resources other) {
			int[] nextResources = new int[values.length];
			for (int i = 0; i < values.length; i++)
				nextResources[i] = Math.max(values[i], other.values[i]);
			return new Resources(nextResources);
		}
		
		int waitTime(Resources resources, Resources robots) {
			int waitTime = 0;
			for (int i = 0; i < values.length; i++) {
				int cost = values[i];
				if (cost == 0)
					continue;
				int robotsCount = robots.values[i];
				if (robotsCount == 0)
					return -1;
				waitTime = Math.max(waitTime, divRoundUp(cost - resources.values[i], robotsCount));
			}
			return waitTime;
		}
		
		int divRoundUp(int a, int b) {
			return a / b + (a % b > 0 ? 1 : 0);
		}
		
		static Pattern pattern = Pattern.compile("(\\d+) (ore|clay|obsidian)");
		static Resources parse(String line) {
			Matcher matcher = pattern.matcher(line);
			Map<String, Integer> resources = new HashMap<>();
			while (matcher.find())
				resources.put(matcher.group(2), Integer.parseInt(matcher.group(1)));
			
			return new Resources(Stream.of("ore", "clay", "obsidian", "geode")
					.mapToInt(r -> resources.getOrDefault(r, 0))
					.toArray());
		}
		
		public boolean moreValuable(Resources future) {
			// Игнорируем ore, т. к. не влияет на результат
			for (int i = 1; i < values.length; i++)
				if (values[i] < future.values[i])
					return false;
			return true;
		}
	}
	
	record Blueprint(int id, Resources[] robotCosts, Resources robotsLimit) {
		
		static Blueprint parse(String line) {
			String[] split = line.split(":");
			int id = Integer.parseInt(split[0].split(" ")[1]);
			Resources[] robotCosts = Arrays.stream(split[1].split("\\."))
					.map(r -> r.split(" robot costs ")[1])
					.map(Resources::parse)
					.toArray(Resources[]::new);
			Resources robotsLimit = Arrays.stream(robotCosts)
					.reduce(new Resources(0, 0, 0, 1000), Resources::maxEach);
			return new Blueprint(id, robotCosts, robotsLimit);
		}
	}
}