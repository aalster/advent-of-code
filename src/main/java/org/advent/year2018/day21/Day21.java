package org.advent.year2018.day21;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Day21 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day21()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 6778585, 6534225)
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
		return runIterationOptimized(0);
	}
	
	@Override
	public Object part2() {
		Set<Integer> results = new HashSet<>();
		int prevResult = 0;
		while (true) {
			int result = runIterationOptimized(prevResult);
			if (!results.add(result))
				return prevResult;
			prevResult = result;
		}
	}
	
	private int runIterationOptimized(int prevResult) {
		int start = operations[7].left;
		int factor = operations[11].right;
		
		int c = prevResult | 0x10000;
		int result = start;
		while (true) {
			result += c & 0xff;
			result &= 0xffffff;
			result *= factor;
			result &= 0xffffff;
			
			if (c < 256)
				break;
			c /= 256;
		}
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

		#ip 3
	0	seti 123 0 5            [5] = 123
	1	bani 5 456 5            [5] = [5] & 456
	2	eqri 5 72 5             [5] = [5] == 72
	3	addr 5 3 3              [3] = [5] + [3]
	4	seti 0 0 3              [3] = 0
	5	seti 0 5 5              [5] = 0
	6	bori 5 65536 2          [2] = [5] | 65536
	7	seti 10362650 3 5       [5] = 10362650
	8	bani 2 255 4            [4] = [2] & 255
	9	addr 5 4 5              [5] = [5] + [4]
	10	bani 5 16777215 5       [5] = [5] & 16777215
	11	muli 5 65899 5          [5] = [5] * 65899
	12	bani 5 16777215 5       [5] = [5] & 16777215
	13	gtir 256 2 4            [4] = 256 > [2]
	14	addr 4 3 3              [3] = [4] + [3]
	15	addi 3 1 3              [3] = [3] + 1
	16	seti 27 4 3             [3] = 27
	17	seti 0 3 4              [4] = 0
	18	addi 4 1 1              [1] = [4] + 1
	19	muli 1 256 1            [1] = [1] * 256
	20	gtrr 1 2 1              [1] = [1] > [2]
	21	addr 1 3 3              [3] = [1] + [3]
	22	addi 3 1 3              [3] = [3] + 1
	23	seti 25 2 3             [3] = 25
	24	addi 4 1 4              [4] = [4] + 1
	25	seti 17 7 3             [3] = 17
	26	setr 4 0 2              [2] = [4]
	27	seti 7 8 3              [3] = 7
	28	eqrr 5 0 4              [4] = [5] == [0]
	29	addr 4 3 3              [3] = [4] + [3]
	30	seti 5 1 3              [3] = 5


		[a, b, c, index, e, f]
		 0  1  2  3      4  5

		#ip 3
	0	seti 123 0 5            f = 123
	1	bani 5 456 5            while (true) { f = f & 456
	2	eqri 5 72 5                 f = f == 72
	3	addr 5 3 3                  if (f == 1) break;
	4	seti 0 0 3              }
	5	seti 0 5 5              f = 0
	6	bori 5 65536 2          while (true) { c = f | 65536
	7	seti 10362650 3 5           f = 10362650
	8	bani 2 255 4                while (true) { e = c & 255
	9	addr 5 4 5                      f = f + e
	10	bani 5 16777215 5               f = f & 16777215
	11	muli 5 65899 5                  f = f * 65899
	12	bani 5 16777215 5               f = f & 16777215
	13	gtir 256 2 4                    e = 256 > c
	14	addr 4 3 3                      if (e == 1) break;
	15	addi 3 1 3                      skip
	16	seti 27 4 3                     skip
	17	seti 0 3 4                      e = 0
	18	addi 4 1 1                      while (true) { b = e + 1
	19	muli 1 256 1                        b = b * 256
	20	gtrr 1 2 1                          b = b > c
	21	addr 1 3 3                          if (b == 1) break;
	22	addi 3 1 3                          skip
	23	seti 25 2 3                         skip
	24	addi 4 1 4                          e = e + 1
	25	seti 17 7 3                     }
	26	setr 4 0 2                      c = e
	27	seti 7 8 3                  }
	28	eqrr 5 0 4                  e = f == a
	29	addr 4 3 3                  if (e == 1) break;
	30	seti 5 1 3              }
	
	
	
	
	
		int a = 0;
		int f = 0;
		
		// Ничего не делает
//		f = 123;
//		while (true) {
//			f = f & 456;
//			f = f == 72 ? 1 : 0;
//			if (f == 1) break;
//		}
		
		f = 0;
		while (true) {
			int c = f | 65536; // тут значение f из предыдущего цикла
			f = 10362650;
			while (true) {
				f = f + (c & 255);
				f = f & 16777215;
				f = f * 65899;
				f = f & 16777215;
				if (256 > c) break;
				
				// Можно заменить на c /= 256;
//				int e = 0;
//				while (true) {
//					if ((e + 1) * 256 > c) break;
//					e++;
//				}
//				c = e;
				c /= 256;
			}
			if (f == a) break;
		}
 */