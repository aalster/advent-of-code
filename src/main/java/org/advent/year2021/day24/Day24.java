package org.advent.year2021.day24;

import lombok.Data;
import org.advent.common.Utils;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("DeconstructionCanBeUsed")
public class Day24 {
	static final boolean applyModelNumberOptimizations = true;
	
	public static void main(String[] args) {
//		if (true) {
//			tests();
//			return;
//		}
		Scanner input = Utils.scanFileNearClass(Day24.class, "input.txt");
		List<String> lines = Utils.readLines(input);
		
		long start = System.currentTimeMillis();
		System.out.println("Answer 1: " + part1(lines));
		System.out.println("Answer 2: " + part2());
		System.out.println((System.currentTimeMillis() - start) + "ms");
	}
	
	private static long part1(List<String> lines) {
		State state = process(lines);
		Value z = state.z;
		System.out.println(z);
		System.out.println("Range: " + z.possibleValues());
		
		for (int i = 1; i < 10; i++) {
			PartialInputProvider input = new PartialInputProvider(Map.of(0, i));
			Value simplified = z.input(input);
			System.out.println(simplified);
			System.out.println("Range: " + simplified.possibleValues());
		}
		return 0;
	}
	
	private static State process(List<String> operations) {
		State state = new State();
		int inputIndex = 0;
		int i = 0;
		for (String operation : operations) {
//			System.out.println(state);
//			System.out.println();
//			System.out.println(i++ + " " + operation);
			String[] split = operation.split(" ");
			VariableValue left = VariableValue.of(split[1]);
			
			if ("inp".equals(split[0])) {
				left.set(state, new InputValue(inputIndex++));
				continue;
			}
			
			Value leftValue = left.substituteVars(state);
			Value rightValue = Value.of(split[2]).substituteVars(state);
			Value nextValue = switch (split[0]) {
				case "add" -> new AddOperationValue(leftValue, rightValue);
				case "mul" -> new MulOperationValue(leftValue, rightValue);
				case "div" -> new DivOperationValue(leftValue, rightValue);
				case "mod" -> new ModOperationValue(leftValue, rightValue);
				case "eql" -> new EqlOperationValue(leftValue, rightValue);
				default -> throw new IllegalStateException("Unexpected operation: " + split[0]);
			};
			left.set(state, nextValue.simplify());
		}
		return state;
	}
	
	private static long part2() {
		return 0;
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
		
		int compute();
		
		Value input(InputProvider input);
		
		PossibleValues possibleValues();
		
		static Value of(String token) {
			if (Character.isLetter(token.charAt(0)))
				return VariableValue.of(token);
			return new ConstantValue(Integer.parseInt(token));
		}
	}
	
	record ConstantValue(int number) implements Value {
		static final ConstantValue ZERO = new ConstantValue(0);
		static final ConstantValue ONE = new ConstantValue(1);
		
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
		public int compute() {
			return number;
		}
		
