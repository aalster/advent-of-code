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
import java.util.Arrays;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day25 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day25()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 1073815584, ExpectedAnswers.IGNORE)
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
		String finalOutput = outputBuffer.read();
		
		Matcher matcher = Pattern.compile("You should be able to get in by typing (\\d+) on the keypad at the main airlock")
				.matcher(finalOutput);
		if (matcher.find())
			return Long.parseLong(matcher.group(1));
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
		Macro macro;
		
		@Override
		public boolean hasNext() {
			return true;
		}
		
		@Override
		public long nextInput() {
			if (!delegate.hasNext()) {
				String nextInput = nextInput(outputBuffer.read());
				System.out.println("<< " + nextInput);
				delegate = InputProvider.ascii(nextInput + "\n");
			}
			return delegate.nextInput();
		}
		
		public String nextInput(String output) {
			while (true) {
				if (macro != null) {
					String nextCommand = macro.nextCommands(output);
					if (nextCommand != null)
						return nextCommand;
					
					System.out.println("MACRO FINISHED");
					macro = null;
					continue;
				}
				
				String manualInput = new Scanner(System.in).nextLine();
				
				if (manualInput.startsWith("macro ")) {
					String[] split = manualInput.split(" ");
					macro = switch (split[1]) {
						case "inspect" -> new InspectMacro();
						case "goto" -> new GotoMacro(StringUtils.removeStart(manualInput, "macro goto").trim());
						case "break" -> new BruteForceMacro();
						default -> throw new RuntimeException("Unknown macro: " + manualInput);
					};
					continue;
				}
				
				if (manualInput.equals("exit"))
					throw new RuntimeException("Exit");
				return manualInput;
			}
		}
		
		final Set<String> ignoredItems = Set.of("escape pod", "infinite loop", "molten lava",
				"giant electromagnet", "photons");
		
		Map<String, Room> rooms = new LinkedHashMap<>();
		Map<String, Room> roomsByName = new LinkedHashMap<>();
		Set<Pair<String, Direction>> checkedDirections = new HashSet<>();
		StringBuilder currentPath = new StringBuilder();
		Queue<Action> actionsQueue = new LinkedList<>();
		
		interface Macro {
			String nextCommands(String output);
		}
		
		class InspectMacro implements Macro {
			
			@Override
			public String nextCommands(String output) {
				System.out.println("CURRENT PATH: " + currentPath);
				if (!actionsQueue.isEmpty())
					return actionsQueue.poll().run();
				
				Room room = rooms.get(currentPath.toString());
//			    System.out.println(room);
				
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
//				    	.filter(d -> !rooms.containsKey(currentPath.toString() + d.compassLetter()))
						.findAny();
				if (nextDirection.isPresent() && !room.name.equals("Security Checkpoint")) {
					Direction direction = nextDirection.get();
					currentPath.append(direction.compassLetter());
					checkedDirections.add(Pair.of(_room.name, direction));
					return direction.getCompassName();
				}
				
				if (back != null) {
					currentPath.setLength(currentPath.length() - 1);
					return back.getCompassName();
				}

//		    	System.out.println("All rooms:");
//		    	rooms.values().forEach(System.out::println);

//			    throw new RuntimeException("Not implemented yet");
				return null;
			}
		}
		
		class GotoMacro implements Macro {
			Queue<Integer> directions = new LinkedList<>();
			
			// TODO идет только с начала
			public GotoMacro(String targetRoom) {
				if (targetRoom.isEmpty())
					targetRoom = "Security Checkpoint";
				String finalTargetRoom = targetRoom;
				rooms.entrySet().stream()
						.filter(e -> e.getValue().name.equals(finalTargetRoom))
						.map(Map.Entry::getKey)
						.flatMapToInt(String::chars)
						.forEach(directions::add);
			}
			
			@Override
			public String nextCommands(String output) {
				if (directions.isEmpty())
					return null;
				Direction direction = Direction.parseCompassLetter((char) (int) directions.poll());
				currentPath.append(direction.compassLetter());
				return direction.getCompassName();
			}
		}
		
		class BruteForceMacro implements Macro {
			static final int STATE_NONE = 0;
			static final int STATE_PICKING = 1;
			static final int STATE_WALKING = 2;
			int state = STATE_NONE;
			
			Direction targetDirection;
			List<String> allItems;
			Set<String> currentItems;
			
			int variant = 0;
			Set<String> currentAttemptItems;
			
			// TODO нужно проверять где сейчас находится
			
			@Override
			public String nextCommands(String output) {
				if (targetDirection == null) {
					Direction back = Direction.parseCompassLetter(currentPath.charAt(currentPath.length() - 1)).reverse();
					targetDirection = rooms.get(currentPath.toString()).doors.stream()
							.filter(d -> d != back)
							.findAny()
							.orElseThrow();
				}
				if (allItems == null) {
					if (!output.contains("Items in your inventory"))
						return "inv";
					allItems = Arrays.stream(output.split("\n"))
							.filter(s -> s.startsWith("- "))
							.map(s -> s.substring(2))
							.toList();
					currentItems = new HashSet<>(allItems);
				}
				
				while (true) {
					if (state == STATE_PICKING) {
						for (String item : allItems) {
							if (currentItems.contains(item) && !currentAttemptItems.contains(item)) {
								currentItems.remove(item);
								return "drop " + item;
							}
							if (!currentItems.contains(item) && currentAttemptItems.contains(item)) {
								currentItems.add(item);
								return "take " + item;
							}
						}
						currentAttemptItems = null;
						state = STATE_WALKING;
					}
					
					if (state == STATE_WALKING) {
						if (!output.contains("you are ejected back to the checkpoint"))
							return targetDirection.getCompassName();
						state = STATE_NONE;
					}
					
					if (currentAttemptItems == null) {
						variant++;
						if (variant > 1 << allItems.size())
							return null;
						
						currentAttemptItems = itemsVariant(variant);
						state = STATE_PICKING;
					}
				}
			}
			
			Set<String> itemsVariant(int variant) {
				Set<String> itemsVariant = new HashSet<>();
				int index = 0;
				for (String item : allItems) {
					if ((variant & 1 << index) > 0)
						itemsVariant.add(item);
					index++;
				}
				return itemsVariant;
			}
		}
	}
}