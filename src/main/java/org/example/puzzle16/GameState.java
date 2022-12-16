package org.example.puzzle16;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
public class GameState {
	private final Set<Valve> openValves;
	private final Map<Integer, Worker> workers;
	private int releasedPressure;
	
//	private final List<String> history;
	
	public void incPressure(int remainingTime) {
//		history.add("\nTime: " + remainingTime);
		int sum = openValves.stream().mapToInt(Valve::rate).sum();
//		history.add("Pressure: " + releasedPressure + " + " + sum + " = " + (releasedPressure + sum));
		releasedPressure += sum;
	}
	
	public void openValve(Valve valve, int workerId) {
		openValves.add(valve);
//		history.add("Valve opened: " + valve + " by " + workerId);
	}
}
