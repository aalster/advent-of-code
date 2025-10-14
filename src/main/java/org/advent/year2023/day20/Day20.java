package org.advent.year2023.day20;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.advent.common.NumbersAdventUtils;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day20 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day20()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 32000000, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 11687500, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 861743850, 247023644760071L)
		);
	}
	
	Map<String, Module> modules;
	
	@Override
	public void prepare(String file) {
		List<String> lines = Utils.readLines(Utils.scanFileNearClass(getClass(), file));
		modules = new HashMap<>();
		for (String line : lines) {
			Module module = Module.parse(line);
			modules.put(module.getName(), module);
		}
		Module button = new ButtonModule("button", List.of("broadcaster"));
		Module output = new OutputModule();
		modules.put(button.getName(), button);
		modules.put(output.getName(), output);
		modules.values().forEach(m -> m.init(modules.values()));
	}
	
	@Override
	public Object part1() {
		long lowPulses = 0;
		long highPulses = 0;
		
		for (int i = 0; i < 1000; i++) {
			List<Pulse> pulses = List.of(new Pulse("button", false, "init"));
			while (!pulses.isEmpty()) {
				pulses = pulses.stream()
						.flatMap(p -> modules.getOrDefault(p.destination(), OutputModule.INSTANCE).nextPulses(p))
						.toList();
				
				Map<Boolean, Long> nextPulsesCount = pulses.stream().collect(Collectors.groupingBy(Pulse::high, Collectors.counting()));
				lowPulses += nextPulsesCount.getOrDefault(false, 0L);
				highPulses += nextPulsesCount.getOrDefault(true, 0L);
			}
		}
		return lowPulses * highPulses;
	}
	
	@Override
	public Object part2() {
		ConjunctionModule endConjunction = modules.values().stream()
				.filter(m -> m instanceof ConjunctionModule)
				.filter(m -> m.getDestinations().contains("rx"))
				.findFirst()
				.map(m -> (ConjunctionModule) m)
				.orElseThrow();
		
		Map<String, Long> endConjunctionInputRepeats = new HashMap<>(modules.values().stream()
				.filter(m -> m.getDestinations().contains(endConjunction.getName()))
				.collect(Collectors.toMap(Module::getName, m -> 0L)));
		
		long n = 0;
		while (true) {
			n++;
			List<Pulse> pulses = List.of(new Pulse("button", false, "init"));
			while (!pulses.isEmpty()) {
				pulses = pulses.stream()
						.flatMap(p -> modules.getOrDefault(p.destination(), OutputModule.INSTANCE).nextPulses(p))
						.toList();
				
				for (Map.Entry<String, Boolean> e : endConjunction.lastSignals.entrySet()) {
					if (e.getValue() && endConjunctionInputRepeats.get(e.getKey()) == 0) {
						endConjunctionInputRepeats.put(e.getKey(), n);
						if (endConjunctionInputRepeats.values().stream().allMatch(r -> r > 0)) {
							return NumbersAdventUtils.lcmBig(endConjunctionInputRepeats.values());
						}
					}
				}
			}
			
			if (n % 100_000 == 0)
				System.out.println(n);
		}
	}
	
	@Data
	@RequiredArgsConstructor
	static abstract class Module {
		private final String name;
		private final ModuleType type;
		private final List<String> destinations;
		
		void init(Collection<Module> modules) {
		}
		
		Stream<Pulse> nextPulses(Pulse pulse) {
			Boolean outputHigh = nextPulse(pulse);
			if (outputHigh == null)
				return Stream.of();
			return destinations.stream()
					.map(d -> new Pulse(d, outputHigh, name));
//					.peek(System.out::println);
		}
		
		abstract Boolean nextPulse(Pulse pulse);
		
		static Module parse(String line) {
			ModuleType type = switch (line.charAt(0)) {
				case '%' -> ModuleType.FLIP_FLOP;
				case '&' -> ModuleType.CONJUNCTION;
				default -> ModuleType.BROADCASTER;
			};
			String[] split = StringUtils.splitByWholeSeparator(line, " -> ");
			String name = split[0].substring(type == ModuleType.BROADCASTER ? 0 : 1);
			List<String> destinations = Arrays.stream(StringUtils.splitByWholeSeparator(split[1], ", ")).toList();
			return switch (type) {
				case FLIP_FLOP -> new FlipFlopModule(name, destinations);
				case CONJUNCTION -> new ConjunctionModule(name, destinations);
				case BROADCASTER -> new BroadcasterModule(name, destinations);
				default -> throw new IllegalArgumentException("Bad type: " + type);
			};
		}
	}
	
	static class ButtonModule extends Module {
		
		public ButtonModule(String name, List<String> destinations) {
			super(name, ModuleType.BUTTON, destinations);
		}
		
		@Override
		Boolean nextPulse(Pulse pulse) {
			return false;
		}
	}
	
	static class OutputModule extends Module {
		static final OutputModule INSTANCE = new OutputModule();
		
		public OutputModule() {
			super("output", ModuleType.BUTTON, List.of());
		}
		
		@Override
		Boolean nextPulse(Pulse pulse) {
			return null;
		}
	}
	
	static class BroadcasterModule extends Module {
		
		public BroadcasterModule(String name, List<String> destinations) {
			super(name, ModuleType.BROADCASTER, destinations);
		}
		
		@Override
		Boolean nextPulse(Pulse pulse) {
			return pulse.high();
		}
	}
	
	static class FlipFlopModule extends Module {
		private boolean on = false;
		
		@Override
		Boolean nextPulse(Pulse pulse) {
			if (pulse.high())
				return null;
			on = !on;
			return on;
		}
		
		public FlipFlopModule(String name, List<String> destinations) {
			super(name, ModuleType.FLIP_FLOP, destinations);
		}
	}
	
	static class ConjunctionModule extends Module {
		private final Map<String, Boolean> lastSignals = new HashMap<>();
		
		@Override
		void init(Collection<Module> modules) {
			modules.stream()
					.filter(m -> m.getDestinations().contains(getName()))
					.forEach(m -> lastSignals.put(m.getName(), false));
		}
		
		@Override
		Boolean nextPulse(Pulse pulse) {
			lastSignals.put(pulse.caller(), pulse.high());
			Set<Boolean> values = new HashSet<>(lastSignals.values());
			return !(values.size() == 1 && values.iterator().next());
		}
		
		public ConjunctionModule(String name, List<String> destinations) {
			super(name, ModuleType.CONJUNCTION, destinations);
		}
	}
	
	record Pulse(String destination, boolean high, String caller) {
		@Override
		public String toString() {
			return caller + " -" + (high ? "high" : "low") + "-> " + destination;
		}
	}
	
	enum ModuleType {
		BUTTON, BROADCASTER, FLIP_FLOP, CONJUNCTION
	}
}