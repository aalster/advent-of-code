package org.advent.year2022.day21;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.LongBinaryOperator;

public class Day21 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day21()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 152, 301),
				new ExpectedAnswers("input.txt", 268597611536314L, 3451534022348L)
		);
	}
	
	List<String> lines;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		lines = Utils.readLines(input);
	}
	
	@Override
	public Object part1() {
		Map<String, Monkey> monkeys = new HashMap<>();
		for (String line : lines) {
			Monkey monkey = Monkey.parse(line);
			monkeys.put(monkey.name(), monkey);
		}
		return monkeys.get("root").answer(monkeys);
	}
	
	@Override
	public Object part2() {
		Map<String, Monkey> monkeys = new HashMap<>();
		for (String line : lines) {
			if (line.startsWith("root"))
				line = StringUtils.replaceEach(line, new String[]{"+", "-", "*", "/"}, new String[]{"=", "=", "=", "="});
			Monkey monkey = Monkey.parse(line);
			monkeys.put(monkey.name(), monkey);
		}
		
		boolean continueSimplifying = true;
		while (continueSimplifying) {
			continueSimplifying = false;
			for (Monkey monkey : monkeys.values()) {
				Monkey simplified = monkey.simplify(monkeys);
				if (simplified != null) {
					continueSimplifying = true;
					monkeys.put(monkey.name(), simplified);
//					System.out.println(monkey.name() + ": " + monkey.view(monkeys) + " -> " + simplified.value());
					break;
				}
			}
		}
		
		Monkey root = monkeys.get("root");
//		System.out.println("\nRoot:\n" + root.view(monkeys));
		
		boolean continueSimplifyingRoot = true;
		while (continueSimplifyingRoot) {
			if (root.left().equals("humn") || root.right().equals("humn"))
				break;
			root = root.simplifyEquality(monkeys);
			
//			System.out.println("  => " + root.view(monkeys));
			
			continueSimplifyingRoot = false;
			Monkey simplifyLeft = monkeys.get(root.left()).simplify(monkeys);
			if (simplifyLeft != null) {
				continueSimplifyingRoot = true;
				monkeys.put(simplifyLeft.name(), simplifyLeft);
			}
			Monkey simplifyRight = monkeys.get(root.right).simplify(monkeys);
			if (simplifyRight != null) {
				continueSimplifyingRoot = true;
				monkeys.put(simplifyRight.name(), simplifyRight);
			}
//			if (continueSimplifyingRoot)
//				System.out.println("  => " + root.view(monkeys));
		}
		
//		System.out.println("\nRoot:\n" + root.view(monkeys));
		return monkeys.get(root.right()).value();
	}
	
	record Monkey(
			String name,
			String presentation,
			Long value,
			LongBinaryOperator operator,
			String left,
			String right,
			String operatorPresentation
	) {
		long answer(Map<String, Monkey> monkeys) {
			if (value != null)
				return value;
			return operator.applyAsLong(monkeys.get(left).answer(monkeys), monkeys.get(right).answer(monkeys));
		}
		
		Monkey simplify(Map<String, Monkey> monkeys) {
			if (value != null || name.equals("humn"))
				return null;
			Monkey l = monkeys.get(left);
			Monkey r = monkeys.get(right);
			if (l.value != null && !l.name.equals("humn") && r.value != null && !r.name.equals("humn"))
				return new Monkey(name, presentation, answer(monkeys), null, null, null, null);
			return null;
		}
		
		Monkey simplifyEquality(Map<String, Monkey> monkeys) {
			Monkey exp = monkeys.get(left).value == null ? monkeys.get(left) : monkeys.get(right);
			Monkey other = right.equals(exp.name) ? monkeys.get(left) : monkeys.get(right);
			
			Monkey expRight = monkeys.get(exp.right);
			
			String antiOp = switch (exp.operatorPresentation) {
				case "+" -> "-";
				case "-" -> "+";
				case "*" -> "/";
				case "/" -> "*";
				default -> throw new IllegalArgumentException();
			};
			
			// x - 2 = 4   ->   x = 4 + 2
			if (expRight.value != null && !expRight.name.equals("humn")) {
				Monkey otherSide = registerMonkey(monkeys, other.name, exp.right, antiOp);
				return new Monkey(name, "modified", null, operator, exp.left, otherSide.name, "=");
			}
			// 2 + x = 6   ->   x = 6 - 2
			if (exp.operatorPresentation.equals("+") || exp.operatorPresentation.equals("*")) {
				Monkey otherSide = registerMonkey(monkeys, other.name, exp.left, antiOp);
				return new Monkey(name, "modified", null, operator, exp.right, otherSide.name, "=");
			}
			// 6 - x = 2   ->   x = 6 - 2
			Monkey otherSide = registerMonkey(monkeys, exp.left, other.name, exp.operatorPresentation);
			return new Monkey(name, "modified", null, operator, exp.right, otherSide.name, "=");
		}
		
		private static Monkey registerMonkey(Map<String, Monkey> monkeys, String left, String right, String op) {
			Monkey otherSide = new Monkey(UUID.randomUUID().toString(), "created", null, parseOperator(op), left, right, op);
			monkeys.put(otherSide.name, otherSide);
			return otherSide;
		}
		
		String view(Map<String, Monkey> monkeys) {
			return view(monkeys, "=".equals(operatorPresentation) ? 2 : 1);
		}
		
		String view(Map<String, Monkey> monkeys, int level) {
			if (name.equals("humn"))
				return "humn";
			if (value != null)
				return "" + value;
			String expression = monkeys.get(left).view(monkeys, level - 1) + " " + operatorPresentation +
					" " + monkeys.get(right).view(monkeys, level - 1);
			return level <= 0 ? "(" + expression + ")" : expression;
		}
		
		static Monkey parse(String line) {
			String[] split = line.split(": ");
			
			String[] operands = split[1].split(" ");
			if (operands.length <= 1) {
				long value = Long.parseLong(operands[0]);
				return new Monkey(split[0], split[1], value, null, null, null, null);
			}
			
			return new Monkey(split[0], split[1], null, parseOperator(operands[1]), operands[0], operands[2], operands[1]);
		}
		
		private static LongBinaryOperator parseOperator(String operand) {
			return switch (operand) {
				case "+" -> (a, b) -> a + b;
				case "-" -> (a, b) -> a - b;
				case "*" -> (a, b) -> a * b;
				case "/" -> (a, b) -> a / b;
				case "=" -> null;
				default -> throw new IllegalArgumentException();
			};
		}
	}
}