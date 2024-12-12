package org.advent.year2024.day9;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day9 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day9.class, "input.txt");
		String line = input.nextLine();
		
		System.out.println("Answer 1: " + part1(line));
		System.out.println("Answer 2: " + part2(line));
	}
	
	private static long part1(String line) {
		int[] numbers = line.chars().map(c -> c - '0').toArray();
		ChecksumCounter counter = new ChecksumCounter();
		
		int endFileIndex = numbers.length - 1;
		if (endFileIndex % 2 == 1)
			endFileIndex--;
		
		for (int i = 0; i <= endFileIndex; i++) {
			int currentSize = numbers[i];
			if (currentSize == 0)
				continue;
			
			if (i % 2 == 0) {
				counter.transfer(i / 2, currentSize);
				continue;
			}
			
			int endSize = numbers[endFileIndex];
			int transferSize = Math.min(endSize, currentSize);
			counter.transfer(endFileIndex / 2, transferSize);
			
			if (endSize <= currentSize)
				endFileIndex -= 2;
			else
				numbers[endFileIndex] -= transferSize;
			numbers[i] -= transferSize;
			i--;
		}
		return counter.checksum;
	}
	
	private static long part2(String line) {
		int[] numbers = line.chars().map(c -> c - '0').toArray();
		Object[] spaces = new Object[numbers.length];
		for (int i = 0; i < numbers.length; i++)
			spaces[i] = i % 2 == 0 ? new File(i, numbers[i]) : new EmptySpace(numbers[i], new ArrayList<>());
		
		for (int i = spaces.length - 1; i >= 0; i--) {
			if (i % 2 == 1)
				continue;
			File file = (File) spaces[i];
			for (int k = 1; k < i; k += 2) {
				EmptySpace empty = (EmptySpace) spaces[k];
				if (file.size <= empty.remainingSize) {
					spaces[k] = empty.addFile(file);
					spaces[i] = new EmptySpace(file.size, new ArrayList<>());
					break;
				}
			}
		}
		ChecksumCounter counter = new ChecksumCounter();
		for (Object space : spaces) {
			if (space instanceof File f)
				counter.transfer(f);
			else if (space instanceof EmptySpace(int remainingSize, List<File> movedFiles)) {
				for (File movedFile : movedFiles)
					counter.transfer(movedFile);
				counter.skip(remainingSize);
			}
		}
		return counter.checksum;
	}
	
	static class ChecksumCounter {
		long checksum = 0;
		long index = 0;
		
		void transfer(long fileNumber) {
			checksum += fileNumber * index;
			index++;
		}
		
		void transfer(long fileNumber, int size) {
			while (size > 0) {
				transfer(fileNumber);
				size--;
			}
		}
		
		void transfer(File f) {
			transfer(f.index / 2, f.size);
		}
		
		void skip(int size) {
			index += size;
		}
	}
	
	record File (int index, int size) {
	}
	record EmptySpace(int remainingSize, List<File> movedFiles) {
		EmptySpace addFile(File f) {
			List<File> nextFiles = new ArrayList<>(movedFiles);
			nextFiles.add(f);
			return new EmptySpace(remainingSize - f.size, nextFiles);
		}
	}
}