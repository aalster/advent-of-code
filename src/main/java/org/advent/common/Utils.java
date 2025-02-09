package org.advent.common;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	public static String replaceEach(String text, String[] targets, String replacement) {
		for (String t : targets)
			text = text.replace(t, replacement);
		return text;
	}
	
	public static String removeEach(String text, String... targets) {
		return replaceEach(text, targets, "");
	}
	
	
	public static List<int[]> intPermutations(int... values) {
		List<int[]> result = new ArrayList<>(values.length * values.length);
		permutations(result, values, 0);
		return result;
	}
	
	private static void permutations(List<int[]> res, int[] arr, int idx) {
		if (idx == arr.length) {
			res.add(Arrays.copyOf(arr, arr.length));
			return;
		}
		
		for (int i = idx; i < arr.length; i++) {
			swap(arr, idx, i);
			permutations(res, arr, idx + 1);
			swap(arr, idx, i);
		}
	}
	
	private static void swap(int[] arr, int i, int j) {
		int temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}
}