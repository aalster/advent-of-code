package org.advent.year2021.day16;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day16 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day16.class, "input.txt");
		List<String> lines = new ArrayList<>();
		while (input.hasNext()) {
			lines.add(input.nextLine());
		}
		
		System.out.println("Answer 1: " + lines.stream().map(Day16::part1).map(String::valueOf).collect(Collectors.joining(", ")));
		System.out.println("Answer 2: " + lines.stream().map(Day16::part2).map(String::valueOf).collect(Collectors.joining(", ")));
	}
	
	private static long part1(String line) {
		BitsReader reader = new BitsReader(line);
		Packet packet = Packet.read(reader);
		return packet.versionsSum();
	}
	
	private static long part2(String line) {
		BitsReader reader = new BitsReader(line);
		Packet packet = Packet.read(reader);
		return packet.value();
	}
	
	@RequiredArgsConstructor
	private static abstract class Packet {
		public final int version;
		public final int type;
		public final int size;
		
		static Packet read(BitsReader reader) {
			int version = reader.nextBits(3);
			int type = reader.nextBits(3);
			return type == 4 ? LiteralValue.read(version, type, reader) : OperatorPacket.read(version, type, reader);
		}
		
		abstract long value();
		abstract long versionsSum();
	}
	
	private static class LiteralValue extends Packet {
		public final long value;
		
		public LiteralValue(int version, int type, int size, long value) {
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
	
	private static class OperatorPacket extends Packet {
		private final List<Packet> children;
		
		public OperatorPacket(int version, int type, int size, List<Packet> children) {
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
	private static class BitsReader {
		private String data;
		private int current;
		private int bitsLeft = 0;
		
		public BitsReader(String data) {
			this.data = data;
		}
		
		public int nextBit() {
			if (bitsLeft <= 0) {
				current = Integer.parseInt(data.substring(0, 1), 16);
				data = data.substring(1);
				bitsLeft = 4;
			}
			bitsLeft--;
			return (current >> bitsLeft) % 2 > 0 ? 1 : 0;
		}
		
		public int nextBits(int count) {
			int result = 0;
			while (count > 0) {
				result = (result << 1) + nextBit();
				count--;
			}
			return result;
		}
	}
}