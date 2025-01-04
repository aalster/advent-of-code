package org.advent.year2021.day18;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day18 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day18()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 4140, 3993),
				new ExpectedAnswers("input.txt", 3869, 4671)
		);
	}
	
	List<Element> elements;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		elements = Utils.readLines(input).stream().map(Element::parse).toList();
	}
	
	@Override
	public Object part1() {
		Element result = elements.getFirst();
		for (Element element : elements) {
			if (result == element)
				continue;
			result = Element.add(result, element);
		}
		return result.magnitude();
	}
	
	@Override
	public Object part2() {
		int maxMagnitude = 0;
		for (Element left : elements) {
			for (Element right : elements) {
				if (left == right)
					continue;
				int magnitude = Element.add(left, right).magnitude();
				if (maxMagnitude < magnitude)
					maxMagnitude = magnitude;
			}
		}
		return maxMagnitude;
	}
	
	static abstract class Element {
		PairElement parent;
		
		void reduce() {
			//noinspection StatementWithEmptyBody
			while (explode(4) || split()) {}
		}
		
		abstract boolean explode(int level);
		abstract boolean split();
		
		abstract int magnitude();
		abstract Element copy();
		abstract void asString(StringBuilder output);
		
		@Override
		public String toString() {
			StringBuilder output = new StringBuilder();
			asString(output);
			return output.toString();
		}
		
		
		static Element add(Element left, Element right) {
			PairElement result = new PairElement(left.copy(), right.copy());
			result.reduce();
			return result;
		}
		
		static Element parse(String line) {
			if (Character.isDigit(line.charAt(0)))
				return new NumberElement(Integer.parseInt(line));
			if (line.charAt(0) != '[')
				throw new RuntimeException("Bad format: " + line);
			
			char[] chars = line.toCharArray();
			int brackets = 0;
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				if (c == '[')
					brackets++;
				else if (c == ']')
					brackets--;
				if (c == ',' && brackets == 1)
					return new PairElement(Element.parse(line.substring(1, i)), Element.parse(line.substring(i + 1, chars.length - 1)));
			}
			throw new RuntimeException("Bad format: " + line);
		}
	}
	
	static class NumberElement extends Element {
		int number;
		
		NumberElement(int number) {
			this.number = number;
		}
		
		@Override
		boolean explode(int level) {
			return false;
		}
		
		@Override
		boolean split() {
			if (number < 10)
				return false;
			
			int left = number / 2;
			int right = left + number % 2;
			parent.replace(this, new PairElement(new NumberElement(left), new NumberElement(right)));
			return true;
		}
		
		@Override
		int magnitude() {
			return number;
		}
		
		@Override
		Element copy() {
			return new NumberElement(number);
		}
		
		@Override
		void asString(StringBuilder output) {
			output.append(number);
		}
	}
	
	static class PairElement extends Element {
		Element left;
		Element right;
		
		PairElement(Element left, Element right) {
			setLeft(left);
			setRight(right);
		}
		
		void setLeft(Element left) {
			if (this.left != null)
				this.left.parent = null;
			this.left = left;
			left.parent = this;
		}
		
		void setRight(Element right) {
			if (this.right != null)
				this.right.parent = null;
			this.right = right;
			right.parent = this;
		}
		
		void replace(Element remove, Element add) {
			if (left == remove)
				setLeft(add);
			else if (right == remove)
				setRight(add);
			else
				throw new RuntimeException("Bad replacement: " + this + " - " + remove);
		}
		
		@Override
		boolean explode(int level) {
			if (level <= 0) {
				doExplode();
				return true;
			}
			return left.explode(level - 1) || right.explode(level - 1);
		}
		
		@Override
		boolean split() {
			return left.split() || right.split();
		}
		
		void doExplode() {
			if (!(left instanceof NumberElement) || !(right instanceof NumberElement))
				throw new RuntimeException("Exploding error: " + this);
			
			PairElement leftParent = null;
			PairElement rightParent = null;
			PairElement current = this;
			while (leftParent == null || rightParent == null) {
				PairElement parent = current.parent;
				if (parent == null)
					break;
				if (leftParent == null && parent.right == current)
					leftParent = parent;
				if (rightParent == null && parent.left == current)
					rightParent = parent;
				current = parent;
			}
			
			if (leftParent != null) {
				Element leftChild = leftParent.left;
				while (leftChild instanceof PairElement next)
					leftChild = next.right;
				leftChild.parent.replace(leftChild, new NumberElement(((NumberElement) leftChild).number + ((NumberElement) left).number));
			}
			
			if (rightParent != null) {
				Element rightChild = rightParent.right;
				while (rightChild instanceof PairElement next)
					rightChild = next.left;
				rightChild.parent.replace(rightChild, new NumberElement(((NumberElement) rightChild).number + ((NumberElement) right).number));
			}
			
			NumberElement replacement = new NumberElement(0);
			if (parent.left == this)
				parent.setLeft(replacement);
			else
				parent.setRight(replacement);
		}
		
		@Override
		int magnitude() {
			return 3 * left.magnitude() + 2 * right.magnitude();
		}
		
		@Override
		Element copy() {
			return new PairElement(left.copy(), right.copy());
		}
		
		@Override
		public void asString(StringBuilder output) {
			output.append("[");
			left.asString(output);
			output.append(",");
			right.asString(output);
			output.append("]");
		}
	}
}