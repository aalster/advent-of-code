package org.advent.year2019.day23;

import lombok.RequiredArgsConstructor;
import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.IntStream;

public class Day23 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day23()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 18966, 14370)
		);
	}
	
	IntcodeComputer computer;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		computer = IntcodeComputer.parse(input.nextLine());
	}
	
	@Override
	public Object part1() {
		Map<Integer, Queue<Long>> queues = new HashMap<>();
		
		List<NetworkComputer> computers = IntStream.range(0, 50)
				.mapToObj(i -> new NetworkComputer(i, computer.copy()))
				.toList();
		
		computers.forEach(c -> queues.computeIfAbsent(c.index, k -> new LinkedList<>()).add((long) c.index));
		
		while (true) {
			for (NetworkComputer networkComputer : computers) {
				Queue<Long> queue = queues.get(networkComputer.index);
				InputProvider provider = queue.isEmpty() ? InputProvider.constant(-1) : new QueueInputProvider(queue);
				Pair<Integer, Message> message = networkComputer.run2(provider);
				if (message == null)
					continue;
				if (message.left() == 255)
					return message.right().y;
				Queue<Long> targetQueue = queues.computeIfAbsent(message.left(), k -> new LinkedList<>());
				targetQueue.add(message.right().x);
				targetQueue.add(message.right().y);
			}
		}
	}
	
	@Override
	public Object part2() {
		Map<Integer, Queue<Long>> queues = new HashMap<>();

		List<NetworkComputer> computers = IntStream.range(0, 50)
				.mapToObj(i -> new NetworkComputer(i, computer.copy()))
				.toList();

		computers.forEach(c -> queues.computeIfAbsent(c.index, k -> new LinkedList<>()).add((long) c.index));
		
		Message natMessage = null;
		Set<Long> natYs = new HashSet<>();
		
		int emptyCycles = 0;
		
		while (true) {
			boolean messageSent = false;
			for (NetworkComputer networkComputer : computers) {
				Queue<Long> queue = queues.get(networkComputer.index);
				InputProvider provider = queue.isEmpty() ? InputProvider.constant(-1) : new QueueInputProvider(queue);
				Pair<Integer, Message> message = networkComputer.run2(provider);
				if (message == null)
					continue;
				if (message.left() == 255) {
					natMessage = message.right();
					continue;
				}
				Queue<Long> targetQueue = queues.computeIfAbsent(message.left(), k -> new LinkedList<>());
				targetQueue.add(message.right().x);
				targetQueue.add(message.right().y);
				messageSent = true;
			}
			
			emptyCycles = messageSent ? 0 : emptyCycles + 1;
			
			if (emptyCycles > 10 && natMessage != null) {
				Queue<Long> targetQueue = queues.computeIfAbsent(0, k -> new LinkedList<>());
				targetQueue.add(natMessage.x);
				targetQueue.add(natMessage.y);
				if (!natYs.add(natMessage.y)) {
					return natMessage.y;
				}
				emptyCycles = 0;
				natMessage = null;
			}
		}
	}
	
	@RequiredArgsConstructor
	static class QueueInputProvider implements InputProvider {
		private final Queue<Long> queue;
		
		@Override
		public boolean hasNext() {
			return !queue.isEmpty();
		}
		
		@Override
		public long nextInput() {
			if (queue.isEmpty())
				throw new IllegalStateException("No more input");
			return queue.poll();
		}
	}
	
	record Message(long x, long y) {
	}
	
	record NetworkComputer(int index, IntcodeComputer computer, Queue<Long> outputQueue) {
		
		NetworkComputer(int index, IntcodeComputer computer) {
			this(index, computer, new LinkedList<>());
		}
		
		Pair<Integer, Message> run2(InputProvider provider) {
			Long output = computer.runUntilOutput(provider);
			while (output != null) {
				outputQueue.add(output);
				output = computer.runUntilOutput(provider);
			}
			if (outputQueue.size() >= 3) {
				return Pair.of(outputQueue.poll().intValue(), new Message(outputQueue.poll(), outputQueue.poll()));
			}
			return null;
		}
	}
}