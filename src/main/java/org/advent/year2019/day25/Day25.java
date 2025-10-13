package org.advent.year2019.day25;

import lombok.RequiredArgsConstructor;
import org.advent.common.Direction;
import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer2;
import org.advent.year2019.intcode_computer.OutputConsumer;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

public class Day25 extends AdventDay {
	
	public static void main(String[] args) {
//		new DayRunner(new Day25()).runAll();
		new DayRunner(new Day25()).run("input.txt", 1);
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
//				new ExpectedAnswers("input2.txt", null, null)
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
	
	record Room(String name, String description, List<Direction> doors, List<String> items) {
		
		static Room parse(String description) {
			String name = description.split("==")[1].trim();
			
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
			return new Room(name, description, doors, items);
		}
		
		@Override
		public String toString() {
			return description.replace("Command?", "").trim();
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
		
		Map<String, Room> rooms = new LinkedHashMap<>();
		Map<String, Room> roomsByName = new LinkedHashMap<>();
		Set<Pair<String, Direction>> checkedDirections = new HashSet<>();
		StringBuilder currentPath = new StringBuilder();
		Queue<Action> actionsQueue = new LinkedList<>();
		
		
		String nextCommands(String output) {
			System.out.println("CURRENT PATH: " + currentPath);
			if (!actionsQueue.isEmpty())
				return actionsQueue.poll().run();
			
			Room room = rooms.get(currentPath.toString());
//			System.out.println(room);
			
			if (room == null) {
				room = Room.parse(output);
				if (roomsByName.containsKey(room.name)) {
					room = roomsByName.get(room.name);
				} else {
					System.out.println("NEW ROOM: " + rooms.size());
					rooms.put(currentPath.toString(), room);
					roomsByName.put(room.name, room);
					
					if (room.items.stream().anyMatch(i -> !ignoredItems.contains(i))) {
						room.items.stream()
								.filter(i -> !ignoredItems.contains(i))
								.map(i -> new Action("take " + i, () -> {
								}))
								.forEach(actionsQueue::add);
						return Objects.requireNonNull(actionsQueue.poll()).run();
					}
				}
			}
			
			Direction back = currentPath.isEmpty() ? null : Direction.parseCompassLetter(currentPath.charAt(currentPath.length() - 1)).reverse();
			
			Room _room = room;
			Optional<Direction> nextDirection = room.doors.stream()
					.filter(d -> d != back)
					.filter(d -> !checkedDirections.contains(Pair.of(_room.name, d)))
//					.filter(d -> !rooms.containsKey(currentPath.toString() + d.compassLetter()))
					.findAny();
			if (nextDirection.isPresent()) {
				Direction direction = nextDirection.get();
				currentPath.append(direction.compassLetter());
				checkedDirections.add(Pair.of(_room.name, direction));
				return direction.getCompassName();
			}
			
			if (back != null) {
				currentPath.setLength(currentPath.length() - 1);
				return back.getCompassName();
			}
			
//			System.out.println("All rooms:");
//			rooms.values().forEach(System.out::println);

//			throw new RuntimeException("Not implemented yet");
			return null;
		}
	}
	
	static class DirectionsMaze<T> {
		static class Node<T> {
			T value;
			Map<Direction, Node<T>> neighbors = new LinkedHashMap<>();
		}
	}
}