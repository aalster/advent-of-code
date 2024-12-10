package org.advent.year2015.day4;

import org.advent.common.Utils;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Scanner;

public class Day4 {
	
	public static void main(String[] args) throws Exception {
		Scanner input = Utils.scanFileNearClass(Day4.class, "input.txt");
		String line = input.nextLine();
		
		System.out.println("Answer 1: " + solve(line, "00000"));
		System.out.println("Answer 2: " + solve(line, "000000"));
	}
	
	private static long solve(String line, String prefix) throws Exception {
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