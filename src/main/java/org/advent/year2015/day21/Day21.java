package org.advent.year2015.day21;

import org.advent.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.IntStream;

public class Day21 {
	
	public static void main(String[] args) {
		Scanner shopInput = Utils.scanFileNearClass(Day21.class, "shop.txt");
		Scanner input = Utils.scanFileNearClass(Day21.class, "input.txt");
		
		Map<String, List<Weapon>> weapons = new HashMap<>();
		while (shopInput.hasNext()) {
			String type = shopInput.nextLine().split(":")[0];
			weapons.put(type, new ArrayList<>());
			while (shopInput.hasNext()) {
				String line = shopInput.nextLine();
				if (line.isEmpty())
					break;
				weapons.get(type).add(Weapon.parse(line));
			}
		}
		Character boss = Character.parse(input);
		Map<String, int[]> weaponsLimits = Map.of(
				"Weapons", new int[] {1},
				"Armor", new int[] {0, 1},
				"Rings", new int[] {0, 1, 2});
		
		System.out.println("Answer 1: " + part1(weapons, weaponsLimits, boss));
		System.out.println("Answer 2: " + part2(weapons, weaponsLimits, boss));
	}
	
	private static long part1(Map<String, List<Weapon>> weapons, Map<String, int[]> weaponsLimits, Character boss) {
		return minCostToWin(weapons, weaponsLimits, Set.of(), boss);
	}
	
	private static long part2(Map<String, List<Weapon>> weapons, Map<String, int[]> weaponsLimits, Character boss) {
		return maxCostToLoose(weapons, weaponsLimits, Set.of(), boss);
	}
	
	static int minCostToWin(Map<String, List<Weapon>> weapons, Map<String, int[]> weaponsLimits, Set<Weapon> selected, Character boss) {
		if (Character.player(selected).winsOver(boss))
			return selected.stream().mapToInt(Weapon::cost).sum();
		
		Optional<Map.Entry<String, int[]>> availableLimit = weaponsLimits.entrySet().stream()
				.filter(e -> e.getValue().length > 0)
				.findAny();
		
		if (availableLimit.isEmpty())
			return Integer.MAX_VALUE;
		
		Map.Entry<String, int[]> entry = availableLimit.get();
		
		if (entry.getValue().length > 1)
			return Arrays.stream(entry.getValue())
					.map(limit -> {
						Map<String, int[]> nextWeaponsLimits = new HashMap<>(weaponsLimits);
						nextWeaponsLimits.put(entry.getKey(), new int[] {limit});
						return minCostToWin(weapons, nextWeaponsLimits, selected, boss);
					})
					.min()
					.orElse(Integer.MAX_VALUE);
		
		int limit = entry.getValue()[0];
		if (limit == 0) {
			Map<String, int[]> skipWeaponsLimits = new HashMap<>(weaponsLimits);
			skipWeaponsLimits.remove(entry.getKey());
			return minCostToWin(weapons, skipWeaponsLimits, selected, boss);
		}
		
		Map<String, int[]> nextWeaponsLimits = new HashMap<>(weaponsLimits);
		nextWeaponsLimits.put(entry.getKey(), new int[] {limit - 1});
		
		return weapons.get(entry.getKey()).stream()
				.filter(weapon -> !selected.contains(weapon))
				.mapToInt(weapon -> {
					Set<Weapon> nextSelected = new HashSet<>(selected);
					nextSelected.add(weapon);
					return minCostToWin(weapons, nextWeaponsLimits, nextSelected, boss);
				})
				.min()
				.orElse(Integer.MAX_VALUE);
	}
	
	static int maxCostToLoose(Map<String, List<Weapon>> weapons, Map<String, int[]> weaponsLimits, Set<Weapon> selected, Character boss) {
		if (Character.player(selected).winsOver(boss))
			return 0;
		
		Optional<Map.Entry<String, int[]>> availableLimit = weaponsLimits.entrySet().stream()
				.filter(e -> e.getValue().length > 0)
				.findAny();
		
		if (availableLimit.isEmpty())
			return selected.stream().mapToInt(Weapon::cost).sum();
		
		Map.Entry<String, int[]> entry = availableLimit.get();
		
		if (entry.getValue().length > 1)
			return Arrays.stream(entry.getValue())
					.map(limit -> {
						Map<String, int[]> nextWeaponsLimits = new HashMap<>(weaponsLimits);
						nextWeaponsLimits.put(entry.getKey(), new int[] {limit});
						return maxCostToLoose(weapons, nextWeaponsLimits, selected, boss);
					})
					.max()
					.orElse(0);
		
		int limit = entry.getValue()[0];
		if (limit == 0) {
			Map<String, int[]> skipWeaponsLimits = new HashMap<>(weaponsLimits);
			skipWeaponsLimits.remove(entry.getKey());
			return maxCostToLoose(weapons, skipWeaponsLimits, selected, boss);
		}
		
		Map<String, int[]> nextWeaponsLimits = new HashMap<>(weaponsLimits);
		nextWeaponsLimits.put(entry.getKey(), new int[] {limit - 1});
		
		return weapons.get(entry.getKey()).stream()
				.filter(weapon -> !selected.contains(weapon))
				.mapToInt(weapon -> {
					Set<Weapon> nextSelected = new HashSet<>(selected);
					nextSelected.add(weapon);
					return maxCostToLoose(weapons, nextWeaponsLimits, nextSelected, boss);
				})
				.max()
				.orElse(0);
	}
	
	record Character(int hp, int damage, int armor) {
		
		boolean winsOver(Character boss) {
			int myHp = hp;
			int otherHp = boss.hp;
			while (true) {
				otherHp -= hit(boss);
				if (otherHp <= 0)
					return true;
				
				myHp -= boss.hit(this);
				if (myHp <= 0)
					return false;
			}
		}
		
		int hit(Character target) {
			return Math.max(1, damage - target.armor);
		}
		
		static Character player(Collection<Weapon> weapons) {
			int damage = 0;
			int armor = 0;
			for (Weapon weapon : weapons) {
				damage += weapon.damage;
				armor += weapon.armor;
			}
			return new Character(100, damage, armor);
		}
		
		static Character parse(Scanner input) {
			int[] values = IntStream.range(0, 3).map(i -> Integer.parseInt(input.nextLine().split(": ")[1])).toArray();
			return new Character(values[0], values[1], values[2]);
		}
	}
	
	record Weapon(String name, int cost, int damage, int armor) {
		
		static Weapon parse(String line) {
			String[] split = Arrays.stream(line.split(" {2}"))
					.map(String::trim)
					.filter(StringUtils::isNotEmpty)
					.toArray(String[]::new);
			return new Weapon(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
		}
	}
}