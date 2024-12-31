package org.advent.year2022.day22;

import org.advent.common.Direction;
import org.advent.common.FieldBounds;
import org.advent.common.Point;
import org.advent.common.Rect;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day22 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day22()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 6032, 5031),
				new ExpectedAnswers("input.txt", 31568, 36540)
		);
	}
	
	Set<Point> allPoints;
	Set<Point> walls;
	List<Object> actions;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		List<List<String>> lists = Utils.splitByEmptyLine(Utils.readLines(input));
		Map<Character, List<Point>> field = Point.readField(lists.getFirst());
		allPoints = Stream.of(field.get('.'), field.get('#')).flatMap(List::stream).collect(Collectors.toSet());
		walls = new HashSet<>(field.get('#'));
		
		Matcher matcher = Pattern.compile("\\d+|L|R").matcher(lists.getLast().getFirst());
		actions = new ArrayList<>();
		while (matcher.find()) {
			String group = matcher.group();
			actions.add(switch (group) {
				case "L" -> Direction.LEFT;
				case "R" -> Direction.RIGHT;
				default -> Integer.parseInt(group);
			});
		}
	}
	
	@Override
	public Object part1() {
		FieldBounds bounds = FieldBounds.ofField(allPoints);
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
		return new State(position, direction).password();
	}
	
	@Override
	public Object part2() {
		Map<FaceType, Face> faces = findFaces(allPoints).stream().collect(Collectors.toMap(Face::type, f -> f));
		Point start = new Point(allPoints.stream().filter(p -> p.y() == 0).mapToInt(Point::x).min().orElseThrow(), 0);
		State state = new State(start, Direction.RIGHT);
		Face face = faces.values().stream().filter(f -> f.contains(start)).findFirst().orElseThrow();
		
		for (Object action : actions) {
			if (action instanceof Direction d) {
				state = state.turn(d);
				continue;
			}
			if (action instanceof Integer steps) {
				for (int step = 0; step < steps; step++) {
					State next = state.move();
					
					Face nextFace = face;
					if (!face.contains(next.position)) {
						nextFace = faces.get(face.neighbor(state.direction));
						if (!nextFace.contains(next.position))
							next = face.jumpToFace(state, nextFace);
					}
					
					if (walls.contains(next.position))
						break;
					face = nextFace;
					state = next;
				}
			}
		}
		return state.password();
	}
	
	record State(Point position, Direction direction) {
		
		State move() {
			return new State(position.move(direction), direction);
		}
		
		State turn(Direction d) {
			return new State(position, direction.rotate(d));
		}
		
		static Direction[] directions = new Direction[] {Direction.RIGHT, Direction.DOWN, Direction.LEFT, Direction.UP};
		int password() {
			return (position.y() + 1) * 1000 + (position.x() + 1) * 4 + ArrayUtils.indexOf(directions, direction);
		}
	}
	
	enum FaceType {
		FRONT,
		TOP,
		RIGHT,
		BOTTOM,
		LEFT,
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
		
		Direction neighborOrientation(FaceType neighbor) {
			return switch (this) {
				case TOP -> switch (neighbor) {
					case BACK, FRONT -> Direction.UP;
					case RIGHT -> Direction.LEFT;
					case LEFT -> Direction.RIGHT;
					default -> throw new IllegalStateException("Unexpected value: " + neighbor);
				};
				
				case FRONT -> Direction.UP;
				
				case BOTTOM -> switch (neighbor) {
					case FRONT, BACK -> Direction.UP;
					case RIGHT -> Direction.RIGHT;
					case LEFT -> Direction.LEFT;
					default -> throw new IllegalStateException("Unexpected value: " + neighbor);
				};
				
				case BACK -> switch (neighbor) {
					case BOTTOM, TOP -> Direction.UP;
					case RIGHT, LEFT -> Direction.DOWN;
					default -> throw new IllegalStateException("Unexpected value: " + neighbor);
				};
				
				case LEFT -> switch (neighbor) {
					case TOP -> Direction.LEFT;
					case FRONT -> Direction.UP;
					case BOTTOM -> Direction.RIGHT;
					case BACK -> Direction.DOWN;
					default -> throw new IllegalStateException("Unexpected value: " + neighbor);
				};
				
				case RIGHT -> switch (neighbor) {
					case TOP -> Direction.RIGHT;
					case FRONT -> Direction.UP;
					case BOTTOM -> Direction.LEFT;
					case BACK -> Direction.DOWN;
					default -> throw new IllegalStateException("Unexpected value: " + neighbor);
				};
			};
		}
		
		FaceType neighbor(Direction direction) {
			return neighborsClockwise()[direction.getIndexClockwise()];
		}
		
		Direction neighborDirection(FaceType neighbor) {
			return Direction.values()[ArrayUtils.indexOf(neighborsClockwise(), neighbor)];
		}
	}
	
	record Face(FaceType type, Set<Point> points, Direction orientation, Rect bounds) {
		
		Face(FaceType type, Set<Point> points, Direction faceDirection) {
			this(type, points, faceDirection, Point.bounds(points));
		}
		
		FaceType neighbor(Direction direction) {
			return type.neighbor(direction.rotate(orientation.mirror()));
		}
		
		Direction neighborOrientation(FaceType neighbor) {
			return type.neighborOrientation(neighbor).rotate(orientation);
		}
		
		boolean contains(Point point) {
			return bounds.containsInclusive(point);
		}
		
		State jumpToFace(State state, Face nextFace) {
			int leavingDelta = leavingDelta(state.position, state.direction);
			Direction nextDirection = nextFace.type.neighborDirection(type).reverse().rotate(nextFace.orientation);
			return new State(nextFace.enteringPosition(leavingDelta, nextDirection), nextDirection);
		}
		
		int leavingDelta(Point from, Direction leavingTo) {
			return switch (leavingTo) {
				case UP -> from.x() - bounds.minX();
				case RIGHT -> from.y() - bounds.minY();
				case DOWN -> bounds.maxX() - from.x();
				case LEFT -> bounds.maxY() - from.y();
			};
		}
		
		Point enteringPosition(int enteringDelta, Direction moveDirection) {
			return switch (moveDirection) {
				case UP -> new Point(bounds.minX() + enteringDelta, bounds.maxY());
				case RIGHT -> new Point(bounds.minX(), bounds.minY() + enteringDelta);
				case DOWN -> new Point(bounds.maxX() - enteringDelta, bounds.minY());
				case LEFT -> new Point(bounds.maxX(), bounds.maxY() - enteringDelta);
			};
		}
	}
	
	List<Face> findFaces(Collection<Point> field) {
		int side = (int) Math.sqrt(field.size() / 6f);
		if (side * side * 6 != field.size())
			throw new RuntimeException("Not a cube");
		
		List<Set<Point>> facesPoints = new ArrayList<>(field.stream().collect(Collectors.groupingBy(
				p1 -> new Point(p1.x() / side, p1.y() / side), Collectors.toSet())).values());
		
		Point topLeftCorner = field.stream().min(Comparator.comparing(Point::y).thenComparing(Point::x)).orElseThrow();
		Set<Point> topFacePoints = facesPoints.stream().filter(p -> p.contains(topLeftCorner)).findAny().orElseThrow();
		
		Face topFace = new Face(FaceType.TOP, topFacePoints, Direction.UP);
		facesPoints.remove(topFace.points);
		
		List<Face> faces = new ArrayList<>();
		faces.add(topFace);
		
		while (!facesPoints.isEmpty()) {
			pointsLoop: for (Set<Point> currentPoints : facesPoints) {
				Point sample = currentPoints.iterator().next();
				for (Direction direction : Direction.values()) {
					Point moved = sample.move(direction, side);
					for (Face face : faces) {
						if (face.contains(moved)) {
							FaceType currentType = face.neighbor(direction.reverse());
							faces.add(new Face(currentType, currentPoints, face.neighborOrientation(currentType)));
							facesPoints.remove(currentPoints);
							break pointsLoop;
						}
					}
				}
			}
		}
		return faces;
	}
}