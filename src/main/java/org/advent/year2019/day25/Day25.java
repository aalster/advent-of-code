package org.advent.year2019.day25;

import lombok.RequiredArgsConstructor;
import org.advent.common.Direction;
import org.advent.common.MazeUtils;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.runner.OutputUtils;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer2;
import org.advent.year2019.intcode_computer.OutputConsumer;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Scanner;
import java.util.SequencedSet;
import java.util.Set;

public class Day25 extends AdventDay {
	
	public static void main(String[] args) {
//		new DayRunner(new Day25()).runAll();
		new DayRunner(new Day25()).run("input.txt", 1);
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", null, null)
		);
	}
	
	long[] program;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		program = IntcodeComputer2.parseProgram(input.nextLine());
	}
	
	@Override
	public Object part1() {
		OutputConsumer.BufferingTextOutputConsumer outputBuffer = OutputConsumer.bufferingText();
		OutputConsumer output = OutputConsumer.combine(OutputConsumer.printer(), outputBuffer);
		
//		InputProvider input = InputProvider.console();
		InputProvider input = new WalkerInputProvider(outputBuffer);
		
		IntcodeComputer2 computer = new IntcodeComputer2(program, input, output);
		
		computer.run();
		return null;
	}
	
	@Override
	public Object part2() {
		return null;
	}
	
	record Action(String command, Runnable action) {
		String run() {
			action.run();
			return command;
		}
	}
	
	record Room(Point location, String description, List<Direction> doors, List<String> items) {
		
		static Room parse(Point location, String description) {
			List<Direction> doors = new ArrayList<>();
			List<String> items = new ArrayList<>();
			
			Iterator<String> iterator = List.of(description.split("\n")).iterator();
			while (iterator.hasNext()) {
				String line = iterator.next();
				if (line.equals("Doors here lead:")) {
					String door = iterator.next();
					while (iterator.hasNext() && !door.isEmpty()) {
						char directionChar = StringUtils.removeStart(door, "- ").charAt(0);
						doors.add(Direction.parseCompassLetter(Character.toUpperCase(directionChar)));
						door = iterator.next();
					}
				}
				if (line.equals("Items here:")) {
					String item = iterator.next();
					while (iterator.hasNext() && !item.isEmpty()) {
						items.add(StringUtils.removeStart(item, "- "));
						item = iterator.next();
					}
				}
			}
			return new Room(location, description, doors, items);
		}
		
		@Override
		public String toString() {
			return "Position: " + location + "\n\n"
					+ description.replace("Command?", "").trim() + "\n\n---------------\n";
		}
	}
	
	@RequiredArgsConstructor
	private static class WalkerInputProvider implements InputProvider {
		final OutputConsumer.BufferingTextOutputConsumer outputBuffer;
		InputProvider delegate = InputProvider.empty();
		InputProvider console = InputProvider.console();
		boolean manual = false;
		
		@Override
		public boolean hasNext() {
			return true;
		}
		
		@Override
		public long nextInput() {
			if (manual)
				return console.nextInput();
			
			if (!delegate.hasNext()) {
				String nextCommand = nextCommands(outputBuffer.read());
				if (nextCommand == null) {
					manual = true;
					System.out.println("\nMANUAL!");
					return console.nextInput();
				}
				System.out.println("<< " + nextCommand);
				delegate = InputProvider.ascii(nextCommand + "\n");
			}
			return delegate.nextInput();
		}
		
		final Set<String> ignoredItems = Set.of("escape pod", "infinite loop", "molten lava",
				"giant electromagnet", "photons");
		
		Map<Point, Room> rooms = new LinkedHashMap<>();
		Point position = Point.ZERO;
		List<String> inventory = new ArrayList<>();
		SequencedSet<Point> unexplored = new LinkedHashSet<>();
		Queue<Action> actionsQueue = new LinkedList<>();
		
		void move(Direction d) {
			position = position.shift(d);
		}
		
		String nextCommands(String output) {
			if (!actionsQueue.isEmpty())
				return actionsQueue.poll().run();
			
			Room room = rooms.get(position);
//			System.out.println(room);
			
			if (room == null) {
				System.out.println("NEW ROOM: " + rooms.size());
				room = Room.parse(position, output);
				rooms.put(position, room);
				unexplored.remove(position);
				
				room.doors.stream()
						.map(position::shift)
						.filter(p -> !rooms.containsKey(p))
						.forEach(e -> {
							if (!unexplored.add(e))
								System.out.println("Multiple ways to " + e);
						});
				
				if (room.items.stream().anyMatch(i -> !ignoredItems.contains(i))) {
					room.items.stream()
							.filter(i -> !ignoredItems.contains(i))
							.map(i -> new Action("take " + i, () -> inventory.add(i)))
							.forEach(actionsQueue::add);
					return Objects.requireNonNull(actionsQueue.poll()).run();
				}
			}

			if (!unexplored.isEmpty()) {
				Point target = unexplored.getLast();
				Map<Point, Integer> stepsMap = MazeUtils.stepsMap(position, target, (p, d) -> rooms.get(p).doors.contains(d));
				List<Point> path = MazeUtils.findPath(stepsMap, target);
				unexplored.remove(target);
				MazeUtils.pathToDirections(path).stream()
						.map(d -> new Action(d.getCompassName(), () -> move(d)))
						.forEach(actionsQueue::add);
				return Objects.requireNonNull(actionsQueue.poll()).run();
			}
			
			printMap();
			
//			System.out.println("All rooms:");
//			rooms.values().forEach(System.out::println);
			
//			throw new RuntimeException("Not implemented yet");
			return null;
		}
		
		void printMap() {
			System.out.println();
			Rect bounds = Point.bounds(rooms.keySet());
			for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
				for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
					Point p = new Point(x, y);
					Room room = rooms.get(p);
					String name = room == null ? "." : room.description.split("==")[1].trim();
					if (position.equals(p))
						name += " (HERE)";
					System.out.print(OutputUtils.leftPad(name, 35));
				}
				System.out.println();
			}
		}
	}
	
	static class DirectionsMaze<T> {
		static class Node<T> {
			T value;
			Map<Direction, Node<T>> neighbors = new LinkedHashMap<>();
		}
	}
}