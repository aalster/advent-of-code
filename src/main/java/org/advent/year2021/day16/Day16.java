package org.advent.year2021.day16;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day16 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day16()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 16, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", 12, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example3.txt", 23, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example4.txt", 31, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example5.txt", ExpectedAnswers.IGNORE, 3),
				new ExpectedAnswers("example6.txt", ExpectedAnswers.IGNORE, 54),
				new ExpectedAnswers("example7.txt", ExpectedAnswers.IGNORE, 7),
				new ExpectedAnswers("example8.txt", ExpectedAnswers.IGNORE, 9),
				new ExpectedAnswers("example9.txt", ExpectedAnswers.IGNORE, 1),
				new ExpectedAnswers("example10.txt", ExpectedAnswers.IGNORE, 0),
				new ExpectedAnswers("example11.txt", ExpectedAnswers.IGNORE, 0),
				new ExpectedAnswers("example12.txt", ExpectedAnswers.IGNORE, 1),
				new ExpectedAnswers("input.txt", 929, 911945136934L)
		);
	}
	
	String line;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		line = input.nextLine();
	}
	
	@Override
	public Object part1() {
		return Packet.read(new BitsReader(line)).versionsSum();
	}
	
	@Override
	public Object part2() {
		return Packet.read(new BitsReader(line)).value();
	}
	
	@RequiredArgsConstructor
	static abstract class Packet {
		final int version;
		final int type;
		final int size;
		
		static Packet read(BitsReader reader) {
			int version = reader.nextBits(3);
			int type = reader.nextBits(3);
			return type == 4 ? LiteralValue.read(version, type, reader) : OperatorPacket.read(version, type, reader);
		}
		
		abstract long value();
		abstract long versionsSum();
	}
	
	static class LiteralValue extends Packet {
		final long value;
		
		LiteralValue(int version, int type, int size, long value) {
			super(version, type, size);
			this.value = value;
		}
		
		@Override
		long value() {
			return value;
		}
		
		@Override
		long versionsSum() {
			return version;
		}
		
		static LiteralValue read(int version, int type, BitsReader reader) {
			int size = 6;
			long value = 0;
			while (true) {
				int lastPartBit = reader.nextBit();
				value = (value << 4) + reader.nextBits(4);
				size += 5;
				if (lastPartBit == 0)
					break;
			}
			return new LiteralValue(version, type, size, value);
		}
	}
	
	static class OperatorPacket extends Packet {
		final List<Packet> children;
		
		OperatorPacket(int version, int type, int size, List<Packet> children) {
			super(version, type, size);
			this.children = children;
		}
		
		@Override
		long value() {
			return switch (type) {
				case 0 -> children.stream().mapToLong(Packet::value).sum();
				case 1 -> children.stream().mapToLong(Packet::value).reduce(1, (l, r) -> l * r);
				case 2 -> children.stream().mapToLong(Packet::value).min().orElseThrow();
				case 3 -> children.stream().mapToLong(Packet::value).max().orElseThrow();
				case 5 -> children.getFirst().value() > children.getLast().value() ? 1 : 0;
				case 6 -> children.getFirst().value() < children.getLast().value() ? 1 : 0;
				case 7 -> children.getFirst().value() == children.getLast().value() ? 1 : 0;
				default -> throw new IllegalStateException();
			};
		}
		
		@Override
		long versionsSum() {
			return version + children.stream().mapToLong(Packet::versionsSum).sum();
		}
		
		static OperatorPacket read(int version, int type, BitsReader reader) {
			int lengthType = reader.nextBit();
			int size = 7;
			List<Packet> children = new ArrayList<>();
			if (lengthType == 0) {
				int childrenSize = reader.nextBits(15);
				size += 15;
				while (childrenSize > 0) {
					Packet child = Packet.read(reader);
					childrenSize -= child.size;
					size += child.size;
					children.add(child);
				}
			} else {
				int childrenCount = reader.nextBits(11);
				size += 11;
				while (childrenCount > 0) {
					Packet child = Packet.read(reader);
					childrenCount--;
					size += child.size;
					children.add(child);
				}
			}
			return new OperatorPacket(version, type, size, children);
		}
	}
	
	@Data
	static class BitsReader {
		String data;
		int current;
		int bitsLeft = 0;
		
		BitsReader(String data) {
			this.data = data;
		}
		
		int nextBit() {
			if (bitsLeft <= 0) {
				current = Integer.parseInt(data.substring(0, 1), 16);
				data = data.substring(1);
				bitsLeft = 4;
			}
			bitsLeft--;
			return (current >> bitsLeft) % 2;
		}
		
		int nextBits(int count) {
			int result = 0;
			while (count > 0) {
				result = (result << 1) + nextBit();
				count--;
			}
			return result;
		}
	}
}