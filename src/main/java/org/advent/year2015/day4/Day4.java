package org.advent.year2015.day4;

import lombok.SneakyThrows;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Scanner;

public class Day4 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day4()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 609043, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 1048970, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 117946, 3938038)
		);
	}
	
	String line;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		line = input.nextLine();
	}
	
	@Override
	public Object part1() {
		return solve("00000");
	}
	
	@Override
	public Object part2() {
		return solve("000000");
	}
	
	@SneakyThrows
	long solve(String prefix) {
		int n = 0;
		MessageDigest md = MessageDigest.getInstance("MD5");
		HexFormat hex = HexFormat.of();
		while (true) {
			if (hex.formatHex(md.digest((line + n).getBytes())).startsWith(prefix))
				return n;
			n++;
		}
	}
}