package org.advent.year2015.day15;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day15 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day15()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 62842880, 57600000),
				new ExpectedAnswers("input.txt", 21367368, 1766400)
		);
	}
	
	List<Ingredient> ingredients;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		ingredients = Utils.readLines(input).stream().map(Ingredient::parse).toList();
	}
	
	@Override
	public Object part1() {
		return solve(false);
	}
	
	@Override
	public Object part2() {
		return solve(true);
	}
	
	long solve(boolean checkCalories) {
		Ingredient total = new Ingredient("Total", 0, 0, 0, 0, 0);
		return nextIngredient(total, ingredients, 100, checkCalories);
	}
	
	long nextIngredient(Ingredient total, List<Ingredient> unusedIngredients, int spoonsLeft, boolean checkCalories) {
		if (unusedIngredients.size() <= 1) {
			Ingredient completed = total.add(unusedIngredients.getFirst(), spoonsLeft);
			if (checkCalories && completed.calories != 500)
				return 0;
			return completed.score();
		}
		
		List<Ingredient> nextUnusedIngredients = new ArrayList<>(unusedIngredients);
		Ingredient ingredient = nextUnusedIngredients.removeFirst();
		
		return IntStream.range(0, spoonsLeft)
				.mapToLong(spoons -> nextIngredient(total.add(ingredient, spoons), nextUnusedIngredients, spoonsLeft - spoons, checkCalories))
				.max()
				.orElse(0);
	}
	
	record Ingredient(String name, int capacity, int durability, int flavor, int texture, int calories) {
		
		Ingredient add(Ingredient ingredient, int spoons) {
			return new Ingredient(name,
					capacity + ingredient.capacity * spoons,
					durability + ingredient.durability * spoons,
					flavor + ingredient.flavor * spoons,
					texture + ingredient.texture * spoons,
					calories + ingredient.calories * spoons);
		}
		
		long score() {
			if (capacity <= 0 || durability <= 0 || flavor <= 0 || texture <= 0)
				return 0;
			return (long) capacity * durability * flavor * texture;
		}
		
		static Pattern pattern = Pattern.compile(
				"(\\w+): capacity (-?\\d+), durability (-?\\d+), flavor (-?\\d+), texture (-?\\d+), calories (-?\\d+)");
		static Ingredient parse(String line) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches())
				throw new IllegalArgumentException("Invalid ingredient: " + line);
			String name = matcher.group(1);
			int capacity = Integer.parseInt(matcher.group(2));
			int durability = Integer.parseInt(matcher.group(3));
			int flavor = Integer.parseInt(matcher.group(4));
			int texture = Integer.parseInt(matcher.group(5));
			int calories = Integer.parseInt(matcher.group(6));
			return new Ingredient(name, capacity, durability, flavor, texture, calories);
		}
	}
}