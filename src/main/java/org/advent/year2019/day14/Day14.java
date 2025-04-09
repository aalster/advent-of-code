package org.advent.year2019.day14;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day14 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day14()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 31, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 165, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 13312, 82892753),
				new ExpectedAnswers("example4.txt", 180697, 5586022),
				new ExpectedAnswers("example5.txt", 2210736, 460664),
				new ExpectedAnswers("input.txt", 202617, 7863863)
		);
	}
	
	Map<String, Recipe> recipes;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		recipes = Utils.readLines(input).stream()
				.map(Recipe::parse)
				.collect(Collectors.toMap(Recipe::resource, r -> r));
		recipes.get("FUEL").initDependencies(recipes);
	}
	
	@Override
	public Object part1() {
		return oreNeeded(1);
	}
	
	@Override
	public Object part2() {
		long oreQuantity = 1_000_000_000_000L;
		long oneFuel = oreNeeded(1);
		long minFuel = oreQuantity / oneFuel;
		long maxFuel = minFuel * 2;
		while (minFuel < maxFuel) {
			long fuel = (minFuel + maxFuel) / 2;
			long ore = oreNeeded(fuel);
			if (ore <= oreQuantity) {
				minFuel = fuel + 1;
			} else {
				maxFuel = fuel;
			}
		}
		return minFuel - 1;
	}
	
	private long oreNeeded(long fuelQuantity) {
		Map<String, Long> ingredients = recipes.get("FUEL").ingredients().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() * fuelQuantity,
						(l, r) -> {throw new RuntimeException("Duplicated key");},
						HashMap::new));
		
		while (ingredients.size() > 1) {
			for (Map.Entry<String, Long> entry : new HashSet<>(ingredients.entrySet())) {
				String resource = entry.getKey();
				if ("ORE".equals(resource))
					continue;
				if (ingredients.keySet().stream()
						.map(recipes::get)
						.filter(Objects::nonNull)
						.anyMatch(otherRecipe -> otherRecipe.dependencies.contains(resource)))
					continue;
				
				long neededQuantity = entry.getValue();
				Recipe recipe = recipes.get(resource);
				long multiplier = neededQuantity / recipe.quantity + (neededQuantity % recipe.quantity == 0 ? 0 : 1);
				ingredients.remove(resource);
				for (Map.Entry<String, Integer> recipeEntry : recipe.ingredients.entrySet())
					ingredients.compute(recipeEntry.getKey(), (k, v) -> (v == null ? 0 : v) + multiplier * recipeEntry.getValue());
			}
		}
		return ingredients.get("ORE");
	}
	
	record Recipe(String resource, int quantity, Map<String, Integer> ingredients, Set<String> dependencies) {
		
		void initDependencies(Map<String, Recipe> recipes) {
			dependencies.addAll(ingredients.keySet());
			for (String ingredient : ingredients.keySet()) {
				Recipe ingredientRecipe = recipes.get(ingredient);
				if (ingredientRecipe == null)
					continue;
				if (ingredientRecipe.dependencies.isEmpty())
					ingredientRecipe.initDependencies(recipes);
				dependencies.addAll(ingredientRecipe.dependencies);
			}
		}
		
		static Recipe parse(String line) {
			String[] split = line.split(" => ");
			Map<String, Integer> ingredients = Arrays.stream(split[0].split(", "))
					.map(ingredient -> ingredient.split(" "))
					.collect(Collectors.toMap(ingredient -> ingredient[1], ingredient -> Integer.parseInt(ingredient[0])));
			String[] resource = split[1].split(" ");
			return new Recipe(resource[1], Integer.parseInt(resource[0]), ingredients, new HashSet<>());
		}
	}
}