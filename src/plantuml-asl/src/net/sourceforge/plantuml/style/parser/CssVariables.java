/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.style.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CssVariables {

	private final Map<String, String> variables = new HashMap<>();

	private final Pattern learnPattern = Pattern.compile("^--([_\\w][-_\\w]+)[ :]+(.*?);?");
	private final Pattern retrieve = Pattern.compile("var\\(-*([_\\w][-_\\w]+)\\)");

	public void learn(String s) {
		final Matcher m = learnPattern.matcher(s);
		if (m.matches())
			variables.put(m.group(1), m.group(2));
	}

	public void learn(String var, String value) {
		if (var.startsWith("--"))
			var = var.substring(2);
		variables.put(var, value);
	}

	public String value(String v) {
		if (v.startsWith("var(")) {
			final Matcher m = retrieve.matcher(v);
			if (m.matches()) {
				final String varname = m.group(1);
				final String result = variables.get(varname);
				if (result != null)
					return result;
			}
		}
		return v;
	}

}
