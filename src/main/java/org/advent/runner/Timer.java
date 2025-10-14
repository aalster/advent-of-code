package org.advent.runner;

public class Timer {
	public static final long TIME_WARNING = 500;
	public static final long TIME_ERROR = 2000;
	
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
		return formatTime(time(), width);
	}
	
	public static String formatTime(long time, int width) {
		String text = time + "ms";
		return pad(colored(time, text), width - text.length());
	}
	
	private static String colored(long time, String text) {
		if (time < TIME_WARNING)
			return text;
		if (time < TIME_ERROR)
			return OutputUtils.yellow(text);
		return OutputUtils.red(text);
	}
	
	private static String pad(String text, int padding) {
		if (padding <= 0)
			return text;
		return " ".repeat(padding) + text;
	}
}
