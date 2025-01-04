package org.advent.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AsciiLetters {
	
	public static String parse(String asciiLetters, char symbol) {
		return parse(Point.readField(List.of(asciiLetters.split("\n"))).get(symbol));
	}
	
	public static String parse(Collection<Point> points) {
		return letters(points).stream().map(l -> "" + letters.getOrDefault(l, '?')).collect(Collectors.joining());
	}
	
	private static List<Set<Point>> letters(Collection<Point> points) {
		points = shiftToStart(points);
		List<Set<Point>> letters = new ArrayList<>();
		while (!points.isEmpty()) {
			int x = Point.maxX(points);
			while (true) {
				int _x = x;
				if (points.stream().noneMatch(p -> p.x() == _x))
					break;
				x--;
			}
			int _x = x;
			Map<Boolean, List<Point>> split = points.stream().collect(Collectors.groupingBy(p -> _x < p.x()));
			points = split.getOrDefault(false, List.of());
			letters.addFirst(new HashSet<>(shiftToStart(split.get(true))));
		}
		return letters;
	}
	
	private static Collection<Point> shiftToStart(Collection<Point> points) {
		int minX = Point.minX(points);
		int minY = Point.minY(points);
		if (minX == 0 && minY == 0)
			return points;
		return points.stream().map(p -> p.shift(-minX, -minY)).toList();
	}
	
	private static final String lettersField = """
			 ##  ###   ##  ###  #### ####  ##  #  # #   ## #  # #    #  # #  #  ##  ###   ##  ###   ##  ### #  # #  # #  # #   # #   # ####\s
			#  # #  # #  # #  # #    #    #  # #  # #    # # #  #    #### ## # #  # #  # #  # #  # #     #  #  # #  # #  #  # #   # #     #\s
			#  # ###  #    #  # ###  ###  #    #### #    # ##   #    #### ## # #  # #  # #  # #  #  ##   #  #  # #  # #  #   #     #     # \s
			#### #  # #    #  # #    #    # ## #  # #    # # #  #    #  # # ## #  # ###  #  # ###     #  #  #  # #  # ####   #     #    #  \s
			#  # #  # #  # #  # #    #    #  # #  # # #  # # #  #    #  # # ## #  # #    # ## # #  #  #  #  #  #  ##  ####  # #    #   #   \s
			#  # ###   ##  ###  #### #     ### #  # #  ##  #  # #### #  # #  #  ##  #     ### #  #  ##   #   ##   ##  #  # #   #   #   ####\s""";
	private static final Map<Set<Point>, Character> letters = allLetters();
	
	private static Map<Set<Point>, Character> allLetters() {
		List<Point> points = Point.readField(List.of(lettersField.split("\n"))).get('#');
		Map<Set<Point>, Character> result = new LinkedHashMap<>();
		int index = 0;
		for (Set<Point> letter : letters(points)) {
			result.put(letter, (char) ('A' + index));
			index++;
		}
		return result;
	}
}
