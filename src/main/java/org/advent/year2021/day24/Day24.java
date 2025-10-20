package org.advent.year2021.day24;

import lombok.Data;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("DeconstructionCanBeUsed")
public class Day24 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day24()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", ExpectedAnswers.IGNORE, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", "99995969919326", "48111514719111")
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
		return solve(false);
	}
	
	@Override
	public Object part2() {
		return solve(true);
	}
	
	String solve(boolean min) {
		State state = process(lines);
		Value z = state.z.simplifyForZeroResult();
		
		int[][] possibleDigitsDesc = filterDigits(z);
		int[][] possibleDigitsAsc = Arrays.stream(possibleDigitsDesc)
				.map(digits -> Arrays.stream(digits).sorted().toArray())
				.toArray(int[][]::new);
		
		for (int index = 0; index < possibleDigitsAsc.length; index++) {
			int[] digits = possibleDigitsAsc[index];
			if (digits.length == 1)
				z = z.input(new PartialInputProvider(Map.of(index, digits[0])));
		}
		z = z.simplifyForZeroResult();
		
		return searchModelNumberRecursive(min ? possibleDigitsAsc : possibleDigitsDesc, 0, z);
	}
	
	static int[][] filterDigits(Value z) {
		Map<Integer, Integer> singlePossibleDigits = new HashMap<>();
		
		int[][] possibleDigits = new int[14][];
		for (int index = 0; index < 14; index++) {
			if (singlePossibleDigits.containsKey(index))
				continue;
			List<Integer> digits = new ArrayList<>();
			for (int digit = 9; digit > 0; digit--) {
				Map<Integer, Integer> inputDigits = new HashMap<>(singlePossibleDigits);
				inputDigits.put(index, digit);
				Value simplified = z.input(new PartialInputProvider(inputDigits));
				if (simplified.possibleValues().contains(0))
					digits.add(digit);
			}
			possibleDigits[index] = digits.stream().mapToInt(i -> i).toArray();
			if (digits.size() == 1) {
				singlePossibleDigits.put(index, digits.getFirst());
				index = -1;
			}
		}
		return possibleDigits;
	}
	
	static String searchModelNumberRecursive(int[][] possibleDigits, int index, Value z) {
		if (index >= possibleDigits.length - 1) {
			for (int digit : possibleDigits[index]) {
				PartialInputProvider input = new PartialInputProvider(Map.of(index, digit));
				if (z.input(input).compute() == 0)
					return "" + digit;
			}
			return null;
		}
		for (int digit : possibleDigits[index]) {
			PartialInputProvider input = new PartialInputProvider(Map.of(index, digit));
			Value simplifiedZ = z.input(input);
			if (!simplifiedZ.possibleValues().contains(0))
				continue;
			String result = searchModelNumberRecursive(possibleDigits, index + 1, simplifiedZ);
			if (result != null)
				return digit + result;
		}
		return null;
	}
	
	static State process(List<String> operations) {
		State state = new State();
		int inputIndex = 0;
		for (String operation : operations) {
			String[] split = operation.split(" ");
			VariableValue left = VariableValue.of(split[1]);
			
			if ("inp".equals(split[0])) {
				left.set(state, new InputValue(inputIndex++));
				continue;
			}
			
			Value leftValue = left.substituteVars(state);
			Value rightValue = Value.of(split[2]).substituteVars(state);
			Value nextValue = switch (split[0]) {
				case "add" -> OperationValue.add(leftValue, rightValue);
				case "mul" -> OperationValue.mul(leftValue, rightValue);
				case "div" -> OperationValue.div(leftValue, rightValue);
				case "mod" -> OperationValue.mod(leftValue, rightValue);
				case "eql" -> OperationValue.eql(leftValue, rightValue);
				default -> throw new IllegalStateException("Unexpected operation: " + split[0]);
			};
			left.set(state, nextValue.simplify());
		}
		return state;
	}
	
	@Data
	static class State {
		Value w = ConstantValue.ZERO;
		Value x = ConstantValue.ZERO;
		Value y = ConstantValue.ZERO;
		Value z = ConstantValue.ZERO;
		
		@Override
		public String toString() {
			return "State:" +
					"\n w = " + w + " " + w.possibleValues() +
					"\n x = " + x + " " + x.possibleValues() +
					"\n y = " + y + " " + y.possibleValues() +
					"\n z = " + z + " " + z.possibleValues();
		}
	}
	
	interface Value {
		
		Value substituteVars(State state);
		
		Value simplify();
		
		default Value simplifyForZeroResult() {
			return this;
		}
		
		long compute();
		
		Value input(InputProvider input);
		
		ValuesRange possibleValues();
		
		static Value of(String token) {
			if (Character.isLetter(token.charAt(0)))
				return VariableValue.of(token);
			return new ConstantValue(Integer.parseInt(token));
		}
	}
	
	record ConstantValue(long number, ValuesRange possibleValues) implements Value {
		static final ConstantValue ZERO = new ConstantValue(0);
		static final ConstantValue ONE = new ConstantValue(1);
		
		public ConstantValue(long number) {
			this(number, new ValuesRange(number, number));
		}
		
		@Override
		public Value substituteVars(State state) {
			return this;
		}
		
		@Override
		public Value simplify() {
			return this;
		}
		
		@Override
		public Value input(InputProvider input) {
			return this;
		}
		
		@Override
		public long compute() {
			return number;
		}
		
		@Override
		public String toString() {
			return "" + number;
		}
	}
	
	record InputValue(int index) implements Value {
		
		@Override
		public Value substituteVars(State state) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Value simplify() {
			return this;
		}
		
		@Override
		public Value input(InputProvider input) {
			Integer value = input.get(index);
			return value == null ? this : new ConstantValue(value);
		}
		
		@Override
		public long compute() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public ValuesRange possibleValues() {
			return ValuesRange.MODEL_NUMBER_DIGIT;
		}
		
		@Override
		public String toString() {
			return "(inp " + index + ")";
		}
	}
	
	record VariableValue(String name, Function<State, Value> getter, BiConsumer<State, Value> setter) implements Value {
		
		@Override
		public Value substituteVars(State state) {
			return getter.apply(state);
		}
		
		void set(State state, Value value) {
			setter.accept(state, value);
		}
		
		@Override
		public Value simplify() {
			return this;
		}
		
		@Override
		public Value input(InputProvider input) {
			return this;
		}
		
		@Override
		public long compute() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public ValuesRange possibleValues() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public String toString() {
			return name;
		}
		
		static Map<String, VariableValue> cache = Map.of(
				"w", new VariableValue("w", State::getW, State::setW),
				"x", new VariableValue("x", State::getX, State::setX),
				"y", new VariableValue("y", State::getY, State::setY),
				"z", new VariableValue("z", State::getZ, State::setZ));
		
		static VariableValue of(String name) {
			return cache.get(name);
		}
	}
	
	@Data
	static abstract class OperationValue implements Value {
		final String name;
		final BiFunction<Value, Value, Value> constructor;
		final Value left;
		final Value right;
		ValuesRange possibleValues;
		
		@Override
		public Value substituteVars(State state) {
			return this;
		}
		
		@Override
		public final Value simplify() {
			if (left instanceof ConstantValue && right instanceof ConstantValue)
				return new ConstantValue(compute());
			ValuesRange range = possibleValues();
			if (range.singleValue())
				// TODO compute / doCompute
				return new ConstantValue(range.min);
			return doSimplify();
		}
		
		abstract Value doSimplify();
		
		@Override
		public Value input(InputProvider input) {
			Value nextLeft = left.input(input);
			Value nextRight = right.input(input);
			if (left == nextLeft && right == nextRight)
				return this;
			return constructor.apply(nextLeft, nextRight).simplify();
		}
		
		@Override
		public ValuesRange possibleValues() {
			if (possibleValues == null)
				possibleValues = initPossibleValues();
			return possibleValues;
		}
		
		abstract ValuesRange initPossibleValues();
		
		@Override
		public String toString() {
			return "(" + left + " " + name + " " + right + ")";
		}
		
		static Value add(Value left, Value right) {
			if (left instanceof ConstantValue) {
				if (right instanceof ConstantValue)
					return new ConstantValue(left.compute() + right.compute());
				return new AddOperationValue(right, left);
			}
			return new AddOperationValue(left, right);
		}
		
		static Value mul(Value left, Value right) {
			if (left instanceof ConstantValue) {
				if (right instanceof ConstantValue)
					return new ConstantValue(left.compute() * right.compute());
				return new MulOperationValue(right, left);
			}
			return new MulOperationValue(left, right);
		}
		
		static OperationValue div(Value left, Value right) {
			return new DivOperationValue(left, right);
		}
		
		static OperationValue mod(Value left, Value right) {
			return new ModOperationValue(left, right);
		}
		
		static OperationValue eql(Value left, Value right) {
			return new EqlOperationValue(left, right);
		}
	}
	
	static class AddOperationValue extends OperationValue {
		
		public AddOperationValue(Value left, Value right) {
			super("+", OperationValue::add, left, right);
		}
		
		@Override
		public Value doSimplify() {
			if (right instanceof ConstantValue) {
				if (ConstantValue.ZERO.equals(right))
					return left;
				if (left instanceof AddOperationValue add && add.right instanceof ConstantValue)
					return OperationValue.add(add.left, OperationValue.add(right, add.right)).simplify();
			}
			return this;
		}
		
		@Override
		public Value simplifyForZeroResult() {
			if (possibleValues().min != 0)
				return this;
			return OperationValue.add(left.simplifyForZeroResult(), right.simplifyForZeroResult()).simplify();
		}
		
		@Override
		public long compute() {
			return left.compute() + right.compute();
		}
		
		@Override
		public ValuesRange initPossibleValues() {
			ValuesRange l = left.possibleValues();
			ValuesRange r = right.possibleValues();
			return new ValuesRange(l.min + r.min, l.max + r.max);
		}
	}
	
	static class MulOperationValue extends OperationValue {
		
		public MulOperationValue(Value left, Value right) {
			super("*", OperationValue::mul, left, right);
		}
		
		@Override
		public Value doSimplify() {
			if (right instanceof ConstantValue) {
				if (ConstantValue.ZERO.equals(right))
					return ConstantValue.ZERO;
				if (ConstantValue.ONE.equals(right))
					return left;
				if (left instanceof MulOperationValue mul && mul.right instanceof ConstantValue)
					return OperationValue.mul(mul.left, OperationValue.mul(right, mul.right)).simplify();
				
				// можно сократить дробь вместо проверки на равенство
				// нельзя, т. к. деление округляет
//			    if (left instanceof DivOperationValue div && right.equals(div.right))
//			    	return div.left;
				if (left instanceof AddOperationValue add) {
					if (add.left instanceof DivOperationValue div && div.right.equals(right))
						return OperationValue.add(div.left, OperationValue.mul(add.right, right)).simplify();
					if (add.right instanceof DivOperationValue div && div.right.equals(right))
						return OperationValue.add(OperationValue.mul(add.left, right), div.left).simplify();
				}
			}
			return this;
		}
		
		@Override
		public Value simplifyForZeroResult() {
			if (!left.possibleValues().contains(0))
				return right.simplifyForZeroResult();
			if (!right.possibleValues().contains(0))
				return left.simplifyForZeroResult();
			return this;
		}
		
		@Override
		public long compute() {
			return left.compute() * right.compute();
		}
		
		@Override
		public ValuesRange initPossibleValues() {
			ValuesRange l = left.possibleValues();
			ValuesRange r = right.possibleValues();
			
			if (l.min >= 0 && r.min >= 0)
				return new ValuesRange(l.min * r.min, l.max * r.max);
			
			long max = Math.max(Math.abs(l.min), Math.abs(l.max)) * Math.max(Math.abs(r.min), Math.abs(r.max));
			return new ValuesRange(-max, max);
		}
	}
	
	static class DivOperationValue extends OperationValue {
		
		public DivOperationValue(Value left, Value right) {
			super("/", OperationValue::div, left, right);
		}
		
		@Override
		public Value doSimplify() {
			if (ConstantValue.ZERO.equals(left))
				return ConstantValue.ZERO;
			
			if (right instanceof ConstantValue) {
				if (ConstantValue.ONE.equals(right))
					return left;
				
				// можно сократить дробь вместо проверки на равенство
				if (left instanceof MulOperationValue mul && right.equals(mul.right))
					return mul.left;
				if (left instanceof AddOperationValue add) {
					if (add.left instanceof MulOperationValue mul && mul.right.equals(right))
						return OperationValue.add(mul.left, OperationValue.div(add.right, right).simplify()).simplify();
					if (add.right instanceof MulOperationValue mul && mul.right.equals(right))
						return OperationValue.add(OperationValue.div(add.left, right), mul.left).simplify();
				}
			}
			return this;
		}
		
		@Override
		public long compute() {
			return left.compute() / right.compute();
		}
		
		@Override
		public ValuesRange initPossibleValues() {
			ValuesRange l = left.possibleValues();
			ValuesRange r = right.possibleValues();
			
			if (r.singleValue() && r.min != 0)
				return new ValuesRange(l.min / r.min, l.max / r.min);
			
			long maxL = Math.max(Math.abs(l.min), Math.abs(l.max));
			long minR = Math.min(Math.abs(r.min), Math.abs(r.max));
			
			if (maxL < minR)
				return ValuesRange.ZERO;

			if (0 < r.min)
				return new ValuesRange(l.min / minR, l.max / minR);
			if (r.max < 0)
				return new ValuesRange(-l.max / minR, -l.min / minR);
			return new ValuesRange(-maxL / minR, maxL / minR);
		}
	}
	
	static class ModOperationValue extends OperationValue {
		
		public ModOperationValue(Value left, Value right) {
			super("%", OperationValue::mod, left, right);
		}
		
		@Override
		public Value doSimplify() {
			if (ConstantValue.ZERO.equals(left))
				return ConstantValue.ZERO;
			if (ConstantValue.ONE.equals(left))
				return ConstantValue.ONE;
			
			if (right instanceof ConstantValue) {
				if (left.possibleValues() instanceof ValuesRange lr && 0 < lr.min && lr.max < right.compute())
					return left;
				
//				if (left instanceof MulOperationValue mul)
//					if (right.equals(mul.right))
//						return new ConstantValue(0);
				
				if (left instanceof AddOperationValue add) {
					if (add.left instanceof MulOperationValue mul && mul.right.equals(right))
						return OperationValue.mod(add.right, right).simplify();
					if (add.right instanceof MulOperationValue mul && mul.right.equals(right))
						return OperationValue.mod(add.left, right).simplify();
				}
			}
			return this;
		}
		
		@Override
		public long compute() {
			return left.compute() % right.compute();
		}
		
		@Override
		public ValuesRange initPossibleValues() {
			ValuesRange l = left.possibleValues();
			ValuesRange r = right.possibleValues();
			if (0 < l.min && l.max < r.max)
				return l;
			return new ValuesRange(0, r.max - 1);
		}
	}
	
	static class EqlOperationValue extends OperationValue {
		
		public EqlOperationValue(Value left, Value right) {
			super("==", OperationValue::eql, left, right);
		}
		
		@Override
		public Value doSimplify() {
			return this;
		}
		
		@Override
		public long compute() {
			return left.compute() == right.compute() ? 1 : 0;
		}
		
		@Override
		public ValuesRange initPossibleValues() {
			ValuesRange l = left.possibleValues();
			ValuesRange r = right.possibleValues();
			if (l.max < r.min || r.max < l.min)
				return ValuesRange.ZERO;
//			if (l.singleValue() && r.singleValue() && l.min == r.min)
//				return ValuesRange.ONE;
			return new ValuesRange(0, 1);
		}
	}
	
	record ValuesRange(long min, long max) {
		static ValuesRange ZERO = new ValuesRange(0, 0);
		static ValuesRange ONE = new ValuesRange(1, 1);
		static ValuesRange MODEL_NUMBER_DIGIT = new ValuesRange(1, 9);
		
		ValuesRange {
			if (max < min)
				throw new IllegalArgumentException("Bad range: " + min + ", " + max);
		}
		
		boolean singleValue() {
			return min == max;
		}
		
		boolean contains(int value) {
			return min <= value && value <= max;
		}
		
		@Override
		public String toString() {
			return "Range[" + min + ", " + max + "]";
		}
	}
	
	
	interface InputProvider {
		Integer get(int index);
	}
	
	record PartialInputProvider(Map<Integer, Integer> values) implements InputProvider {
		@Override
		public Integer get(int index) {
			return values.get(index);
		}
	}
	
	
	
	
