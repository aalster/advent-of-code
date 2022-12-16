package org.example.puzzle16;

import org.example.Utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Puzzle16 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Puzzle16.class, "input.txt");
		Pattern pattern = Pattern.compile("Valve (.+) has flow rate=(.+); tunnels? leads? to valves? (.+)");
		Map<String, Valve> valves = new LinkedHashMap<>();
		while (input.hasNext()) {
			Matcher matcher = pattern.matcher(input.nextLine());
			if (!matcher.find())
				continue;
			String name = matcher.group(1);
			int rate = Integer.parseInt(matcher.group(2));
			List<String> availableValves = List.of(matcher.group(3).split(", "));
			valves.put(name, new Valve(name, rate, availableValves));
		}
		
		PathService pathService = PathService.computeAllPaths(valves);
		for (Map.Entry<String, Integer> entry : pathService.paths().entrySet())
			System.out.println(entry.getKey() + ": " + entry.getValue());
		
		Valve start = valves.get("AA");
//		int time = 30;
//		int workers = 1;
		int time = 26;
		int workers = 2;
		
		GameEngine gameEngine = new GameEngine(valves, pathService, start);
		System.out.println(gameEngine.maxPressure(workers, time));
	}
}