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
package net.sourceforge.plantuml.tim;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.json.JsonArray;
import net.sourceforge.plantuml.json.JsonObject;
import net.sourceforge.plantuml.json.JsonValue;
import net.sourceforge.plantuml.tim.expression.TValue;

public class VariableManager {

	private final TMemory memory;
	private final TContext context;
	private final LineLocation location;

	public VariableManager(TContext context, TMemory memory, LineLocation location) {
		this.memory = memory;
		this.context = context;
		this.location = location;
	}

	public int replaceVariables(String str, int i, StringBuilder result) throws EaterException, EaterExceptionLocated {
		final String presentVariable = getVarnameAt(str, i);
		if (result.toString().endsWith("##")) {
			result.setLength(result.length() - 2);
		}
		final TValue value = memory.getVariable(presentVariable);
		i += presentVariable.length() - 1;
		if (value.isJson()) {
			if (value.toJson().isString()) {
				result.append(value.toJson().asString());
			} else if (value.toJson().isNumber()) {
				result.append(value.toJson().toString());
			} else {
				JsonValue jsonValue = (JsonObject) value.toJson();
				i++;
				i = replaceJson(jsonValue, str, i, result) - 1;
			}
		} else {
			result.append(value.toString());
		}
		if (i + 2 < str.length() && str.charAt(i + 1) == '#' && str.charAt(i + 2) == '#') {
			i += 2;
		}
		return i;
	}

	private int replaceJson(JsonValue jsonValue, String str, int i, StringBuilder result)
			throws EaterException, EaterExceptionLocated {
		while (i < str.length()) {
			final char n = str.charAt(i);
			if (n == '.') {
				i++;
				final StringBuilder fieldName = new StringBuilder();
				while (i < str.length()) {
					if (Character.isJavaIdentifierPart(str.charAt(i)) == false) {
						break;
					}
					fieldName.append(str.charAt(i));
					i++;
				}
				jsonValue = ((JsonObject) jsonValue).get(fieldName.toString());
			} else if (n == '[') {
				i++;
				final StringBuilder inBracket = new StringBuilder();
				int level = 0;
				while (true) {
					if (str.charAt(i) == '[') {
						level++;
					}
					if (str.charAt(i) == ']') {
						if (level == 0)
							break;
						level--;
					}
					inBracket.append(str.charAt(i));
					i++;
				}
				final String nbString = context.applyFunctionsAndVariables(memory, location, inBracket.toString());
				if (jsonValue instanceof JsonArray) {
					final int nb = Integer.parseInt(nbString);
					jsonValue = ((JsonArray) jsonValue).get(nb);
				} else if (jsonValue instanceof JsonObject) {
					jsonValue = ((JsonObject) jsonValue).get(nbString);
				} else {
					throw EaterException.unlocated("Major parsing error");
				}
				if (jsonValue == null) {
					throw EaterException.unlocated("Data parsing error");
				}
				i++;
			} else {
				break;
			}
		}
		if (jsonValue != null) {
			if (jsonValue.isString()) {
				result.append(jsonValue.asString());
			} else {
				result.append(jsonValue.toString());
			}
		}
		return i;
	}

	public String getVarnameAt(String s, int pos) {
		if (pos > 0 && TLineType.isLetterOrUnderscoreOrDigit(s.charAt(pos - 1))
				&& justAfterBackslashN(s, pos) == false) {
			return null;
		}
		final String varname = memory.variablesNames3().getLonguestMatchStartingIn(s.substring(pos));
		if (varname.length() == 0) {
			return null;
		}
		if (pos + varname.length() == s.length()
				|| TLineType.isLetterOrUnderscoreOrDigit(s.charAt(pos + varname.length())) == false) {
			return varname;
		}
		return null;
	}

	public static boolean justAfterBackslashN(String s, int pos) {
		return pos > 1 && s.charAt(pos - 2) == '\\' && s.charAt(pos - 1) == 'n';
	}

}
