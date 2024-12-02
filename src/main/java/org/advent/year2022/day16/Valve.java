package org.advent.year2022.day16;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

record Valve(String name, int rate, List<String> availableValves) {
	
	List<Valve> availableValves(Map<String, Valve> allValves) {
		return availableValves.stream().map(allValves::get).toList();
	}
	
	static Valve parse(Matcher matcher) {
		String name = matcher.group(1);
		int rate = Integer.parseInt(matcher.group(2));
		List<String> availableValves = List.of(matcher.group(3).split(", "));
		return new Valve(name, rate, availableValves);
	}
}
