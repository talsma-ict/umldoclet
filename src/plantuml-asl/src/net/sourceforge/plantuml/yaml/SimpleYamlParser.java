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
package net.sourceforge.plantuml.yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.json.JsonArray;
import net.sourceforge.plantuml.json.JsonObject;
import net.sourceforge.plantuml.json.JsonString;
import net.sourceforge.plantuml.json.JsonValue;

public class SimpleYamlParser {

	private JsonValue result;
	private final List<Integer> pendingIndents = new ArrayList<>();

	public JsonValue parse(List<String> lines) {
		result = new JsonObject();
		pendingIndents.clear();
		pendingIndents.add(0);
		final YamlLines yamlLines = new YamlLines(lines);
		for (String s : yamlLines) {
			parseSingleLine(s);
		}
		return result;

	}

	private String[] nameAndValue(String s) {
		final Pattern p1 = Pattern.compile("^\\s*" + YamlLines.KEY + "\\s*: \\s*(\\S.*)$");
		final Matcher m1 = p1.matcher(s);
		if (m1.matches()) {
			final String name = m1.group(1);
			final String data = m1.group(2).trim();
			return new String[] { name, data };
		}
		return null;
	}

	private void parseSingleLine(String s) {
		final int indent = getIndent(s);
//		System.err.println("s=" + s);

		if (isListStrict(s)) {
			strictMuteToArray(indent);
			return;
		}

		final String listedValue = listedValue(s);
		if (listedValue != null) {
			final JsonArray array = getForceArray(indent);
			array.add(listedValue);
			return;
		}

		final JsonObject working = (JsonObject) getWorking(indent);
		if (working == null) {
			System.err.println("ERROR: ignoring " + s);
			return;
		}

		final String[] nameAndValue = nameAndValue(s);
		if (nameAndValue != null) {
			final String name = nameAndValue[0];
			final String data = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(nameAndValue[1], "\"");
			working.add(name, data);
			return;
		}

		final String nameOnly = YamlLines.nameOnly(s);
		if (nameOnly != null) {
			working.add(nameOnly, new JsonObject());
			return;
		}

		throw new UnsupportedOperationException(s);
	}

	private String listedValue(String s) {
		final Pattern p1 = Pattern.compile("^\\s*[-]\\s*(\\S.*)$");
		final Matcher m1 = p1.matcher(s);
		if (m1.matches()) {
			final String name = m1.group(1).trim();
			return name;
		}
		return null;
	}

	private JsonArray getForceArray(int indent) {
		if (indent == 0 && getLastIndent() == 0) {
			if (result instanceof JsonArray == false) {
				result = new JsonArray();
			}
			return (JsonArray) result;
		}
		while (getLastIndent() > indent - 1)
			pendingIndents.remove(pendingIndents.size() - 1);

		final JsonObject last = (JsonObject) search(result, pendingIndents.size());
		final String field = last.names().get(last.size() - 1);
		if (last.get(field) instanceof JsonArray == false) {
			last.set(field, new JsonArray());
		}
		return (JsonArray) last.get(field);
	}

	private void strictMuteToArray(int indent) {
		if (indent == 0 && getLastIndent() == 0) {
			if (result instanceof JsonArray == false) {
				result = new JsonArray();
			}
			return;
		}
		while (getLastIndent() > indent)
			pendingIndents.remove(pendingIndents.size() - 1);

		if (result instanceof JsonArray) {
			((JsonArray) result).add(new JsonObject());
			return;
		}

		final JsonObject last = (JsonObject) search(result, pendingIndents.size());
		final String field = last.names().get(last.size() - 1);
		if (last.get(field) instanceof JsonArray == false) {
			last.set(field, new JsonArray());
		} else {
			((JsonArray) last.get(field)).add(new JsonObject());
		}
	}

	private boolean isListStrict(String s) {
		return s.trim().equals("-");
	}

	private int getLastIndent() {
		return pendingIndents.get(pendingIndents.size() - 1);
	}

	private JsonValue getWorking(int indent) {
		if (indent > getLastIndent()) {
			pendingIndents.add(indent);
			return search(result, pendingIndents.size());
		}
		if (indent == getLastIndent()) {
			return search(result, pendingIndents.size());
		}
		final int idx = pendingIndents.indexOf(indent);
		if (idx == -1) {
			return null;
		}

		while (pendingIndents.size() > idx + 1)
			pendingIndents.remove(pendingIndents.size() - 1);

		return search(result, pendingIndents.size());
	}

	private static JsonValue search(JsonValue current1, int size) {
		if (current1 instanceof JsonArray) {
			JsonArray array = (JsonArray) current1;

			final JsonValue tmp;
			if (array.size() == 0) {
				tmp = new JsonObject();
				array.add(tmp);
			} else {
				tmp = array.get(array.size() - 1);
			}
			return tmp;
		}
		if (size <= 1) {
			return current1;
		}

		final JsonObject current = (JsonObject) current1;
		final String last = current.names().get(current.size() - 1);
		// System.err.println("last=" + last);
		JsonValue tmp = current.get(last);
		if (tmp instanceof JsonArray) {
			JsonArray array = (JsonArray) tmp;
			if (array.size() == 0) {
				tmp = new JsonObject();
				array.add(tmp);
			} else {
				tmp = array.get(array.size() - 1);
			}
		}
		if (tmp instanceof JsonString) {
			System.err.println("JsonString? " + tmp);
			return null;
		}
		return search(tmp, size - 1);
	}

	private int getIndent(String s) {
		int indent = 0;
		for (int i = 0; i < s.length(); i++) {
			final char ch = s.charAt(i);
			if (ch == ' ' || ch == '\t') {
				indent++;
			} else {
				return indent;
			}
		}
		return 0;
	}

}
