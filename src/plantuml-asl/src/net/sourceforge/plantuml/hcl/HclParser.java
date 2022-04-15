/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.hcl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.plantuml.json.Json;
import net.sourceforge.plantuml.json.JsonArray;
import net.sourceforge.plantuml.json.JsonObject;
import net.sourceforge.plantuml.json.JsonString;
import net.sourceforge.plantuml.json.JsonValue;

public class HclParser {

	private final List<HclTerm> terms = new ArrayList<HclTerm>();

	public HclParser(Iterable<Character> source) {
		parse(source.iterator());
	}

	public JsonObject parseMe() {
		final Map<String, JsonObject> map = new LinkedHashMap<>();
		final Iterator<HclTerm> it = terms.iterator();
		while (it.hasNext())
			map.putAll(getModuleOrSomething(it));

		if (map.size() == 1)
			return map.values().iterator().next();

		final JsonObject result = new JsonObject();
		for (Entry<String, JsonObject> ent : map.entrySet())
			result.add(ent.getKey(), ent.getValue());

		return result;
	}

	private Map<String, JsonObject> getModuleOrSomething(Iterator<HclTerm> it) {
		final StringBuilder name = new StringBuilder();
		while (true) {
			final HclTerm current = it.next();
			if (current.is(SymbolType.STRING_QUOTED))
				name.append("\"" + current.getData() + "\" ");
			else if (current.is(SymbolType.STRING_SIMPLE))
				name.append(current.getData() + " ");
			else if (current.is(SymbolType.CURLY_BRACKET_OPEN)) {
				return Collections.singletonMap(name.toString().trim(), getBracketData(it));
			} else
				throw new IllegalStateException(current.toString());
		}
	}

	private JsonValue getFunctionData(String functionName, Iterator<HclTerm> it) {
		final JsonArray args = new JsonArray();
		if (it.next().is(SymbolType.PARENTHESIS_OPEN) == false)
			throw new IllegalStateException();

		while (true) {
			final Object value = getValue(it);
			if (value instanceof HclTerm && ((HclTerm) value).is(SymbolType.PARENTHESIS_CLOSE)) {
				if (args.size() == 0)
					return Json.value(functionName + "()");
				final JsonObject result = new JsonObject();
				result.add(functionName + "()", args);
				return result;
			}
			if (value instanceof HclTerm && ((HclTerm) value).is(SymbolType.COMMA))
				continue;

			if (value instanceof String)
				args.add((String) value);
			else if (value instanceof JsonArray)
				args.add((JsonArray) value);
			else if (value instanceof JsonObject)
				args.add((JsonObject) value);
			else if (value instanceof JsonString)
				args.add((JsonString) value);
			else
				throw new IllegalStateException();

		}
	}

	private JsonObject getBracketData(Iterator<HclTerm> it) {
		final JsonObject result = new JsonObject();
		while (true) {
			final HclTerm current = it.next();
			if (current.is(SymbolType.CURLY_BRACKET_CLOSE))
				return result;
			if (current.is(SymbolType.STRING_SIMPLE) || current.is(SymbolType.STRING_QUOTED)) {
				final String fieldName = current.getData();
				final HclTerm next = it.next();
				if (next.is(SymbolType.EQUALS, SymbolType.TWO_POINTS) == false)
					throw new IllegalStateException(current.toString());
				final Object value = getValue(it);
				if (value instanceof String)
					result.add(fieldName, (String) value);
				else if (value instanceof JsonArray)
					result.add(fieldName, (JsonArray) value);
				else if (value instanceof JsonObject)
					result.add(fieldName, (JsonObject) value);
				else if (value instanceof JsonString)
					result.add(fieldName, (JsonString) value);
				else
					throw new IllegalStateException();

			} else
				throw new IllegalStateException(current.toString());
		}
	}

	private Object getValue(Iterator<HclTerm> it) {
		final HclTerm current = it.next();
		if (current.is(SymbolType.COMMA, SymbolType.PARENTHESIS_CLOSE))
			return current;
		if (current.is(SymbolType.STRING_QUOTED))
			return current.getData();
		if (current.is(SymbolType.STRING_SIMPLE))
			return current.getData();
		if (current.is(SymbolType.SQUARE_BRACKET_OPEN))
			return getArray(it);
		if (current.is(SymbolType.CURLY_BRACKET_OPEN))
			return getBracketData(it);
		if (current.is(SymbolType.FUNCTION_NAME))
			return getFunctionData(current.getData(), it);
		throw new IllegalStateException(current.toString());
	}

	private Object getArray(Iterator<HclTerm> it) {
		final JsonArray result = new JsonArray();
		while (true) {
			final HclTerm current = it.next();
			if (current.is(SymbolType.CURLY_BRACKET_OPEN))
				result.add(getBracketData(it));
			if (current.is(SymbolType.SQUARE_BRACKET_CLOSE))
				return result;
			if (current.is(SymbolType.COMMA))
				continue;
			if (current.is(SymbolType.STRING_QUOTED))
				result.add(current.getData());
		}
	}

	@Override
	public String toString() {
		return terms.toString();
	}

	private void parse(Iterator<Character> it) {
		final StringBuilder pendingString = new StringBuilder();
		while (it.hasNext()) {
			final char c = it.next();
			final SymbolType type = getType(c);
			if (type == SymbolType.PARENTHESIS_OPEN) {
				if (pendingString.length() == 0)
					throw new IllegalArgumentException();
				terms.add(new HclTerm(SymbolType.FUNCTION_NAME, pendingString.toString()));
				pendingString.setLength(0);
			} else if (type != null && pendingString.length() > 0) {
				terms.add(new HclTerm(SymbolType.STRING_SIMPLE, pendingString.toString()));
				pendingString.setLength(0);
			}

			if (type == SymbolType.SPACE)
				continue;

			if (type != null) {
				terms.add(new HclTerm(type));
				continue;
			}
			if (c == '\"') {
				final String s = eatUntilDoubleQuote(it);
				terms.add(new HclTerm(SymbolType.STRING_QUOTED, s));
				continue;
			}
			pendingString.append(c);
		}
	}

	private String eatUntilDoubleQuote(Iterator<Character> it) {
		final StringBuilder sb = new StringBuilder();
		while (it.hasNext()) {
			final char c = it.next();
			if (c == '\\') {
				sb.append(it.next());
				continue;
			}
			if (c == '\"')
				return sb.toString();
			sb.append(c);
		}
		return sb.toString();
	}

	private SymbolType getType(final char c) {
		if (Character.isSpaceChar(c))
			return SymbolType.SPACE;
		else if (c == '{')
			return SymbolType.CURLY_BRACKET_OPEN;
		else if (c == '}')
			return SymbolType.CURLY_BRACKET_CLOSE;
		else if (c == '[')
			return SymbolType.SQUARE_BRACKET_OPEN;
		else if (c == ']')
			return SymbolType.SQUARE_BRACKET_CLOSE;
		else if (c == '(')
			return SymbolType.PARENTHESIS_OPEN;
		else if (c == ')')
			return SymbolType.PARENTHESIS_CLOSE;
		else if (c == '=')
			return SymbolType.EQUALS;
		else if (c == ',')
			return SymbolType.COMMA;
		else if (c == ':')
			return SymbolType.TWO_POINTS;

		return null;
	}

}
