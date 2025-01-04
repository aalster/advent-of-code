package org.advent.year2016.day5;

import lombok.SneakyThrows;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Day5 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day5()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "18f47a30", "05ace8e3"),
				new ExpectedAnswers("input.txt", "f97c354d", "863dde27")
		);
	}
	
	String line;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		line = Utils.readLines(input).getFirst();
	}
	
	@SneakyThrows
	@Override
	public Object part1() {
		return processGoodHashes(line, goodHashes -> {
			StringBuilder result = new StringBuilder();
			while (result.length() < 8)
				result.append(goodHashes.take().charAt(5));
			return result.toString();
		});
	}
	
	@SneakyThrows
	@Override
	public Object part2() {
		return processGoodHashes(line, goodHashes -> {
			char[] result = "________".toCharArray();
			int charsRead = 0;
			while (charsRead < result.length) {
				String hash = goodHashes.take();
				int position = hash.charAt(5) - '0';
				if (0 <= position && position < 8 && result[position] == '_') {
					result[position] = hash.charAt(6);
					charsRead++;
				}
			}
			return new String(result);
		});
	}
	
	String processGoodHashes(String line, HashesConsumer handler) {
		int threads = Runtime.getRuntime().availableProcessors();
		try (ExecutorService executor = Executors.newFixedThreadPool(threads)) {
			AtomicInteger index = new AtomicInteger(0);
			LinkedBlockingQueue<String> goodHashes = new LinkedBlockingQueue<>();
			for (int i = 0; i < threads; i++)
				executor.submit(GoodHashesProducer.create(goodHashes, line, index));
			
			try {
				String result = handler.apply(goodHashes);
				executor.shutdownNow();
				return result;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@FunctionalInterface
	interface HashesConsumer {
		String apply(LinkedBlockingQueue<String> goodHashes) throws InterruptedException;
	}

	record GoodHashesProducer(MessageDigest digest, HexFormat hex, Queue<String> goodHashes, String line, AtomicInteger index) implements Runnable {

		@SneakyThrows
		static GoodHashesProducer create(Queue<String> queue, String line, AtomicInteger index) {
			return new GoodHashesProducer(MessageDigest.getInstance("MD5"), HexFormat.of(), queue, line, index);
		}

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				String hash = hex.formatHex(digest.digest((line + index.incrementAndGet()).getBytes()));
				if (hash.startsWith("00000"))
					goodHashes.add(hash);
			}
		}
	}
}