//	static void tests() {
//		InputValue inp = new InputValue(0);
//		ConstantValue c5 = new ConstantValue(5);
//		ConstantValue cn5 = new ConstantValue(-5);
//		ConstantValue c25 = new ConstantValue(25);
//
//		List.of(
//				add(add(inp, c5), c5),
//				add(add(inp, c5), cn5),
//				mul(mul(inp, c5), c5),
//
//				div(inp, c25),
//				div(add(inp, c5), c25),
//				div(add(inp, c25), c25),
//				mod(inp, c25),
//				mod(add(inp, c5), c25),
//
//				div(mul(inp, c5), c5),
//				mul(div(inp, c5), c5),
//				mod(mul(inp, c5), c5),
//				mul(mod(inp, c5), c5),
//
//				div(add(mul(inp, c25), inp), c25),
//				div(add(inp, mul(inp, c25)), c25),
//				mul(add(div(inp, c25), inp), c25),
//				mul(add(inp, div(inp, c25)), c25),
//				mod(add(mul(inp, c25), inp), c25),
//				mod(add(inp, mul(inp, c25)), c25)
//		).forEach(v -> System.out.println(v + " -> " + v.simplify() + "   " + v.possibleValues()));
//	}
//
//	static OperationValue add(Value left, Value right) {
//		return new AddOperationValue(left, right);
//	}
//
//	static OperationValue mul(Value left, Value right) {
//		return new MulOperationValue(left, right);
//	}
//
//	static OperationValue div(Value left, Value right) {
//		return new DivOperationValue(left, right);
//	}
//
//	static OperationValue mod(Value left, Value right) {
//		return new ModOperationValue(left, right);
//	}
}