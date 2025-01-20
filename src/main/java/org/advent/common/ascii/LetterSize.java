package org.advent.common.ascii;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LetterSize {
	SIZE_6x5(6, 5),
	SIZE_8x7(8, 7),
	SIZE_10x8(10, 8);
	
	private final int height;
	private final int width;
	
	String fileName() {
		return "letters_" + height + "x" + width + ".txt";
	}
	
	static LetterSize forHeight(int height) {
		for (LetterSize size : values())
			if (size.getHeight() == height)
				return size;
		throw new IllegalArgumentException("No such letter size: " + height);
	}
}
