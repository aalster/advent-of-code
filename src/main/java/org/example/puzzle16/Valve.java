package org.example.puzzle16;

import java.util.List;
import java.util.Map;

record Valve(String name, int rate, List<String> availableValves) {
	
	public List<Valve> availableValves(Map<String, Valve> allValves) {
		return availableValves.stream().map(allValves::get).toList();
	}
}
