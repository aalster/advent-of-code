package org.advent.year2021.day19;

import org.advent.common.Point3D;
import org.advent.common.Region3D;
import org.advent.common.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19 {
	static final int scannerRange = 1000;
	static final int commonBeacons = 12;
	
	public static void main(String[] args) {
		Scanner input = Utils.scanFileNearClass(Day19.class, "input.txt");
		List<ScannerReport> reports = new ArrayList<>();
		while (input.hasNext())
			reports.add(ScannerReport.parse(input));
		
		System.out.println("Answer 1: " + part1(reports));
		System.out.println("Answer 2: " + part2(reports));
	}
	
	private static long part1(List<ScannerReport> reports) {
		return mergeScanners(reports).stream().flatMap(r -> r.beacons().stream()).distinct().count();
	}
	
	private static long part2(List<ScannerReport> reports) {
		List<ScannerReport> merged = mergeScanners(reports);
		int maxDistance = 0;
		for (ScannerReport left : merged) {
			for (ScannerReport right : merged) {
				if (left == right)
					continue;
				int distance = left.scanner().manhattanDistance(right.scanner());
				if (maxDistance < distance)
					maxDistance = distance;
			}
		}
		return maxDistance;
	}
	
	private static List<ScannerReport> mergeScanners(List<ScannerReport> reports) {
		List<ScannerReport> notMerged = new ArrayList<>(reports);
		List<ScannerReport> merged = new ArrayList<>();
		merged.add(notMerged.removeFirst());
		
		List<ScannerReport> checked = new ArrayList<>();
		Map<Integer, List<ScannerReport>> cachedAlignments = new HashMap<>();
		
		while (!notMerged.isEmpty()) {
			ScannerReport basis = merged.removeFirst();
			for (ScannerReport candidate : new ArrayList<>(notMerged)) {
				List<ScannerReport> alignments = cachedAlignments.computeIfAbsent(candidate.number(), k1 -> candidate.allAlignments());
				candidateChecked: for (ScannerReport aligned : alignments) {
					for (Point3D basisBeacon : skip(basis.beacons(), commonBeacons - 1)) {
						for (Point3D candidateBeacon : skip(aligned.beacons(), commonBeacons - 1)) {
							Point3D delta = basisBeacon.subtract(candidateBeacon);
							ScannerReport shifted = aligned.shift(delta);
							if (matches(basis, shifted)) {
								merged.add(shifted);
								notMerged.remove(candidate);
//								System.out.println("Found pair: " + basis.number() + "-" + aligned.number() + " " + delta);
								break candidateChecked;
							}
						}
					}
				}
			}
			checked.add(basis);
		}
		checked.addAll(merged);
		return checked;
	}
	
	static boolean matches(ScannerReport basis, ScannerReport candidate) {
		Region3D intersectionRegion = basis.region().intersection(candidate.region());
		Set<Point3D> basisCommonBeacons = basis.beacons().stream()
				.filter(intersectionRegion::containsInclusive)
				.collect(Collectors.toSet());
		Set<Point3D> candidateCommonBeacons = candidate.beacons().stream()
				.filter(intersectionRegion::containsInclusive)
				.collect(Collectors.toSet());
		return basisCommonBeacons.size() == candidateCommonBeacons.size()
				&& basisCommonBeacons.size() >= commonBeacons
				&& basisCommonBeacons.containsAll(candidateCommonBeacons);
	}
	
	static <T> Collection<T> skip(Collection<T> items, int skip) {
		return items.stream().skip(skip).toList();
	}
	
	record ScannerReport(int number, Point3D scanner, Set<Point3D> beacons) {
		
		Region3D region() {
			return Region3D.fromCenter(scanner, scannerRange);
		}
		
		ScannerReport shift(Point3D delta) {
			return new ScannerReport(number, delta, beacons.stream().map(delta::shift).collect(Collectors.toSet()));
		}
		
		List<ScannerReport> allAlignments() {
			Set<Point3D> zRotation = rotate(beacons, Point3D::rotateRightAlongZ);
			Set<Point3D> zNegativeRotation = rotate(rotate(zRotation, Point3D::rotateRightAlongZ), Point3D::rotateRightAlongZ);
			return Stream.concat(
							allRotations(beacons, Point3D::rotateRightAlongX),
							Stream.of(zRotation, zNegativeRotation))
					.flatMap(p -> allRotations(p, Point3D::rotateRightAlongY))
					.map(beacons -> new ScannerReport(number, scanner, beacons))
					.toList();
		}
		
		static ScannerReport parse(Scanner scanner) {
			int number = Integer.parseInt(scanner.nextLine().replace("--- scanner ", "").replace(" ---", ""));
			Set<Point3D> beacons = new HashSet<>();
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.isEmpty())
					break;
				beacons.add(Point3D.parse(line));
			}
			return new ScannerReport(number, new Point3D(0, 0, 0), beacons);
		}
	}
	
	static Stream<Set<Point3D>> allRotations(Set<Point3D> points, Function<Point3D, Point3D> rotation) {
		Set<Point3D> first = rotate(points, rotation);
		Set<Point3D> second = rotate(first, rotation);
		Set<Point3D> third = rotate(second, rotation);
		return Stream.of(points, first, second, third);
	}
	
	static Set<Point3D> rotate(Set<Point3D> points, Function<Point3D, Point3D> rotation) {
		return points.stream().map(rotation).collect(Collectors.toSet());
	}
}