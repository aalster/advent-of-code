package org.advent.year2018.day13;

import lombok.Data;
import org.advent.common.Direction;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day13 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day13()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", "7,3", ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, "6,4"),
				new ExpectedAnswers("input.txt", "33,69", "135,9")
		);
	}
	
	Map<Point, Character> field;
	List<Cart> carts;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		field = new HashMap<>();
		carts = new ArrayList<>();
		int y = 0;
		for (String line : Utils.readLines(input)) {
			char[] chars = line.toCharArray();
			for (int x = 0; x < chars.length; x++) {
				char c = chars[x];
				if (c == ' ')
					continue;
				if (c == '<' || c == '>') {
					carts.add(new Cart(new Point(x, y), Direction.parseSymbol(c)));
					c = '-';
				} else if (c == '^' || c == 'v') {
					carts.add(new Cart(new Point(x, y), Direction.parseSymbol(c)));
					c = '|';
				}
				field.put(new Point(x, y), c);
			}
			y++;
		}
	}
	
	@Override
	public Object part1() {
		Set<Point> cartsPositions = new HashSet<>(carts.stream().map(Cart::getPosition).toList());
		
		while (true) {
			carts.sort(Comparator.comparing(Cart::getPosition, Comparator.comparing(Point::y).thenComparing(Point::x)));
			for (Cart cart : carts) {
				cartsPositions.remove(cart.position);
				Point nextPosition = cart.move(field);
				if (!cartsPositions.add(nextPosition))
					return nextPosition.x() + "," + nextPosition.y();
			}
		}
	}
	
	@Override
	public Object part2() {
		Map<Point, Cart> cartsPositions = carts.stream().collect(Collectors.toMap(Cart::getPosition, cart -> cart));
		
		while (carts.size() > 1) {
			carts.sort(Comparator.comparing(Cart::getPosition, Comparator.comparing(Point::y).thenComparing(Point::x)));
			Set<String> crashedIds = new HashSet<>();
			for (Cart cart : carts) {
				if (crashedIds.contains(cart.id))
					continue;
				
				cartsPositions.remove(cart.position);
				Point nextPosition = cart.move(field);
				Cart crashCart = cartsPositions.put(nextPosition, cart);
				if (crashCart != null) {
					cartsPositions.remove(nextPosition);
					crashedIds.add(cart.id);
					crashedIds.add(crashCart.id);
				}
			}
			if (!crashedIds.isEmpty())
				carts.removeIf(c -> crashedIds.contains(c.id));
		}
		Point position = carts.getFirst().position;
		return position.x() + "," + position.y();
	}
	
	@Data
	static class Cart {
		static final Direction[] turns = new Direction[]{Direction.LEFT, Direction.UP, Direction.RIGHT};
		final String id;
		Point position;
		Direction direction;
		int turnIndex;
		
		Cart(Point position, Direction direction) {
			this.id = position.toString();
			this.position = position;
			this.direction = direction;
		}
		
		Point move(Map<Point, Character> field) {
			position = position.shift(direction);
			direction = switch (field.get(position)) {
				case '/' -> direction.rotate(direction.isVertical() ? Direction.RIGHT : Direction.LEFT);
				case '\\' -> direction.rotate(direction.isVertical() ? Direction.LEFT : Direction.RIGHT);
				case '+' -> direction.rotate(nextTurn());
				default -> direction;
			};
			return position;
		}
		
		Direction nextTurn() {
			return turns[turnIndex++ % turns.length];
		}
	}
}