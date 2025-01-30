package org.advent.year2018.day24;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day24()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 5216, 51),
				new ExpectedAnswers("input.txt", 14377, 6947)
		);
	}
	
	Army immuneSystem;
	Army infection;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		immuneSystem = Army.parse(input);
		infection = Army.parse(input);
	}
	
	@Override
	public Object part1() {
		battle(immuneSystem, infection);
		return Stream.of(immuneSystem, infection).mapToInt(Army::totalUnits).sum();
	}
	
	@Override
	public Object part2() {
		int minBoost = 0;
		int maxBoost = 10000;
		while (minBoost < maxBoost) {
			int boost = (maxBoost + minBoost) / 2;
			Army immuneSystemCopy = immuneSystem.copy(boost);
			boolean finished = battle(immuneSystemCopy, infection.copy(0));
			if (!finished || immuneSystemCopy.groups.isEmpty())
				minBoost = boost + 1;
			else
				maxBoost = boost;
		}
		Army immuneSystemCopy = immuneSystem.copy(minBoost);
		battle(immuneSystemCopy, infection.copy(0));
		return immuneSystemCopy.totalUnits();
	}
	
	boolean battle(Army immuneSystem, Army infection) {
		int prevTotalUnits = immuneSystem.totalUnits() + infection.totalUnits();
		while (true) {
			List<Attack> attacks = new ArrayList<>(immuneSystem.attacks(infection));
			attacks.addAll(infection.attacks(immuneSystem));
			attacks.sort(Comparator.comparing((Attack a) -> a.group.initiative).reversed());
			attacks.forEach(Attack::apply);
			
			for (Army army : List.of(immuneSystem, infection))
				army.groups.removeIf(group -> group.units <= 0);
			
			int immuneSystemUnits = immuneSystem.totalUnits();
			int infectionUnits = infection.totalUnits();
			if (immuneSystemUnits <= 0 || infectionUnits <= 0)
				return true;
			int totalUnits = immuneSystemUnits + infectionUnits;
			if (totalUnits == prevTotalUnits)
				return false;
			prevTotalUnits = totalUnits;
		}
	}
	
	record Attack(Group group, Group target) {
		void apply() {
			target.units = Math.max(target.units - group.damageTo(target) / target.unitHp, 0);
		}
	}
	
	@Data
	@AllArgsConstructor
	static class Group {
		static final Comparator<Group> strongestGroupComparator = Comparator.comparingInt(Group::effectivePower)
				.thenComparingInt(Group::getInitiative).reversed();
		
		final int unitHp;
		final String attackType;
		final int unitDamage;
		final int initiative;
		final Set<String> immunities;
		final Set<String> weaknesses;
		int units;
		
		Group copy(int boost) {
			return new Group(unitHp, attackType, unitDamage + boost, initiative, immunities, weaknesses, units);
		}
		
		int effectivePower() {
			return units * unitDamage;
		}
		
		int damageTo(Group target) {
			return effectivePower() * (target.weaknesses.contains(attackType) ? 2 : 1);
		}
		
		Attack attack(List<Group> enemies) {
			Group target = null;
			int maxDamage = -1;
			for (Group enemy : enemies) {
				if (enemy.immunities.contains(attackType))
					continue;
				int damage = damageTo(enemy);
				if (maxDamage < damage || (maxDamage == damage && strongestGroupComparator.compare(enemy, target) < 0)) {
					maxDamage = damage;
					target = enemy;
				}
			}
			if (target == null)
				return null;
			enemies.remove(target);
			return new Attack(this, target);
		}
		
		static final Pattern pattern = Pattern.compile("^(.+) units each with (.+) hit points" +
				"(?: \\((.+)\\))? with an attack that does (.+) (.+) damage at initiative (.+)$");
		
		static Group parse(String line) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches())
				throw new IllegalArgumentException("Invalid line: " + line);
			
			int units = Integer.parseInt(matcher.group(1));
			int unitHp = Integer.parseInt(matcher.group(2));
			int unitDamage = Integer.parseInt(matcher.group(4));
			String attackType = matcher.group(5);
			int initiative = Integer.parseInt(matcher.group(6));
			
			Set<String> immunities = new HashSet<>();
			Set<String> weaknesses = new HashSet<>();
			String group3 = matcher.group(3);
			if (group3 != null) {
				for (String part : group3.split("; ")) {
					String[] split = part.split(" to ");
					("immune".equals(split[0]) ? immunities : weaknesses).addAll(List.of(split[1].split(", ")));
				}
			}
			return new Group(unitHp, attackType, unitDamage, initiative, immunities, weaknesses, units);
		}
	}
	
	@Data
	static class Army {
		final String name;
		final List<Group> groups;
		
		Army copy(int boost) {
			return new Army(name, groups.stream().map(g -> g.copy(boost)).collect(Collectors.toList()));
		}
		
		int totalUnits() {
			return groups.stream().mapToInt(g -> g.units).sum();
		}
		
		List<Attack> attacks(Army army) {
			List<Group> enemies = new ArrayList<>(army.groups);
			return groups.stream()
					.sorted(Group.strongestGroupComparator)
					.map(g -> g.attack(enemies))
					.filter(Objects::nonNull)
					.toList();
		}
		
		static Army parse(Scanner input) {
			String name = input.nextLine().replace(":", "");
			List<Group> groups = new ArrayList<>();
			while (input.hasNext()) {
				String line = input.nextLine();
				if (line.isEmpty())
					break;
				groups.add(Group.parse(line));
			}
			return new Army(name, groups);
		}
	}
}