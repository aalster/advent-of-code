package org.advent.year2018.day19;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day19 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day19()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 6, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("input.txt", 1836, 18992556)
		);
	}
	
	int ip;
	Operation[] operations;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		ip = Integer.parseInt(input.nextLine().replace("#ip ", ""));
		operations = Utils.readLines(input).stream().map(Operation::parse).toArray(Operation[]::new);
	}
	
	@Override
	public Object part1() {
		return solve(0);
	}
	
	@Override
	public Object part2() {
		return solve(1);
	}
	
	int solve(int reg0Initial) {
		int[] registers = new int[6];
		registers[0] = reg0Initial;
		
		for (; registers[ip] < operations.length; registers[ip]++) {
			
			// С индекса 1 начинается перебор множителей.
			// У разных инпутов отличается регистр с числом для подсчета, но оно всегда наибольшее.
			// Так совпало, что алгоритм работает и для example.txt
			if (registers[ip] == 1)
				return factorsSum(Arrays.stream(registers).max().orElseThrow());
			
			Operation op = operations[registers[ip]];
			int result = switch (op.name) {
				case "addr" -> registers[op.left] + registers[op.right];
				case "addi" -> registers[op.left] + op.right;
				case "mulr" -> registers[op.left] * registers[op.right];
				case "muli" -> registers[op.left] * op.right;
				case "banr" -> registers[op.left] & registers[op.right];
				case "bani" -> registers[op.left] & op.right;
				case "borr" -> registers[op.left] | registers[op.right];
				case "bori" -> registers[op.left] | op.right;
				case "setr" -> registers[op.left];
				case "seti" -> op.left;
				case "gtir" -> op.left > registers[op.right] ? 1 : 0;
				case "gtri" -> registers[op.left] > op.right ? 1 : 0;
				case "gtrr" -> registers[op.left] > registers[op.right] ? 1 : 0;
				case "eqir" -> op.left == registers[op.right] ? 1 : 0;
				case "eqri" -> registers[op.left] == op.right ? 1 : 0;
				case "eqrr" -> registers[op.left] == registers[op.right] ? 1 : 0;
				default -> throw new IllegalArgumentException("Unknown opcode " + op.name);
			};
			registers[op.target] = result;
		}
		return registers[0];
	}
	
	int factorsSum(int number) {
		int result = 0;
		int sqrtB = (int) Math.sqrt(number);
		for (int factor = 1; factor < sqrtB; factor++)
			if (number % factor == 0)
				result += factor + number / factor;
		
		return result;
	}
	
	record Operation(String name, int left, int right, int target) {
		
		static Operation parse(String line) {
			String[] split = line.split(" ");
			return new Operation(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
		}
	}
}

/*
		[a, b, c, d, e, index]
		 0  1  2  3  4  5
		
		#ip 5
	0	addi 5 16 5       index = 16
	1	seti 1 0 3        d = 1
	2	seti 1 2 2        c = 1
	3	mulr 3 2 4        e = d * c
	4	eqrr 4 1 4        e = e == b
	5	addr 4 5 5        index = e + index
	6	addi 5 1 5        index = index + 1
	7	addr 3 0 0        a = d + a
	8	addi 2 1 2        c = c + 1
	9	gtrr 2 1 4        e = c > b
	10	addr 5 4 5        index = index + e
	11	seti 2 7 5        index = 2
	12	addi 3 1 3        d = d + 1
	13	gtrr 3 1 4        e = d > b
	14	addr 4 5 5        index = e + index
	15	seti 1 3 5        index = 1
	16	mulr 5 5 5        index = index * index
	17	addi 1 2 1        b = b + 2
	18	mulr 1 1 1        b = b * b
	19	mulr 5 1 1        b = index * b
	20	muli 1 11 1       b = b * 11
	21	addi 4 7 4        e = e + 7
	22	mulr 4 5 4        e = e * index
	23	addi 4 20 4       e = e + 20
	24	addr 1 4 1        b = b + e
	25	addr 5 0 5        index = index + a
	26	seti 0 4 5        index = 0
	27	setr 5 9 4        e = index
	28	mulr 4 5 4        e = e * index
	29	addr 5 4 4        e = index + e
	30	mulr 5 4 4        e = index * e
	31	muli 4 14 4       e = e * 14
	32	mulr 4 5 4        e = e * index
	33	addr 1 4 1        b = b + e
	34	seti 0 2 0        a = 0
	35	seti 0 5 5        index = 0
	
	
	Убрали прыжки по индексам:
	
	17	addi 1 2 1        b = b + 2
	18	mulr 1 1 1        b = b * b
	19	mulr 5 1 1        b = 19 * b
	20	muli 1 11 1       b = b * 11
	21	addi 4 7 4        e = e + 7
	22	mulr 4 5 4        e = e * 22
	23	addi 4 20 4       e = e + 20
	24	addr 1 4 1        b = b + e
	25	addr 5 0 5        if (a == 1) {
	27	setr 5 9 4            e = 27
	28	mulr 4 5 4            e = e * 28
	29	addr 5 4 4            e = 29 + e
	30	mulr 5 4 4            e = 30 * e
	31	muli 4 14 4           e = e * 14
	32	mulr 4 5 4            e = e * 32
	33	addr 1 4 1            b = b + e
	34	seti 0 2 0            a = 0
	35	seti 0 5 5        }
	1	seti 1 0 3        d = 1
	2	seti 1 2 2        while (true) { c = 1
	3	mulr 3 2 4            while (true) { e = d * c
	4	eqrr 4 1 4                e = e == b
	5	addr 4 5 5                if (e == 0) {
	6	addi 5 1 5                    a = d + a
	7	addr 3 0 0                }
	8	addi 2 1 2                c = c + 1
	9	gtrr 2 1 4                e = c > b
	10	addr 5 4 5                if (e == 1) break;
	11	seti 2 7 5            }
	12	addi 3 1 3            d = d + 1
	13	gtrr 3 1 4            e = d > b
	14	addr 4 5 5            if (e == 1) break;
	15	seti 1 3 5        }
	
	
	Сокращенный код:

	int b = 2 * 2 * 19 * 11 + 7 * 22 + 20;
	if (reg0Initial == 1)
	    b += (27 * 28 + 29) * 30 * 14 * 32;

	int a = 0;
	int sqrtB = (int) Math.sqrt(b);
	for (int d = 1; d < sqrtB; d++)
		if (b % d == 0)
			a += d + b / d;

	return a;
*/