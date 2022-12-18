package org.advent.year2022.day7;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.IntPredicate;

public class Day7 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day7.class,"input.txt");
		File current = File.createRoot();
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.startsWith("$ cd ")) {
				current = current.cd(StringUtils.removeStart(line, "$ cd "));
			} else if (line.startsWith("$ ls")) {
				continue;
			} else {
				current.addContent(line);
			}
		}
		
		File root = current.root();
		root.printTree();
		root.countSize();
		
		List<File> largeFiles = new ArrayList<>();
		filterFiles(root, size -> size <= 100000, largeFiles);
		
		System.out.println(largeFiles);
		System.out.println("Answer 1: " + largeFiles.stream().mapToInt(File::getSize).sum());
		
		int space = 70000000;
		int requiredSpace = 30000000;
		int deletionSize = requiredSpace - space + root.getSize();
		System.out.println("root size: " + root.getSize());
		System.out.println("deletionSize: " + deletionSize);
		
		List<File> deletionCandidates = new ArrayList<>();
		filterFiles(root, size -> size >= deletionSize, deletionCandidates);
		
		int minFileSize = deletionCandidates.stream()
				.mapToInt(File::getSize)
				.min()
				.orElse(0);
		System.out.println("Answer 2: " + minFileSize);
	}
	
	static void filterFiles(File current, IntPredicate filter, List<File> largeFiles) {
		if (current.isDir() && filter.test(current.getSize()))
			largeFiles.add(current);
		for (File nested : current.getNested().values())
			if (nested.isDir())
				filterFiles(nested, filter, largeFiles);
	}
	
	@Data
	@AllArgsConstructor
	static class File {
		private final String name;
		private final File parent;
		private final boolean isDir;
		private final Map<String, File> nested = new LinkedHashMap<>();
		private int size;
		
		static File createRoot() {
			return dir("/", null);
		}
		
		static File dir(String name, File parent) {
			return new File(name, parent, true, -1);
		}
		
		static File file(String name, File parent, int size) {
			return new File(name, parent, false, size);
		}
		
		void addContent(String line) {
			String[] split = line.split(" ");
			String contentName = split[1];
			nested.put(contentName, "dir".equals(split[0])
					? dir(contentName, this)
					: file(contentName, this, Integer.parseInt(split[0])));
		}
		
		File cd(String name) {
			if ("/".equals(name))
				return root();
			if ("..".equals(name))
				return parent;
			return nested.get(name);
		}
		
		File root() {
			File current = this;
			while (current.parent != null)
				current = current.parent;
			return current;
		}
		
		void printTree() {
			printTree(0);
		}
		
		private void printTree(int indent) {
			System.out.println(" ".repeat(indent * 2) + "- " + this);
			for (File file : nested.values())
				file.printTree(indent + 1);
		}
		
		@Override
		public String toString() {
			return name + " " + (isDir ? "(dir)" : "(file, size=" + size + ")");
		}
		
		int countSize() {
			if (size >= 0)
				return size;
			return size = nested.values().stream().mapToInt(File::countSize).sum();
		}
	}
}