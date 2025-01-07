package org.advent.year2016.day14;

import lombok.SneakyThrows;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.io.Closeable;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Day14 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day14()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 22728, 22551),
				new ExpectedAnswers("input.txt", 25427, 22045)
		);
	}
	
	String salt;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		salt = input.nextLine();
	}
	
	@Override
	public Object part1() {
		return solve(salt, 0);
	}
	
	@Override
	public Object part2() {
		return solve(salt, 2016);
	}
	
	private int solve(String salt, int stretches) {
		try (Md5Hashes hashes = Md5Hashes.start(salt, stretches)) {
			int index = -1;
			int keysLeft = 64;
			while (keysLeft > 0) {
				keysLeft--;
				
				keySearch: while (true) {
					index++;
					char tripletChar = findTripletChar(hashes.hash(index));
					if (tripletChar > 0) {
						String repeats = ("" + tripletChar).repeat(5);
						for (int repeatsIndex = 1; repeatsIndex <= 1000; repeatsIndex++)
							if (hashes.hash(index + repeatsIndex).contains(repeats))
								break keySearch;
					}
				}
			}
			return index;
		}
	}
	
	char findTripletChar(String text) {
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length - 2; i++)
			if (chars[i] == chars[i + 1] && chars[i] == chars[i + 2])
				return chars[i];
		return 0;
	}
	
	record Md5Hashes(ExecutorService executor, Map<Integer, String> hashes) implements Closeable {
		
		static Md5Hashes start(String salt, int stretches) {
			Map<Integer, String> hashes = new ConcurrentHashMap<>();
			int threads = Runtime.getRuntime().availableProcessors();
			ExecutorService executor = Executors.newFixedThreadPool(threads);
				AtomicInteger index = new AtomicInteger(0);
				for (int i = 0; i < threads; i++)
					executor.submit(HashesProducer.create(hashes, salt, stretches, index));
			
			return new Md5Hashes(executor, hashes);
		}
		
		@Override
		public void close() {
			executor.shutdownNow();
		}
		
		@SneakyThrows
		String hash(int index) {
			String result = hashes.get(index);
			while (result == null) {
				Thread.sleep(20);
				result = hashes.get(index);
			}
			return result;
		}
	}
	
	record HashesProducer(MessageDigest digest, HexFormat hex, Map<Integer, String> hashes,
	                      String salt, int stretches, AtomicInteger index) implements Runnable {
		
		@SneakyThrows
		static HashesProducer create(Map<Integer, String> hashes, String salt, int stretches, AtomicInteger index) {
			return new HashesProducer(MessageDigest.getInstance("MD5"), HexFormat.of(), hashes, salt, stretches, index);
		}
		
		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				int currentIndex = index.getAndIncrement();
				
				String result = hex.formatHex(digest.digest((salt + currentIndex).getBytes()));
				int stretchesLeft = stretches;
				while (stretchesLeft-- > 0)
					result = hex.formatHex(digest.digest(result.getBytes()));
				hashes.put(currentIndex, result);
			}
		}
	}
}