		@Override
		public PossibleValues possibleValues() {
			return new ValuesRange(number, number);
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
		public int compute() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public PossibleValues possibleValues() {
			if (applyModelNumberOptimizations)
				return PossibleValues.MODEL_NUMBER_DIGIT;
			return PossibleValues.ANY;
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
		public int compute() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public PossibleValues possibleValues() {
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
		final BiFunction<Value, Value, OperationValue> constructor;
		final Value left;
		final Value right;
		
		@Override
		public Value substituteVars(State state) {
			return this;
		}
		
		@Override
		public Value simplify() {
			if (left instanceof ConstantValue && right instanceof ConstantValue)
				return new ConstantValue(compute());
			PossibleValues possibleValues = possibleValues();
			if (possibleValues instanceof ValuesRange range && range.singleValue())
				return new ConstantValue(range.min);
			return this;
		}
		
		@Override
		public Value input(InputProvider input) {
			return constructor.apply(left.input(input), right.input(input)).simplify();
		}
		
		@Override
		public String toString() {
			return "(" + left + " " + name + " " + right + ")";
		}
	}
	
	static class AddOperationValue extends OperationValue {
		
		public AddOperationValue(Value left, Value right) {
			super("+", AddOperationValue::new, left, right);
		}
		
		@Override
		public Value simplify() {
			if (ConstantValue.ZERO.equals(left))
				return right;
			if (ConstantValue.ZERO.equals(right))
				return left;
			if (left instanceof ConstantValue && !(right instanceof ConstantValue))
				return new AddOperationValue(right, left).simplify();
			if (right instanceof ConstantValue && left instanceof AddOperationValue add && add.right instanceof ConstantValue)
				return new AddOperationValue(add.left, new AddOperationValue(right, add.right).simplify()).simplify();
			return super.simplify();
		}
		
		@Override
		public int compute() {
			return left.compute() + right.compute();
		}
		
		@Override
		public PossibleValues possibleValues() {
			PossibleValues l = left.possibleValues();
			PossibleValues r = right.possibleValues();
			if (l == PossibleValues.ANY || r == PossibleValues.ANY)
				return PossibleValues.ANY;
			if (l instanceof ValuesRange lr && r instanceof ValuesRange rr)
				return new ValuesRange(lr.min + rr.min, lr.max + rr.max);
			throw new IllegalStateException();
		}
	}
	
	static class MulOperationValue extends OperationValue {
		
		public MulOperationValue(Value left, Value right) {
			super("*", MulOperationValue::new, left, right);
		}
		
		@Override
		public Value simplify() {
			if (ConstantValue.ZERO.equals(left) || ConstantValue.ZERO.equals(right))
				return ConstantValue.ZERO;
			if (ConstantValue.ONE.equals(left))
				return right;
			if (ConstantValue.ONE.equals(right))
				return left;
			if (left instanceof ConstantValue && !(right instanceof ConstantValue))
				return new MulOperationValue(right, left);
			if (right instanceof ConstantValue && left instanceof MulOperationValue mul && mul.right instanceof ConstantValue)
				return new MulOperationValue(mul.left, new MulOperationValue(right, mul.right).simplify()).simplify();
			
			if (right instanceof ConstantValue) {
				// можно сократить дробь вместо проверки на равенство
				// нельзя, т. к. деление округляет
//				if (left instanceof DivOperationValue div && right.equals(div.right))
//					return div.left;
				if (left instanceof AddOperationValue add) {
					if (add.left instanceof DivOperationValue div && div.right.equals(right))
						return new AddOperationValue(div.left, new MulOperationValue(add.right, right).simplify()).simplify();
					if (add.right instanceof DivOperationValue div && div.right.equals(right))
						return new AddOperationValue(new MulOperationValue(add.left, right).simplify(), div.left).simplify();
				}
			}
			return super.simplify();
		}
		
		@Override
		public int compute() {
			return left.compute() * right.compute();
		}
		
		@Override
		public PossibleValues possibleValues() {
			PossibleValues l = left.possibleValues();
			PossibleValues r = right.possibleValues();
			if (!(l instanceof ValuesRange lr) || !(r instanceof ValuesRange rr))
				return PossibleValues.ANY;
			int max = Math.max(Math.abs(lr.min), Math.abs(lr.max)) * Math.max(Math.abs(rr.min), Math.abs(rr.max));
			if (lr.min < 0 || rr.min < 0)
				return new ValuesRange(-max, max);
			try {
				return new ValuesRange(lr.min * rr.min, max);
			} catch (RuntimeException e) {
				System.out.println("LEFT: " + l);
				System.out.println("RIGHT: " + r);
				throw e;
			}
		}
	}
	
	static class DivOperationValue extends OperationValue {
		
		public DivOperationValue(Value left, Value right) {
			super("/", DivOperationValue::new, left, right);
		}
		
		@Override
		public Value simplify() {
			if (ConstantValue.ZERO.equals(left))
				return ConstantValue.ZERO;
			if (ConstantValue.ONE.equals(right))
				return left;
			
			if (right instanceof ConstantValue) {
				// можно сократить дробь вместо проверки на равенство
				if (left instanceof MulOperationValue mul && right.equals(mul.right))
					return mul.left;
				if (left instanceof AddOperationValue add) {
					if (add.left instanceof MulOperationValue mul && mul.right.equals(right))
						return new AddOperationValue(mul.left, new DivOperationValue(add.right, right).simplify()).simplify();
					if (add.right instanceof MulOperationValue mul && mul.right.equals(right))
						return new AddOperationValue(new DivOperationValue(add.left, right).simplify(), mul.left).simplify();
				}
			}
			
			return super.simplify();
		}
		
		@Override
		public int compute() {
			return left.compute() / right.compute();
		}
		
		@Override
		public PossibleValues possibleValues() {
			PossibleValues l = left.possibleValues();
			PossibleValues r = right.possibleValues();
			if (!(l instanceof ValuesRange lr) || !(r instanceof ValuesRange rr))
				return PossibleValues.ANY;
			
			int maxL = Math.max(Math.abs(lr.min), Math.abs(lr.max));
			int minR = Math.min(Math.abs(rr.min), Math.abs(rr.max));
			
			if (maxL < minR)
				return PossibleValues.ZERO;
			
			if (0 < rr.min)
				return new ValuesRange(lr.min / minR, lr.max / minR);
			if (rr.max < 0)
				return new ValuesRange(-lr.max / minR, -lr.min / minR);
			return new ValuesRange(-maxL / minR, maxL / minR);
		}
	}
	
	static class ModOperationValue extends OperationValue {
		
		public ModOperationValue(Value left, Value right) {
			super("%", ModOperationValue::new, left, right);
		}
		
		@Override
		public Value simplify() {
			if (ConstantValue.ZERO.equals(left))
				return ConstantValue.ZERO;
			if (ConstantValue.ONE.equals(left))
				return ConstantValue.ONE;
			
			if (right instanceof ConstantValue) {
				if (left.possibleValues() instanceof ValuesRange lr && 0 < lr.min && lr.max < right.compute()) {
					return left;
				}
				if (left instanceof MulOperationValue mul) {
					if (right.equals(mul.left) || right.equals(mul.right))
						return new ConstantValue(0);
				}
				if (left instanceof AddOperationValue add) {
					if (add.left instanceof MulOperationValue mul && mul.right.equals(right))
						return new ModOperationValue(add.right, right).simplify();
					if (add.right instanceof MulOperationValue mul && mul.right.equals(right))
						return new ModOperationValue(add.left, right).simplify();
				}
			}
			
			return super.simplify();
		}
		
		@Override
		public int compute() {
			return left.compute() % right.compute();
		}
		
		@Override
		public PossibleValues possibleValues() {
			PossibleValues l = left.possibleValues();
			PossibleValues r = right.possibleValues();
			if (r == PossibleValues.ANY)
				return PossibleValues.ANY;
			if (r instanceof ValuesRange rr) {
				if (l instanceof ValuesRange lr && 0 < lr.min && lr.max < rr.max)
					return l;
				return new ValuesRange(0, rr.max - 1);
			}
			throw new UnsupportedOperationException();
		}
	}
	
	static class EqlOperationValue extends OperationValue {
		
		public EqlOperationValue(Value left, Value right) {
			super("==", EqlOperationValue::new, left, right);
		}
		
		@Override
		public Value simplify() {
			PossibleValues possibleValues = possibleValues();
			if (possibleValues instanceof ValuesRange range && range.singleValue())
				return new ConstantValue(range.min);
			if (applyModelNumberOptimizations) {
				if (left instanceof InputValue)
					return simplifyForInput(right);
				if (right instanceof InputValue)
					return simplifyForInput(left);
			}
			return super.simplify();
		}
		
		private Value simplifyForInput(Value other) {
			if (other instanceof ConstantValue constant) {
				int c = constant.compute();
				if (c < 1 || 9 < c)
					return ConstantValue.ZERO;
			}
			return this;
		}
		
		@Override
		public int compute() {
			return left.compute() == right.compute() ? 1 : 0;
		}
		
		@Override
		public PossibleValues possibleValues() {
			PossibleValues l = left.possibleValues();
			PossibleValues r = right.possibleValues();
			if (l instanceof ValuesRange lr && r instanceof ValuesRange rr) {
				if (lr.max < rr.min || rr.max < lr.min)
					return PossibleValues.ZERO;
				if (lr.singleValue() && rr.singleValue() && lr.min == rr.min)
					return PossibleValues.ONE;
			}
			return new ValuesRange(0, 1);
		}
	}
	
	interface PossibleValues {
		PossibleValues ANY = new PossibleValues() {
			@Override
			public String toString() {
				return "ANY";
			}
		};
		ValuesRange ZERO = new ValuesRange(0, 0);
		ValuesRange ONE = new ValuesRange(1, 1);
		ValuesRange MODEL_NUMBER_DIGIT = new ValuesRange(1, 9);
	}
	
	record ValuesRange(int min, int max) implements PossibleValues {
		ValuesRange {
			if (max < min)
				throw new IllegalArgumentException("Bad range: " + min + ", " + max);
		}
		
		boolean singleValue() {
			return min == max;
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
	
	
	
	
	static void tests() {
		InputValue inp = new InputValue(0);
		ConstantValue c2 = new ConstantValue(2);
		ConstantValue c5 = new ConstantValue(5);
		ConstantValue cn5 = new ConstantValue(-5);
		ConstantValue c10 = new ConstantValue(10);
		ConstantValue c25 = new ConstantValue(25);
		
		List.of(
				add(add(inp, c5), c5),
				add(add(inp, c5), cn5),
				mul(mul(inp, c5), c5),
				
				div(inp, c25),
				div(add(inp, c5), c25),
				div(add(inp, c25), c25),
				mod(inp, c25),
				mod(add(inp, c5), c25),
				
				div(mul(inp, c5), c5),
				mul(div(inp, c5), c5),
				mod(mul(inp, c5), c5),
				mul(mod(inp, c5), c5),
				
				div(add(mul(inp, c25), inp), c25),
				div(add(inp, mul(inp, c25)), c25),
				mul(add(div(inp, c25), inp), c25),
				mul(add(inp, div(inp, c25)), c25),
				mod(add(mul(inp, c25), inp), c25),
				mod(add(inp, mul(inp, c25)), c25)
		).forEach(v -> System.out.println(v + " -> " + v.simplify() + "   " + v.possibleValues()));
	}
	
	static OperationValue add(Value left, Value right) {
		return new AddOperationValue(left, right);
	}
	
	static OperationValue mul(Value left, Value right) {
		return new MulOperationValue(left, right);
	}
	
	static OperationValue div(Value left, Value right) {
		return new DivOperationValue(left, right);
	}
	
	static OperationValue mod(Value left, Value right) {
		return new ModOperationValue(left, right);
	}
}