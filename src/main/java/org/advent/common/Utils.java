package org.advent.common;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Utils {
	
	@SneakyThrows
	public static String readFileNearClass(Class<?> type, String path) {
		String targetClassPackage = type.getResource("").getPath();
		
		String winPathFixRegex = "^/[a-zA-Z]:/.+$"; // Фиксит пути типа '/C:/some/path'
		if (targetClassPackage.matches(winPathFixRegex))
			targetClassPackage = StringUtils.substringAfter(targetClassPackage, "/");
		
		String srcClassPackage = targetClassPackage.replace("/target/classes/", "/src/main/java/");
		return Files.readString(Path.of(srcClassPackage + path));
	}
	
	@SneakyThrows
	public static Scanner scanFileNearClass(Class<?> type, String path) {
		return new Scanner(readFileNearClass(type, path));
	}
}