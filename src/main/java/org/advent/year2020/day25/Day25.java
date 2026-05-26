package org.advent.year2020.day25;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day25 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day25()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 14897079, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 8329514, ExpectedAnswers.IGNORE)
		);
	}
	
	int cardPublicKey;
	int doorPublicKey;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		cardPublicKey = input.nextInt();
		doorPublicKey = input.nextInt();
	}
	
	@Override
	public Object part1() {
		long handshakeLeft = transform(doorPublicKey, findLoopNumber(cardPublicKey));
		long handshakeRight = transform(cardPublicKey, findLoopNumber(doorPublicKey));
		if (handshakeLeft != handshakeRight)
			throw new IllegalStateException("Handshake failed");
		return handshakeLeft;
	}
	
	@Override
	public Object part2() {
		return null;
	}
	
	final int subjectNumber = 7;
	final int mod = 20201227;
	
	int findLoopNumber(int publicKey) {
		int loopNumber = 0;
		long value = 1;
		while (value != publicKey && loopNumber < 10000000) {
			value = value * subjectNumber % mod;
			loopNumber++;
		}
		return loopNumber;
	}
	
	long transform(int subjectNumber, int loopNumber) {
		long value = 1;
		while (loopNumber-- > 0)
			value = value * subjectNumber % mod;
		return value;
	}
}