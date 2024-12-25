package org.advent.year2015.day22;

import lombok.RequiredArgsConstructor;
import org.advent.common.Utils;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day22 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day22.class, "input.txt");
		int[] array = Utils.readLines(input).stream().map(line -> line.split(": ")[1]).mapToInt(Integer::parseInt).toArray();
		Character boss = new Character(array[0], array[1], 0);
		
		System.out.println("Answer 1: " + solve(boss, false));
		System.out.println("Answer 2: " + solve(boss, true));
	}
	
	private static long solve(Character boss, boolean hard) {
		int startingHp = 50;
		int startingMana = 500;
		
		Character player = new Character(startingHp, 0, 0);
		List<GameState> gameStates = List.of(new GameState(player, boss, startingMana, 0, 0, 0, 0));
		
		int turn = 0;
		int minUsedMana = Integer.MAX_VALUE;
		while (!gameStates.isEmpty()) {
			int _turn = turn;
			int _minUsedMana = minUsedMana;
			Map<Boolean, List<GameState>> nextStates = gameStates.stream()
					.filter(s -> s.usedMana < _minUsedMana)
					.flatMap(s -> s.difficultyEffect(hard))
					.flatMap(GameState::processEffects)
					.flatMap(s -> s.nextTurn(_turn))
					.collect(Collectors.groupingBy(s -> s.boss.hp <= 0));
			
			for (GameState finished : nextStates.getOrDefault(Boolean.TRUE, List.of()))
				minUsedMana = Math.min(minUsedMana, finished.usedMana);
			
			gameStates = nextStates.getOrDefault(Boolean.FALSE, List.of());
			turn++;
		}
		return minUsedMana == Integer.MAX_VALUE ? -1 : minUsedMana;
	}
	
	record GameState(Character player, Character boss, int mana, int usedMana, int armorDuration, int poisonDuration, int manaDuration) {
		
		Stream<GameState> difficultyEffect(boolean hard) {
			if (!hard)
				return Stream.of(this);
			
			if (player.hp <= 0)
				return Stream.empty();
			if (boss.hp <= 0)
				return Stream.of(this);
			return Stream.of(new GameState(player.hit(1), boss, mana, usedMana, armorDuration, poisonDuration, manaDuration));
		}
		
		Stream<GameState> processEffects() {
			if (player.hp <= 0)
				return Stream.empty();
			if (boss.hp <= 0)
				return Stream.of(this);
			
			Character player = this.player;
			Character boss = this.boss;
			int mana = this.mana;
			int armorDuration = this.armorDuration;
			int poisonDuration = this.poisonDuration;
			int manaDuration = this.manaDuration;
			
			if (armorDuration > 0) {
				armorDuration--;
				if (armorDuration == 0)
					player = player.buff(0, -Spell.SHIELD.armorBuff);
			}
			if (poisonDuration > 0) {
				poisonDuration--;
				boss = boss.hit(Spell.POISON.poisonDamage);
			}
			if (manaDuration > 0) {
				manaDuration--;
				mana += Spell.RECHARGE.manaBuff;
			}
			
			return Stream.of(new GameState(player, boss, mana, usedMana, armorDuration, poisonDuration, manaDuration));
		}
		
		Stream<GameState> nextTurn(int turn) {
			if (player.hp <= 0)
				return Stream.empty();
			if (boss.hp <= 0)
				return Stream.of(this);
			
			if (turn % 2 == 1)
				return Stream.of(new GameState(player.hit(boss.damage), boss, mana, usedMana, armorDuration, poisonDuration, manaDuration));
			
			return Spell.allSpells.stream().filter(s -> s.usable(this)).map(s -> s.nextState(this));
		}
	}
	
	record Character(int hp, int damage, int armor) {
		Character hit(int damageDealt) {
			if (damageDealt == 0)
				return this;
			return new Character(hp - Math.max(damageDealt - armor, 1), damage, armor);
		}
		
		Character buff(int hpBuff, int armorBuff) {
			if (hpBuff == 0 && armorBuff == 0)
				return this;
			return new Character(hp + hpBuff, damage, armor + armorBuff);
		}
	}
	
	@RequiredArgsConstructor
	enum Spell {
		MAGIC_MISSILE(53, 4, 0, 0, 0, 0, 0, 0, 0),
		DRAIN(73, 2, 2, 0, 0, 0, 0, 0, 0),
		SHIELD(113, 0, 0, 7, 6, 0, 0, 0, 0),
		POISON(173, 0, 0, 0, 0, 3, 6, 0, 0),
		RECHARGE(229, 0, 0, 0, 0, 0, 0, 101, 5);
		
		static final List<Spell> allSpells = List.of(Spell.values());
		
		final int manaCost;
		final int damage;
		final int hpBuff;
		final int armorBuff;
		final int armorDuration;
		final int poisonDamage;
		final int poisonDuration;
		final int manaBuff;
		final int manaDuration;
		
		GameState nextState(GameState state) {
			return new GameState(
					state.player.buff(hpBuff, armorBuff),
					state.boss.hit(damage),
					state.mana - manaCost,
					state.usedMana + manaCost,
					Math.max(state.armorDuration, armorDuration),
					Math.max(state.poisonDuration, poisonDuration),
					Math.max(state.manaDuration, manaDuration)
			);
		}
		
		boolean usable(GameState state) {
			if (state.mana < manaCost)
				return false;
			if (armorDuration > 0 && state.armorDuration > 0)
				return false;
			if (poisonDuration > 0 && state.poisonDuration > 0)
				return false;
			if (manaDuration > 0 && state.manaDuration > 0)
				return false;
			return true;
		}
	}
}