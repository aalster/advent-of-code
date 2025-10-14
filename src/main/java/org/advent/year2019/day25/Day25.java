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
	
	static final boolean silent = true;
//	static final String script = "";
	static final String script = """
				macro explore collect
				macro goto Security Checkpoint
				macro break
				""";
	
	long[] program;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		program = IntcodeComputer2.parseProgram(input.nextLine());
	}
	
	@Override
	public Object part1() {
		OutputConsumer.BufferingTextOutputConsumer outputBuffer = OutputConsumer.bufferingText();
		OutputConsumer output = OutputConsumer.combine(outputBuffer, OutputConsumer.printer(silent));
		
		InputProvider input = InputProvider.console();
		input = new MacroInputProvider(InputProvider.combine(InputProvider.buffering(script), input), outputBuffer, silent);
		
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
	
	record Room(String name, String description, List<Direction> doors, List<String> items, List<Direction> path) {
		
		static Room parse(String description, List<Direction> path) {
			String name = parseName(description);
			
			List<Direction> doors = new ArrayList<>();
			List<String> items = new ArrayList<>();
			
			Iterator<String> iterator = List.of(description.split("\n")).iterator();
			while (iterator.hasNext()) {
				String line = iterator.next();
				if (line.equals("Doors here lead:")) {
					while (iterator.hasNext()) {
						String door = iterator.next();
						if (!door.startsWith("- "))
							break;
						char directionChar = StringUtils.removeStart(door, "- ").charAt(0);
						doors.add(Direction.parseCompassLetter(Character.toUpperCase(directionChar)));
					}
				}
				if (line.equals("Items here:")) {
					while (iterator.hasNext()) {
						String item = iterator.next();
						if (!item.startsWith("- "))
							break;
						items.add(StringUtils.removeStart(item, "- "));
					}
				}
			}
			return new Room(name, description, doors, items, new ArrayList<>(path));
		}
		
		static String parseName(String description) {
			return description.split("==")[1].trim();
		}
		
		@Override
		public String toString() {
			return description.replace("Command?", "").trim();
		}
	}
	
	static class Ship {
		Map<String, Room> roomsByName = new LinkedHashMap<>();
		String currentRoomDescription;
		String currentRoomName;
		Set<String> currentItems = new HashSet<>();
		
		void onOutput(String output) {
			if (output.startsWith("==")) {
				currentRoomDescription = output;
				currentRoomName = Room.parseName(output);
			} else if (output.startsWith("You take the ")) {
				currentItems.add(StringUtils.removeStart(StringUtils.removeEnd(output, "."), "You take the "));
			} else if (output.startsWith("You drop the ")) {
				currentItems.remove(StringUtils.removeStart(StringUtils.removeEnd(output, "."), "You drop the "));
			}
		}
	}
	
	@RequiredArgsConstructor
	static class MacroInputProvider implements InputProvider {
		final InputProvider userInputProvider;
		final BufferingInputProvider inputBuffer = InputProvider.buffering();
		final OutputConsumer.BufferingTextOutputConsumer outputBuffer;
		final boolean silent;
		final Ship ship = new Ship();
		Macro macro;
		
		@Override
		public boolean hasNext() {
			return true;
		}
		
		@Override
		public long nextInput() {
			if (!inputBuffer.hasNext()) {
				String output = outputBuffer.read().replace("Command?", "").trim();
				inputBuffer.append(nextInput(output) + "\n");
			}
			return inputBuffer.nextInput();
		}
		
		public String nextInput(String output) {
			ship.onOutput(output);
			
			while (true) {
				if (macro != null) {
					try {
						String nextCommand = macro.nextCommands(ship, output);
						if (nextCommand == null) {
							macro = null;
							continue;
						}
						if (!silent)
							System.out.println("<< " + nextCommand);
						return nextCommand;
					} catch (MacroException e) {
						macro = null;
						if (!silent)
							System.out.println("Error: " + e.getMessage());
						continue;
					}
				}
				
				String userInput = userInputProvider.nextLine();
				
				if (userInput.startsWith("macro ")) {
					macro = Macro.parse(StringUtils.removeStart(userInput, "macro "));
					continue;
				}
				if (userInput.equals("exit"))
					throw new RuntimeException("Exit");
				return userInput;
			}
		}
	}
	
	static class MacroException extends RuntimeException {
		public MacroException(String message) {
			super(message);
		}
	}
	
	interface Macro {
		String nextCommands(Ship ship, String output);
		
		static Macro parse(String command) {
			String[] split = command.split(" ", 2);
			String params = split.length > 1 ? split[1] : "";
			return switch (split[0]) {
				case "explore" -> new ExploreMacro(params);
				case "goto" -> new GotoMacro(params);
				case "break" -> new BreakMacro();
				default -> throw new RuntimeException("Unknown macro: " + command);
			};
		}
	}
	
	static class ExploreMacro implements Macro {
		static final Set<String> ignoredItems = Set.of("escape pod", "infinite loop",
				"molten lava", "giant electromagnet", "photons");
		static final Set<String> ignoredRooms = Set.of("Security Checkpoint");
		final boolean collect;
		Set<Pair<String, Direction>> checkedDirections = new HashSet<>();
		List<Direction> currentPath;
		
		ExploreMacro(String params) {
			collect = "collect".equals(params);
		}
		
		@Override
		public String nextCommands(Ship ship, String output) {
			if (currentPath == null) {
				if (ignoredItems.contains(ship.currentRoomName))
					throw new MacroException("Can't explore from " + ship.currentRoomName);
				currentPath = new ArrayList<>();
			}
			
			Room currentRoom = ship.roomsByName.computeIfAbsent(ship.currentRoomName,
					k -> Room.parse(ship.currentRoomDescription, currentPath));
			
			if (collect) {
				Optional<String> item = currentRoom.items.stream()
						.filter(i -> !ignoredItems.contains(i))
						.filter(i -> !ship.currentItems.contains(i))
						.findAny();
				if (item.isPresent())
					return "take " + item.get();
			}
			
			Direction back = currentPath.isEmpty() ? null : currentPath.getLast().reverse();
			
			if (!ignoredRooms.contains(currentRoom.name)) {
				Optional<Direction> nextDirection = currentRoom.doors.stream()
						.filter(d -> d != back)
						.filter(d -> !checkedDirections.contains(Pair.of(currentRoom.name, d)))
						.findAny();
				
				if (nextDirection.isPresent()) {
					Direction direction = nextDirection.get();
					currentPath.add(direction);
					checkedDirections.add(Pair.of(currentRoom.name, direction));
					return direction.getCompassName();
				}
			}
			if (back != null) {
				currentPath.removeLast();
				return back.getCompassName();
			}
			return null;
		}
	}
	
	@RequiredArgsConstructor
	static class GotoMacro implements Macro {
		final String targetRoomName;
		Queue<Direction> directions;
		
		@Override
		public String nextCommands(Ship ship, String output) {
			if (directions == null) {
				Room currentRoom = ship.roomsByName.get(ship.currentRoomName);
				Room targetRoom = ship.roomsByName.get(targetRoomName);
				if (currentRoom == null || targetRoom == null)
					throw new MacroException("Ship not explored");
				directions = relativePath(currentRoom.path, targetRoom.path);
			}
			
			if (directions.isEmpty())
				return null;
			
			return directions.poll().getCompassName();
		}
		
		Queue<Direction> relativePath(List<Direction> c, List<Direction> t) {
			List<Direction> current = new LinkedList<>(c);
			LinkedList<Direction> target = new LinkedList<>(t);
			while (!current.isEmpty() && !target.isEmpty() && current.getFirst() == target.getFirst()) {
				current.removeFirst();
				target.removeFirst();
			}
			while (!current.isEmpty())
				target.addFirst(current.removeFirst().reverse());
			return target;
		}
	}
	
	static class BreakMacro implements Macro {
		static final int STATE_NONE = 0;
		static final int STATE_PICKING = 1;
		static final int STATE_WALKING = 2;
		int state = STATE_NONE;
		
		Direction targetDirection;
		List<String> availableItems;
		
		int variant = 0;
		Set<String> currentVariantItems;
		
		@Override
		public String nextCommands(Ship ship, String output) {
			if (targetDirection == null) {
				if (!"Security Checkpoint".equals(ship.currentRoomName))
					throw new MacroException("Not a Security Checkpoint");
				
				Room currentRoom = ship.roomsByName.get(ship.currentRoomName);
				if (currentRoom == null)
					throw new MacroException("Ship not explored");
				
				Direction back = currentRoom.path.getLast().reverse();
				targetDirection = currentRoom.doors.stream()
						.filter(d -> d != back)
						.findAny()
						.orElseThrow();
			}
			if (availableItems == null) {
				if (ship.currentItems.isEmpty())
					throw new MacroException("No items in inventory");
				availableItems = new ArrayList<>(ship.currentItems);
			}
			
			while (true) {
				if (state == STATE_PICKING) {
					for (String item : availableItems) {
						boolean haveItem = ship.currentItems.contains(item);
						boolean shouldHaveItem = currentVariantItems.contains(item);
						if (haveItem != shouldHaveItem)
							return (shouldHaveItem ? "take " : "drop ") + item;
					}
					currentVariantItems = null;
					state = STATE_WALKING;
				}
				
				if (state == STATE_WALKING) {
					if (!output.contains("you are ejected back to the checkpoint"))
						return targetDirection.getCompassName();
					state = STATE_NONE;
				}
				
				if (currentVariantItems == null) {
					currentVariantItems = nextItemsVariant();
					if (currentVariantItems == null)
						throw new MacroException("Combination not found");
					state = STATE_PICKING;
				}
			}
		}
		
		Set<String> nextItemsVariant() {
			variant++;
			if (variant > 1 << availableItems.size())
				return null;
			
			Set<String> itemsVariant = new HashSet<>();
			int index = 0;
			for (String item : availableItems) {
				if ((variant & 1 << index) > 0)
					itemsVariant.add(item);
				index++;
			}
			return itemsVariant;
		}
	}
}