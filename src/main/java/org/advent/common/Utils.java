package org.advent.common;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

public class Utils {
	
	@SneakyThrows
	private static Path fileNearClass(Class<?> type, String path) {
		String targetClassPackage = Objects.requireNonNull(type.getResource("")).getPath();
		
		String winPathFixRegex = "^/[a-zA-Z]:/.+$"; // Фиксит пути типа '/C:/some/path'
		if (targetClassPackage.matches(winPathFixRegex))
			targetClassPackage = StringUtils.substringAfter(targetClassPackage, "/");
		
		String srcClassPackage = targetClassPackage.replace("/target/classes/", "/src/main/java/");
		return Path.of(srcClassPackage + path);
	}
	
	@SneakyThrows
	public static Scanner scanFileNearClass(Class<?> type, String path) {
		return new Scanner(fileNearClass(type, path));
	}
	
	public static List<String> readLines(Scanner input) {
		List<String> lines = new ArrayList<>();
		while (input.hasNext())
			lines.add(input.nextLine());
		return lines;
	}
	
	public static List<List<String>> splitByEmptyLine(List<String> lines) {
		lines = new ArrayList<>(lines);
		List<List<String>> result = new ArrayList<>();
		while (!lines.isEmpty()) {
			int index = lines.indexOf("");
			if (index >= 0) {
				List<String> subList = lines.subList(0, index);
				if (!subList.isEmpty())
					result.add(subList);
				lines = lines.subList(index + 1, lines.size());
			} else if (!lines.isEmpty()) {
				result.add(lines);
				lines = List.of();
			}
		}
		return result;
	}
	
	public static <T> Set<T> combineToSet(Collection<T> collection, Collection<T> other) {
		Set<T> result = new HashSet<>(collection);
		result.addAll(other);
		return result;
	}
	
	public static <T> Set<T> combineToSet(Collection<T> collection, T... other) {
		Set<T> result = new HashSet<>(collection);
		result.addAll(List.of(other));
		return result;
	}
}