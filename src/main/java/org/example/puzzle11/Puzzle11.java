package org.example.puzzle11;

import org.apache.commons.lang3.StringUtils;
import org.example.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Puzzle11 {
//	static final int rounds = 20;
//	static final BigInteger divisor = BigInteger.valueOf(3);
	static final int rounds = 10000;
	static final BigInteger divisor = BigInteger.ONE;
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Puzzle11.class, "example.txt");
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
		
		
		for (int round = 0; round < rounds; round++) {
			if (round % 100 == 0) {
				System.out.println("Round: " + round);
				printStats(stats);
			}
			game.round(stats);
		}
		printStats(stats);
		System.out.println("Answer: " + stats.values().stream()
				.sorted(Comparator.<Integer>naturalOrder().reversed())
				.limit(2)
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
			stats.compute(monkey.id(), (id, count) -> monkey.items().size() + (count == null ? 0 : count));
			while (!monkey.items().isEmpty()) {
				BigInteger item = monkey.items().remove(0);
				item = monkey.operation().apply(item);
//				if (divisor != BigInteger.ONE)
//					item = item.divide(divisor);
				monkeys.get(monkey.target(item)).items().add(item);
			}
		}
		
		@Override
		public String toString() {
			return monkeys.values().stream().sorted(Comparator.comparing(Monkey::id)).map(Monkey::toString).collect(Collectors.joining("\n"));
		}
	}
	
	record Monkey(
			int id,
			List<BigInteger> items,
			UnaryOperator<BigInteger> operation,
			BigInteger test,
			int successTarget,
			int failureTarget
	) {
		int target(BigInteger item) {
			return item.mod(test).signum() == 0 ? successTarget : failureTarget;
		}
		
		static Monkey parse(MatchResult matchResult) {
			int id = Integer.parseInt(matchResult.group(1));
			List<BigInteger> items = Arrays.stream(matchResult.group(2).split(", ")).map(BigInteger::new).toList();
			UnaryOperator<BigInteger> operation = parseOperation(matchResult.group(3));
			BigInteger test = new BigInteger(matchResult.group(4));
			int successTarget = Integer.parseInt(matchResult.group(5));
			int failureTarget = Integer.parseInt(matchResult.group(6));
			return new Monkey(id, new ArrayList<>(items), operation, test, successTarget, failureTarget);
		}
		
		static UnaryOperator<BigInteger> parseOperation(String group) {
			String[] split = StringUtils.removeStart(group, "new = ").split(" ");
			BinaryOperator<BigInteger> operator = switch (split[1]) {
				case "+" -> BigInteger::add;
				case "-" -> BigInteger::subtract;
				case "*" -> BigInteger::multiply;
				case "/" -> BigInteger::divide;
				default -> throw new RuntimeException("Unknown operator " + split[1]);
			};
			UnaryOperator<BigInteger> left = parseOperationValue(split[0]);
			UnaryOperator<BigInteger> right = parseOperationValue(split[2]);
			return old -> operator.apply(left.apply(old), right.apply(old));
		}
		
		static UnaryOperator<BigInteger> parseOperationValue(String value) {
			if ("old".equals(value))
				return old -> old;
			BigInteger num = new BigInteger(value);
			return old -> num;
		}
	}
}