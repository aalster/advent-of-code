package org.example.puzzle16;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
	
	public void setTarget(Valve target) {
		this.target = target;
	}
}
