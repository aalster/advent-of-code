package org.advent.runner;

public class Timer {
	private long start = System.currentTimeMillis();
	
	public long time() {
		long current = System.currentTimeMillis();
		long diff = current - start;
		start = current;
		return diff;
	}
	
	public String stepFormatted() {
		return stepFormatted(0);
	}
	
	public String stepFormatted(int width) {
		long time = time();
		String text = time + "ms";
		return pad(colored(time, text), width - text.length());
	}
	
	private String colored(long time, String text) {
		if (time < 500)
			return text;
		if (time < 2000)
			return OutputUtils.yellow(text);
		return OutputUtils.red(text);
	}
	
	private String pad(String text, int padding) {
		if (padding <= 0)
			return text;
		return " ".repeat(padding) + text;
	}
}
