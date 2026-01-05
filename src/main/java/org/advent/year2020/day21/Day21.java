package org.advent.year2020.day21;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day21 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day21()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 5, "mxmxvkd,sqjhc,fvjkl"),
				new ExpectedAnswers("input.txt", 2874, "gfvrr,ndkkq,jxcxh,bthjz,sgzr,mbkbn,pkkg,mjbtz")
		);
	}
	
	List<Foods> foods;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		foods = Utils.readLines(input).stream().map(Foods::parse).toList();
	}
	
	@Override
	public Object part1() {
		Set<String> allergens = new HashSet<>(allergens(foods).values());
		return foods.stream().flatMap(f -> f.ingredients().stream()).filter(i -> !allergens.contains(i)).count();
	}
	
	@Override
	public Object part2() {
		return allergens(foods).entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.map(Map.Entry::getValue)
				.collect(Collectors.joining(","));
	}
	
	Map<String, String> allergens(List<Foods> foods) {
		Map<String, Set<String>> allergenCandidates = new HashMap<>();
		for (Foods food : foods)
			for (String allergen : food.allergens)
				allergenCandidates.computeIfAbsent(allergen, k -> new HashSet<>(food.ingredients()))
						.retainAll(food.ingredients());
		
		Map<String, String> allergens = new HashMap<>();
		while (true) {
			Optional<Map.Entry<String, Set<String>>> single = allergenCandidates.entrySet().stream()
					.filter(e -> e.getValue().size() == 1).findAny();
			if (single.isEmpty())
				break;
			
			String allergen = single.get().getKey();
			String food = single.get().getValue().iterator().next();
			allergenCandidates.remove(allergen);
			
			allergenCandidates.values().forEach(s -> s.remove(food));
			allergens.put(allergen, food);
		}
		
		if (!allergenCandidates.isEmpty())
			throw new IllegalStateException("Unresolved allergens: " + allergenCandidates);
		return allergens;
	}
	
	record Foods(Set<String> ingredients, Set<String> allergens) {
		
		static Foods parse(String line) {
			String[] split = line.replace(")", "").split(" \\(contains ");
			return new Foods(Set.of(split[0].split(" ")), Set.of(split[1].split(", ")));
		}
	}
}