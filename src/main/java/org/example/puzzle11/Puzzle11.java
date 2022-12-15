package org.example.puzzle11;

import org.apache.commons.lang3.StringUtils;
import org.example.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Puzzle11 {
//	static final int rounds = 20;
//	static final int divisor = 3;
	static final int rounds = 10000;
	static final int divisor = 1;
	static int testsMultiple = 1;
	static final Set<Integer> checks = Set.of(1, 20, 1000, 2000);
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Puzzle11.class, "input.txt");
		Pattern pattern = Pattern.compile("""
				Monkey (\\d+):
				  Starting items: (.+)
				  Operation: (.+)
				  Test: divisible by (\\d+)
				    If true: throw to monkey (\\d+)
				    If false: throw to monkey (\\d+)""");
		Map<Integer, Monkey> monkeys = input.findAll(pattern).map(Monkey::parse).collect(Collectors.toMap(Monkey::id, m -> m));
		Map<Integer, Integer> stats = new HashMap<>();
		Game game = new Game(monkeys);
		System.out.println(game);
		testsMultiple = game.testsMultiple();
		
		for (int round = 0; round < rounds; round++) {
			if (checks.contains(round)) {
				System.out.println("Round: " + round);
				System.out.println(game);
				printStats(stats);
			}
			game.round(stats);
		}
		printStats(stats);
		System.out.println("Answer 1: " + stats.values().stream()
				.sorted(Comparator.<Integer>naturalOrder().reversed())
				.limit(2)
				.mapToLong(Integer::longValue)
				.reduce((left, right) -> left * right)
				.orElse(0));
	}
	
	private static void printStats(Map<Integer, Integer> stats) {
		System.out.println(stats.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.map(entry -> "Monkey %d inspected items %d times.".formatted(entry.getKey(), entry.getValue()))
				.collect(Collectors.joining("\n")));
	}
	
	record Game(Map<Integer, Monkey> monkeys) {
		void round(Map<Integer, Integer> stats) {
			for (int i = 0; i < monkeys.size(); i++)
				turn(monkeys.get(i), stats);
		}
		
		void turn(Monkey monkey, Map<Integer, Integer> stats) {
			while (!monkey.items().isEmpty()) {
				long item = monkey.items().remove(0);
				item = monkey.operation().applyAsLong(item) / divisor % testsMultiple;
				monkeys.get(monkey.target(item)).items().add(item);
				stats.compute(monkey.id(), (id, count) -> count == null ? 1 : count + 1);
			}
		}
		
		int testsMultiple() {
			return monkeys().values().stream().mapToInt(Monkey::test).reduce((a, b) -> a * b).orElse(1);
		}
		
		@Override
		public String toString() {
			return monkeys.values().stream().sorted(Comparator.comparing(Monkey::id)).map(Monkey::toString).collect(Collectors.joining("\n"));
		}
	}
	
	record Monkey(
			int id,
			List<Long> items,
			LongUnaryOperator operation,
			int test,
			int successTarget,
			int failureTarget
	) {
		int target(long item) {
			return item % test == 0 ? successTarget : failureTarget;
		}
		
		static Monkey parse(MatchResult matchResult) {
			int id = Integer.parseInt(matchResult.group(1));
			List<Long> items = Arrays.stream(matchResult.group(2).split(", ")).mapToLong(Long::parseLong).boxed().toList();
			LongUnaryOperator operation = parseOperation(matchResult.group(3));
			int test = Integer.parseInt(matchResult.group(4));
			int successTarget = Integer.parseInt(matchResult.group(5));
			int failureTarget = Integer.parseInt(matchResult.group(6));
			return new Monkey(id, new ArrayList<>(items), operation, test, successTarget, failureTarget);
		}
		
		static LongUnaryOperator parseOperation(String group) {
			String[] split = StringUtils.removeStart(group, "new = ").split(" ");
			LongBinaryOperator operator = switch (split[1]) {
				case "+" -> (a, b) -> a + b;
				case "-" -> (a, b) -> a - b;
				case "*" -> (a, b) -> a * b;
				case "/" -> (a, b) -> a / b;
				default -> throw new RuntimeException("Unknown operator " + split[1]);
			};
			LongUnaryOperator left = parseOperationValue(split[0]);
			LongUnaryOperator right = parseOperationValue(split[2]);
			return old -> operator.applyAsLong(left.applyAsLong(old), right.applyAsLong(old));
		}
		
		static LongUnaryOperator parseOperationValue(String value) {
			return "old".equals(value) ? old -> old : old -> Long.parseLong(value);
		}
	}
}