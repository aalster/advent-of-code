package org.advent.runner;

import org.apache.commons.lang3.StringUtils;

public class Timer {
	private long start = System.currentTimeMillis();
	
	public long step() {
		long current = System.currentTimeMillis();
		long diff = current - start;
		start = current;
		return diff;
	}
	
	public String stepFormatted() {
		long step = step();
		if (step < 500)
			return step + "ms";
		if (step < 2000)
			return OutputUtils.yellow(step + "ms");
		return OutputUtils.red(step + "ms");
	}
	
	public String stepFormatted(int width) {
		return StringUtils.leftPad(stepFormatted(), width, ' ');
	}
}
