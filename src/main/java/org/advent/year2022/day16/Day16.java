package org.advent.year2022.day16;

import org.advent.common.Utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day16 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day16.class, "input.txt");
		Pattern pattern = Pattern.compile("Valve (.+) has flow rate=(.+); tunnels? leads? to valves? (.+)");
		Map<String, Valve> valves = new LinkedHashMap<>();
		while (input.hasNext()) {
			Matcher matcher = pattern.matcher(input.nextLine());
			if (matcher.find()) {
				Valve valve = Valve.parse(matcher);
				valves.put(valve.name(), valve);
			}
		}
		
		PathService pathService = PathService.computeAllPaths(valves);

		System.out.println("Answer 1: " + solve(valves, pathService, 30, 1));
		System.out.println("Answer 2: " + solve(valves, pathService, 26, 2));
	}
	
	private static int solve(Map<String, Valve> valves, PathService pathService, int time, int workers) {
		Valve start = valves.get("AA");
		return new GameEngine(valves, pathService, start).maxPressure(workers, time);
	}
}