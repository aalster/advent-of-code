package org.advent.year2022.day16;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Worker {
	private final int id;
	private Valve position;
	private Valve target;
	private int remainingDistance;
	
	public Worker cloneWorker() {
		return new Worker(id, position, target, remainingDistance);
	}
}
