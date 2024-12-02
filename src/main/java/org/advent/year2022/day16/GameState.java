package org.advent.year2022.day16;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
public class GameState {
	private final Set<Valve> openValves;
	private final Map<Integer, Worker> workers;
	private int releasedPressure;
	
	void incPressure() {
		int sum = openValves.stream().mapToInt(Valve::rate).sum();
		releasedPressure += sum;
	}
	
	void openValve(Valve valve) {
		openValves.add(valve);
	}
}
