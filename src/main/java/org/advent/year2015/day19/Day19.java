package org.advent.year2015.day19;

import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Day19 {
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day19.class, "input.txt");
		Map<String, List<String>> replacements = new HashMap<>();
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty())
				break;
			String[] split = line.split(" => ");
			replacements.computeIfAbsent(split[0], k -> new ArrayList<>()).add(split[1]);
		}
		String molecule = input.nextLine();
		
		System.out.println("Answer 1: " + part1(replacements, molecule));
		System.out.println("Answer 2: " + part2(replacements, molecule));
	}
	
	private static long part1(Map<String, List<String>> replacements, String molecule) {
		Set<String> nextMolecules = new HashSet<>(molecule.length() * 2);
		for (String replace : replacements.keySet()) {
			int prevIndex = 0;
			while (true) {
				int index = molecule.indexOf(replace, prevIndex);
				if (index < 0)
					break;
				prevIndex = index + 1;
				String left = molecule.substring(0, index);
				String right = molecule.substring(index + replace.length());
				for (String replacement : replacements.get(replace))
					nextMolecules.add(left + replacement + right);
			}
		}
		return nextMolecules.size();
	}
	
	private static long part2(Map<String, List<String>> replacements, String molecule) {
		return molecule.chars().filter(Character::isUpperCase).count()
				- count(molecule, "Rn") - count(molecule, "Ar") - count(molecule, "Y") * 2L - 1;
	}
	
	static int count(String text, String target) {
		int count = 0;
		int prevIndex = 0;
		while (true) {
			int index = text.indexOf(target, prevIndex);
			if (index < 0)
				break;
			prevIndex = index + 1;
			count++;
		}
		return count;
	}
	
	/*
		https://www.reddit.com/r/adventofcode/comments/3xflz8/day_19_solutions/
		
		All of the rules are of one of the following forms:
		α => βγ
		α => βRnγAr
		α => βRnγYδAr
		α => βRnγYδYεAr
		
		As Rn, Ar, and Y are only on the left side of the equation, one merely only needs to compute
		#NumSymbols - #Rn - #Ar - 2 * #Y - 1
		
		Subtract of #Rn and #Ar because those are just extras.
		Subtract two times #Y because we get rid of the Ys and the extra elements following them.
		Subtract one because we start with "e".
		
		1 элемент -> 2 элемента
		Al => ThF
		B => BCa
		B => TiB
		Ca => CaCa
		Ca => PB
		Ca => SiTh
		F => CaF
		F => PMg
		F => SiAl
		H => HCa
		H => NTh
		H => OB
		Mg => BF
		Mg => TiMg
		N => HSi
		O => HP
		O => OTi
		P => CaP
		P => PTi
		
		1 элемент -> 2 элемента + Rn + Ar
		Al => ThRnFAr
		B => TiRnFAr
		Ca => PRnFAr
		Ca => SiRnMgAr
		H => CRnAlAr
		H => NRnMgAr
		H => ORnFAr
		N => CRnFAr
		O => CRnMgAr
		O => NRnFAr
		P => SiRnFAr
		
		1 элемент -> 3 элемента + Rn + Ar + Y
		Ca => SiRnFYFAr
		H => CRnFYMgAr
		H => CRnMgYFAr
		H => NRnFYFAr
		O => CRnFYFAr
		
		1 элемент -> 4 элемента + Rn + Ar + 2*Y
		H => CRnFYFYFAr

	 */
}