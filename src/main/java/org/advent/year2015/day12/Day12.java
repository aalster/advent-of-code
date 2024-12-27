package org.advent.year2015.day12;

import org.advent.common.Pair;
import org.advent.common.Utils;
import org.advent.runner.AdventDay;
import org.advent.runner.DayRunner;
import org.advent.runner.ExpectedAnswers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day12 extends AdventDay {
	
	public static void main(String[] args) {
		new DayRunner(new Day12()).runAll();
	}
	
	@Override
	public List<ExpectedAnswers> expected() {
		return List.of(
				new ExpectedAnswers("example.txt", 6 * 2 + 3 * 2, ExpectedAnswers.IGNORE),
				new ExpectedAnswers("example2.txt", ExpectedAnswers.IGNORE, 6 + 4 + 6),
				new ExpectedAnswers("input.txt", 111754, 65402)
		);
	}
	
	String line;
	
	@Override
	public void prepare(String file) {
		Scanner input = Utils.scanFileNearClass(getClass(), file);
		line = input.nextLine();
	}
	
	@Override
	public Object part1() {
		int result = 0;
		char[] chars = line.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (!Character.isDigit(chars[i]))
				continue;
			int number = chars[i] - '0';
			int next = i + 1;
			while (next < chars.length && Character.isDigit(chars[next])) {
				number = number * 10 + (chars[next] - '0');
				next++;
			}
			if (i > 0 && chars[i - 1] == '-')
				number *= -1;
			result += number;
			i = next - 1;
		}
		return result;
	}
	
	@Override
	public Object part2() {
		return JsonElement.parse(line).numbersSum("red");
	}
	
	sealed interface JsonElement permits JsonString, JsonNumber, JsonArray, JsonObject {
		
		int numbersSum(String excludeObjectsField);
		
		static JsonElement parse(String json) {
			json = json.replace(" ", "").replace("\n", "");
			return parse(json, 0).left();
		}
		
		static Pair<? extends JsonElement, Integer> parse(String json, int index) {
			return switch (json.charAt(index)) {
				case '{' -> JsonObject.parse(json, index);
				case '[' -> JsonArray.parse(json, index);
				case '\"' -> JsonString.parse(json, index);
				case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> JsonNumber.parse(json, index);
				default -> throw new RuntimeException("Invalid character '" + json.charAt(index) + "'");
			};
		}
	}
	
	record JsonString(String value) implements JsonElement {
		
		@Override
		public int numbersSum(String excludeObjectsField) {
			return 0;
		}
		
		static Pair<JsonString, Integer> parse(String json, int start) {
			int index = start + 1;
			while (json.charAt(index) != '\"')
				index++;
			return Pair.of(new JsonString(json.substring(start + 1, index)), index + 1);
		}
	}
	
	record JsonNumber(int value) implements JsonElement {
		
		@Override
		public int numbersSum(String excludeObjectsField) {
			return value;
		}
		
		static Pair<JsonNumber, Integer> parse(String json, int start) {
			int index = start;
			if (json.charAt(index) == '-')
				index++;
			int number = 0;
			while (index < json.length() && Character.isDigit(json.charAt(index))) {
				number = number * 10 + (json.charAt(index) - '0');
				index++;
			}
			if (json.charAt(start) == '-')
				number *= -1;
			return Pair.of(new JsonNumber(number), index);
		}
	}
	
	record JsonArray(List<JsonElement> values) implements JsonElement {
		
		@Override
		public int numbersSum(String excludeObjectsField) {
			return values.stream().mapToInt(e -> e.numbersSum(excludeObjectsField)).sum();
		}
		
		static Pair<JsonArray, Integer> parse(String json, int start) {
			List<JsonElement> values = new ArrayList<>();
			int index = start + 1;
			while (json.charAt(index) != ']') {
				Pair<? extends JsonElement, Integer> valueParsed = JsonElement.parse(json, index);
				index = valueParsed.right();
				
				values.add(valueParsed.left());
				
				if (json.charAt(index) == ',')
					index++;
			}
			return Pair.of(new JsonArray(values), index + 1);
		}
	}
	
	record JsonObject(Map<String, JsonElement> values) implements JsonElement {
		
		@Override
		public int numbersSum(String excludeObjectsField) {
			if (values.values().stream()
					.anyMatch(e -> e instanceof JsonString(String value) && value.equals(excludeObjectsField)))
				return 0;
			return values.values().stream().mapToInt(e -> e.numbersSum(excludeObjectsField)).sum();
		}
		
		static Pair<JsonObject, Integer> parse(String json, int start) {
			Map<String, JsonElement> fields = new HashMap<>();
			int index = start + 1;
			while (json.charAt(index) != '}') {
				Pair<JsonString, Integer> keyParsed = JsonString.parse(json, index);
				index = keyParsed.right();
				if (json.charAt(index) != ':')
					throw new RuntimeException("Expected ':' found: " + json.charAt(index));
				index++;
				Pair<? extends JsonElement, Integer> valueParsed = JsonElement.parse(json, index);
				index = valueParsed.right();
				
				fields.put(keyParsed.left().value, valueParsed.left());
				
				if (json.charAt(index) == ',')
					index++;
			}
			return Pair.of(new JsonObject(fields), index + 1);
		}
	}
}