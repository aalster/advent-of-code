package org.advent.year2022.day22;

import org.advent.common.Direction;
import org.advent.common.FieldBounds;
import org.advent.common.Pair;
import org.advent.common.Point;
import org.advent.common.Utils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day22 {
	
	public static void main(String[] args) throws Exception {
		Scanner input = Utils.scanFileNearClass(Day22.class, "input.txt");
		Set<Point> field = new HashSet<>();
		Set<Point> walls = new HashSet<>();
		int row = 0;
		while (input.hasNext()) {
			String line = input.nextLine();
			if (line.isEmpty())
				break;
			for (int col = 0; col < line.length(); col++) {
				char c = line.charAt(col);
				if (c == ' ')
					continue;
				field.add(new Point(col, row));
				if (c == '#')
					walls.add(new Point(col, row));
			}
			row++;
		}
		List<Object> actions = parseActions(input.nextLine());
		
		System.out.println("Answer 1: " + part1(field, walls, actions));
		// Вторая часть решена через костыль
		System.out.println("Answer 2: " + part2(field, walls, actions));
	}
	
	private static int part1(Set<Point> field, Set<Point> walls, List<Object> actions) {
		FieldBounds bounds = FieldBounds.ofField(field);
		Point position = bounds.rowMin(0);
		Direction direction = Direction.RIGHT;
		
		for (Object action : actions) {
			if (action instanceof Direction d) {
				direction = direction.rotate(d);
				continue;
			}
			if (action instanceof Integer steps) {
				for (int step = 0; step < steps; step++) {
					Point next = bounds.moveWrappingAround(position, direction);
					if (walls.contains(next))
						break;
					position = next;
				}
			}
		}
		System.out.println(position + ", " + direction);
		return (position.y() + 1) * 1000 + (position.x() + 1) * 4 + directionId(direction);
	}
	
	private static int part2(Set<Point> field, Set<Point> walls, List<Object> actions) throws Exception {
		Point topLeftCorner = new Point(field.stream().filter(p -> p.y() == 0).mapToInt(Point::x).min().orElseThrow(), 0);
		
		int side = (int) Math.sqrt(field.size() / 6f);
		if (side * side * 6 != field.size())
			throw new RuntimeException("Not a cube");
		
		Map<FaceType, Face> faces = splitByFace2(field, topLeftCorner, side);
		Location location = new Location(topLeftCorner, Direction.RIGHT, FaceType.TOP);
		
		for (Object action : actions) {
			
			if (action instanceof Direction d) {
				location = location.rotate(d);
				continue;
			}
			if (action instanceof Integer steps) {
				for (int step = 0; step < steps; step++) {
//					printField(field, walls, location.position(), location.direction());
//					Thread.sleep(2000);
					
					Location next = location.move();
					Face currentFace = faces.get(location.face());
					if (!currentFace.points().contains(next.position())) {
						Face nextFace = faces.get(currentFace.neighbors().get(location.direction()));
						int delta = currentFace.leavingDelta(location.position(), location.direction());
						Direction enteringDirection = nextFace.enteringDirection(currentFace.type());
						Point nextPosition = nextFace.enteringPoint(delta, enteringDirection);
						next = new Location(nextPosition, enteringDirection.reverse(), nextFace.type());
					}
					if (walls.contains(next.position()))
						break;
					location = next;
				}
			}
		}
		System.out.println(location);
		return (location.position().y() + 1) * 1000 + (location.position().x() + 1) * 4 + directionId(location.direction());
	}
	
	static void printField(Set<Point> emptyPoints, Set<Point> walls, Point position, Direction direction) {
		System.out.println("\nField:");
		for (int y = 0; y < 12; y++) {
			for (int x = 0; x < 16; x++) {
				Point point = new Point(x, y);
				System.out.print(position.equals(point) ? direction.presentation() :
						walls.contains(point) ? "#" : emptyPoints.contains(point) ? "." : " ");
			}
			System.out.println();
		}
	}
	
	private static List<Object> parseActions(String line) {
		List<Object> actions = new ArrayList<>();
		int steps = 0;
		for (char c : line.toCharArray()) {
			if ('0' <= c && c <= '9') {
				steps = steps * 10 + (c - '0');
			} else {
				actions.add(steps);
				steps = 0;
				actions.add(c == 'L' ? Direction.LEFT : Direction.RIGHT);
			}
		}
		if (steps > 0)
			actions.add(steps);
		return actions;
	}
	
	private static int directionId(Direction direction) {
		return switch (direction) {
			case RIGHT -> 0;
			case DOWN -> 1;
			case LEFT -> 2;
			case UP -> 3;
		};
	}
	
	enum FaceType {
		FRONT,
		LEFT,
		RIGHT,
		TOP,
		BOTTOM,
		BACK;
		
		FaceType[] neighborsClockwise() {
			return switch (this) {
				case TOP -> new FaceType[] {BACK, RIGHT, FRONT, LEFT};
				case FRONT -> new FaceType[] {TOP, RIGHT, BOTTOM, LEFT};
				case BOTTOM -> new FaceType[] {FRONT, RIGHT, BACK, LEFT};
				case BACK -> new FaceType[] {BOTTOM, RIGHT, TOP, LEFT};
				case LEFT -> new FaceType[] {TOP, FRONT, BOTTOM, BACK};
				case RIGHT -> new FaceType[] {TOP, BACK, BOTTOM, FRONT};
			};
		}
		
		FaceType neighbor(Direction direction) {
			return switch (this) {
				case TOP -> switch (direction) {
					case UP -> BACK;
					case RIGHT -> RIGHT;
					case DOWN -> FRONT;
					case LEFT -> LEFT;
				};
				case FRONT -> switch (direction) {
					case UP -> TOP;
					case RIGHT -> RIGHT;
					case DOWN -> BOTTOM;
					case LEFT -> LEFT;
				};
				case BOTTOM -> switch (direction) {
					case UP -> FRONT;
					case RIGHT -> RIGHT;
					case DOWN -> BACK;
					case LEFT -> LEFT;
				};
				case BACK -> switch (direction) {
					case UP -> BOTTOM;
					case RIGHT -> RIGHT;
					case DOWN -> TOP;
					case LEFT -> LEFT;
				};
				case LEFT -> switch (direction) {
					case UP -> TOP;
					case RIGHT -> FRONT;
					case DOWN -> BOTTOM;
					case LEFT -> BACK;
				};
				case RIGHT -> switch (direction) {
					case UP -> TOP;
					case RIGHT -> BACK;
					case DOWN -> BOTTOM;
					case LEFT -> FRONT;
				};
			};
		}
	}
	
	record Face(
			FaceType type,
			Set<Point> points,
			int minX,
			int maxX,
			int minY,
			int maxY,
			Map<Direction, FaceType> neighbors
	) {
		
		int leavingDelta(Point from, Direction leavingTo) {
			return switch (leavingTo) {
				case UP -> from.x() - minX;
				case RIGHT -> from.y() - minY;
				case DOWN -> maxX - from.x();
				case LEFT -> maxY - from.y();
			};
		}
		
		Direction enteringDirection(FaceType enteringFrom) {
			for (Map.Entry<Direction, FaceType> entry : neighbors.entrySet())
				if (entry.getValue() == enteringFrom)
					return entry.getKey();
			throw new NullPointerException();
		}
		
		Point enteringPoint(int enteringDelta, Direction enteringFrom) {
			return switch (enteringFrom) {
				case UP -> new Point(maxX - enteringDelta, minY);
				case RIGHT -> new Point(maxX, maxY - enteringDelta);
				case DOWN -> new Point(minX + enteringDelta, maxY);
				case LEFT -> new Point(minX, minY + enteringDelta);
			};
		}
		
		static Face create(FaceType type, Map<FaceType, Set<Point>> allPoints, int side) {
			Set<Point> points = allPoints.get(type);
			int minX = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxY = Integer.MIN_VALUE;
			
			for (Point point : points) {
				if (point.x() < minX)
					minX = point.x();
				if (maxX < point.x())
					maxX = point.x();
				if (point.y() < minY)
					minY = point.y();
				if (maxY < point.y())
					maxY = point.y();
			}
			
			Point anyPoint = points.iterator().next();
			Direction anyPointDirection = Direction.UP;
			FaceType anyPointFace = null;
			while (anyPointFace == null) {
				anyPointDirection = anyPointDirection.rotate(Direction.RIGHT);
				Point checkPoint = anyPoint.move(anyPointDirection, side);
				for (Map.Entry<FaceType, Set<Point>> entry : allPoints.entrySet()) {
					if (entry.getValue().contains(checkPoint)) {
						anyPointFace = entry.getKey();
						break;
					}
				}
			}
			Map<Direction, FaceType> neighbors = new HashMap<>();
			neighbors.put(anyPointDirection, anyPointFace);
			
			FaceType[] neighborsClockwise = type.neighborsClockwise();
			
			for (int i = ArrayUtils.indexOf(neighborsClockwise, anyPointFace) + 1; i < 7; i++) {
				anyPointDirection = anyPointDirection.rotate(Direction.RIGHT);
				neighbors.put(anyPointDirection, neighborsClockwise[i % 4]);
			}
			
			return new Face(type, points, minX, maxX, minY, maxY, neighbors);
		}
	}
	
	record Location (
		Point position,
		Direction direction,
		FaceType face
	) {
		Location move() {
			return new Location(position.move(direction), direction, face);
		}
		Location rotate(Direction direction) {
			return new Location(position, this.direction.rotate(direction), face);
		}
	}
	
//	static Map<FaceType, Face> splitByFace(Set<Point> points, Point topLeftCorner, int side) {
//		Map<FaceType, Set<Point>> faces = Arrays.stream(FaceType.values()).collect(Collectors.toMap(f -> f, f -> new HashSet<>()));
//		for (Point point : points)
//			faces.get(getFace(topLeftCorner, point, side)).add(point);
//		return Arrays.stream(FaceType.values()).collect(Collectors.toMap(f -> f, f -> Face.create(f, faces, side)));
//	}
//
//	static FaceType getFace(Point topLeftCorner, Point target, int side) {
//		int dx = (target.x() / side * side - topLeftCorner.x()) / side;
//		int dy = (target.y() / side * side - topLeftCorner.y()) / side;
//
//		if (dx < -2 || 3 <= dx)
//			throw new RuntimeException("Such dx not supported");
//
//		if (dx == -1)
//			return FaceType.LEFT;
//		if (dx == 1)
//			return FaceType.RIGHT;
//
//		if (dx != 0)
//			dy += 2;
//
//		return switch (dy % 4) {
//			case 0 -> FaceType.TOP;
//			case 1 -> FaceType.FRONT;
//			case 2 -> FaceType.BOTTOM;
//			case 3 -> FaceType.BACK;
//			default -> throw new IllegalArgumentException();
//		};
//	}
	
	static Map<FaceType, Face> splitByFace2(Set<Point> points, Point topLeftCorner, int side) {
		points = new HashSet<>(points);
		Map<FaceType, Set<Point>> pointsByFace = new HashMap<>();
		
		List<Pair<FaceType, Point>> checks = new ArrayList<>();
		checks.add(Pair.of(FaceType.TOP, topLeftCorner));
		while (!points.isEmpty() && !checks.isEmpty()) {
			Pair<FaceType, Point> pair = checks.remove(0);
			if (!points.contains(pair.right()))
				continue;
			
			pointsByFace.put(pair.left(), removeSameFace(points, pair.right(), side));
			
			if (pair.left() == FaceType.LEFT) {
				// Вставил костыль, т. к. замучился
				checks.add(Pair.of(FaceType.BACK, pair.right().move(Direction.DOWN, side)));
			} else {
				for (Direction direction : Direction.values())
					checks.add(Pair.of(pair.left().neighbor(direction), pair.right().move(direction, side)));
			}
		}
		
		return Arrays.stream(FaceType.values()).collect(Collectors.toMap(f -> f, f -> Face.create(f, pointsByFace, side)));
	}
	
	static Set<Point> removeSameFace(Set<Point> points, Point example, int side) {
		int minX = example.x() / side * side;
		int minY = example.y() / side * side;
		int maxX = minX + side;
		int maxY = minY + side;
		Set<Point> sidePoints = points.stream()
				.filter(p -> minX <= p.x() && p.x() < maxX && minY <= p.y() && p.y() < maxY)
				.collect(Collectors.toSet());
		points.removeAll(sidePoints);
		return sidePoints;
	}
}