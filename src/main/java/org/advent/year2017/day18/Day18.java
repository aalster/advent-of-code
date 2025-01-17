package org.advent.year2017.day18;

import lombok.Data;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

public class Day18 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day18()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 4, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 3),
				new ExpectedAnswers("input.txt", 7071, 8001)
		);
	}
	
	String[][] operations;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		operations = Utils.readLines(input).stream()
				.map(line -> Arrays.stream(line.split(" ")).toArray(String[]::new))
				.toArray(String[][]::new);
	}
	
	@Override
	public Object part1() {
		Program program = new Program(operations, 0);
		LinkedList<Long> queue = new LinkedList<>();
		program.setPartnerQueue(queue);
		program.executeUntilWaiting();
		return queue.getLast();
	}
	
	@Override
	public Object part2() {
		Program first = new Program(operations, 0);
		Program second = new Program(operations, 1);
		first.setPartnerQueue(second.queue);
		second.setPartnerQueue(first.queue);
		
		List<Program> programs = new ArrayList<>(List.of(first, second));
		
		main: while (!programs.isEmpty()) {
			Iterator<Program> iterator = programs.iterator();
			while (iterator.hasNext()) {
				Program program = iterator.next();
				ProgramState state = program.executeUntilWaiting();
				if (state == ProgramState.FINISHED)
					iterator.remove();
				else if (state == ProgramState.DEADLOCK)
					break main;
			}
		}
		return second.sentCount;
	}
	
	@Data
	static class Program {
		final String[][] operations;
		final Map<String, Long> registers = new HashMap<>();
		final Queue<Long> queue = new LinkedList<>();
		Queue<Long> partnerQueue;
		int sentCount = 0;
		int index = 0;
		boolean finished = false;
		
		Program(String[][] operations, long id) {
			this.operations = operations;
			this.registers.put("p", id);
		}
		
		long get(String name) {
			return Character.isLetter(name.charAt(0)) ? registers.getOrDefault(name, 0L) : Long.parseLong(name);
		}
		
		void set(String name, long value) {
			registers.put(name, value);
		}
		
		void send(long value) {
			partnerQueue.add(value);
			sentCount++;
		}
		
		ProgramState executeUntilWaiting() {
			if (!finished) {
				int iterations = 0;
				for (; index < operations.length; index++) {
					String[] operation = operations[index];
					switch (operation[0]) {
						case "set" -> set(operation[1], get(operation[2]));
						case "add" -> set(operation[1], get(operation[1]) + get(operation[2]));
						case "mul" -> set(operation[1], get(operation[1]) * get(operation[2]));
						case "mod" -> set(operation[1], get(operation[1]) % get(operation[2]));
						case "snd" -> send(get(operation[1]));
						case "rcv" -> {
							Long value = queue.poll();
							if (value == null)
								return iterations == 0 ? ProgramState.DEADLOCK : ProgramState.WAITING;
							set(operation[1], value);
						}
						case "jgz" -> {
							if (get(operation[1]) > 0)
								index += (int) get(operation[2]) - 1;
						}
						default -> throw new IllegalStateException("Unexpected value: " + operation[0]);
					}
					iterations++;
				}
			}
			
			finished = true;
			return ProgramState.FINISHED;
		}
	}
	
	enum ProgramState {
		FINISHED, WAITING, DEADLOCK
	}
}