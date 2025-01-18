package org.advent.year2017.day23;

import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.List;
import java.util.Scanner;

public class Day23 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day23()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("input.txt", 8281, 911)
		);
	}
	
	String[] operations;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		operations = Utils.readLines(input).toArray(String[]::new);
	}
	
	@Override
	public Object part1() {
		int b = Integer.parseInt(operations[0].split(" ")[2]);
		return (b - 2) * (b - 2);
	}
	
	@Override
	public Object part2() {
		/*
		set b 93           b = 93;
		set c b            c = b;
		jnz a 2            if (a != 0) {
		jnz 1 5              // skip
		mul b 100            b *= 100;
		sub b -100000        b += 100000;
		set c b              c = b;
		sub c -17000         c += 17000; }
		set f 1            do {f = 1;
		set d 2              d = 2;
		set e 2              do { e = 2;
		set g d                do { g = d;
		mul g e                  g *= e;
		sub g b                  g -= b;
		jnz g 2                  if (g == 0) {
		set f 0                    f = 0; }
		sub e -1                 e++;
		set g e                  g = e;
		sub g b                  g -= b;
		jnz g -8               } while (g != 0);
		sub d -1               d++;
		set g d                g = d;
		sub g b                g -= b;
		jnz g -13            } while (g != 0);
		jnz f 2              if (f == 0)
		sub h -1               h++;
		set g b              g = b;
		sub g c              g -= c;
		jnz g 2              if (g == 0)
		jnz 1 3                break;
		sub b -17            b += 17;
		jnz 1 -23          } while (true);
		 */
		
		int b = Integer.parseInt(operations[0].split(" ")[2]);
		b *= Integer.parseInt(operations[4].split(" ")[2]);
		b -= Integer.parseInt(operations[5].split(" ")[2]);
		
		int count = -Integer.parseInt(operations[7].split(" ")[2]);
		count /= -Integer.parseInt(operations[30].split(" ")[2]);
		
		int h = 0;
		
		
		for (int i = 0; i <= count; i++) {
			boolean f = true;
			int sqrtB = (int) Math.sqrt(b) + 1;
			for (int d = 2; d < sqrtB; d++) {
				if (b % d == 0) {
					f = false;
					break;
				}
			}
			if (!f)
				h++;
			b += 17;
		}
		
		return h;
	}
}