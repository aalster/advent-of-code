package org.advent.year2019.day23;

import lombok.RequiredArgsConstructor;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.advent.year2019.intcode_computer.InputProvider;
import org.advent.year2019.intcode_computer.IntcodeComputer2;
import org.advent.year2019.intcode_computer.OutputConsumer;

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
	
	long[] program;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		program = IntcodeComputer2.parseProgram(input.nextLine());
	}
	
	@Override
	public Object part1() {
		Network network = new Network();
		List<NetworkComputer> computers = IntStream.range(0, 50)
				.mapToObj(i -> new NetworkComputer(network, i, program))
				.toList();
		
		while (network.natMessage == null)
			for (NetworkComputer networkComputer : computers)
				networkComputer.run();
		return network.natMessage.y;
	}
	
	@Override
	public Object part2() {
		int emptyCyclesCount = 2;
		Network network = new Network();
		List<NetworkComputer> computers = IntStream.range(0, 50)
				.mapToObj(i -> new NetworkComputer(network, i, program))
				.toList();
		
		while (network.firstDuplicatedNatY == null) {
			for (NetworkComputer networkComputer : computers)
				networkComputer.run();
			network.onCycle(emptyCyclesCount);
		}
		return network.firstDuplicatedNatY;
	}
	
	record Message(long x, long y) {
	}
	
	static class Network {
		final Map<Integer, Queue<Long>> queues = new HashMap<>();
		Message natMessage;
		Set<Long> natYs = new HashSet<>();
		Long firstDuplicatedNatY;
		int messagesSent = 0;
		int emptyCycles = 0;
		
		Queue<Long> queue(int address) {
			return queues.computeIfAbsent(address, k -> new LinkedList<>());
		}
		
		void send(int address, Message message) {
			if (address == 255) {
				natMessage = message;
				return;
			}
			Queue<Long> queue = queue(address);
			queue.add(message.x);
			queue.add(message.y);
			messagesSent++;
		}
		
		long[] readAllMessages(int address) {
			Queue<Long> queue = queue(address);
			if (queue.isEmpty())
				return new long[] {-1};
			long[] messages = queue.stream().mapToLong(Long::longValue).toArray();
			queue.clear();
			return messages;
		}
		
		void onCycle(int emptyCyclesCount) {
			if (messagesSent == 0) {
				emptyCycles++;
				if (emptyCycles >= emptyCyclesCount && natMessage != null) {
					send(0, natMessage);
					if (!natYs.add(natMessage.y))
						firstDuplicatedNatY = natMessage.y;
					
					natMessage = null;
					emptyCycles = 0;
				}
			}
			messagesSent = 0;
		}
	}
	
	@RequiredArgsConstructor
	static class NetworkOutputConsumer extends OutputConsumer.BufferingOutputConsumer {
		final Network network;
		
		@Override
		public void accept(long output) {
			super.accept(output);
			if (buffer.size() >= 3)
				network.send((int) readNext(), new Message(readNext(), readNext()));
		}
	}
	
	static class NetworkComputer {
		final Network network;
		final int index;
		final InputProvider.BufferingInputProvider input;
		final NetworkOutputConsumer output;
		final IntcodeComputer2 computer;
		
		NetworkComputer(Network network, int index, long[] program) {
			this.network = network;
			this.index = index;
			this.input = InputProvider.buffering().append(index);
			this.output = new NetworkOutputConsumer(network);
			this.computer = new IntcodeComputer2(program, input, output);
		}
		
		void run() {
			input.append(network.readAllMessages(index));
			computer.run();
		}
	}
}