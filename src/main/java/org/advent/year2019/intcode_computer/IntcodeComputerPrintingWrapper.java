package org.advent.year2019.intcode_computer;

import lombok.Data;

@Data
public class IntcodeComputerPrintingWrapper {
	
	private final IntcodeComputer computer;
	
	public void run(InputProvider inputProvider) {
		while (computer.getState() == IntcodeComputer.State.RUNNING) {
			Long symbol = computer.runUntilOutput(inputProvider);
			if (symbol == null)
				break;
			if (symbol < 256)
				System.out.print((char) symbol.longValue());
			else
				System.out.println(symbol);
		}
	}
}