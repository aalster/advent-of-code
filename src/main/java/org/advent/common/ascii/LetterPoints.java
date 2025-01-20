package org.advent.common.ascii;

import lombok.Data;
import org.advent.common.Point;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Data
public class LetterPoints {
	private static final boolean printUnknownLetters = true;
	private final LetterSize letterSize;
	private final Map<Set<Point>, Character> letters;
	
	public static LetterPoints load(LetterSize letterSize) {
		List<String> lines = Utils.readLines(Utils.scanFileNearClass(LetterPoints.class, "letters/" + letterSize.fileName()));
		Map<Set<Point>, Character> letters = new LinkedHashMap<>();
		char letter = 'A';
		for (List<String> letterLines : Utils.splitByEmptyLine(lines)) {
			Set<Point> points = new HashSet<>(Point.readField(letterLines).get('#'));
			letters.put(points, letter);
			letter++;
		}
		return new LetterPoints(letterSize, letters);
	}
	
	
	public String parse(Collection<Point> points) {
		return letters(points).stream().map(l -> "" + getLetter(l)).collect(Collectors.joining());
	}
	
	private Character getLetter(Set<Point> points) {
		Character letter = letters.get(points);
		if (printUnknownLetters && letter == null) {
			System.out.println("Unknown letter:");
			Point.printField(points, p -> points.contains(p) ? '#' : ' ');
		}
		return letter == null ? '?' : letter;
	}
	
	private Collection<Set<Point>> letters(Collection<Point> points) {
		return shiftToStart(points).stream()
				.collect(Collectors.groupingBy(p -> p.x() / letterSize.getWidth(), TreeMap::new,
						Collectors.mapping(p -> new Point(p.x() % letterSize.getWidth(), p.y()), Collectors.toSet())))
				.values();
		
	}
	
	private Collection<Set<Point>> lettersUsingShift(Collection<Point> points) {
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
}
