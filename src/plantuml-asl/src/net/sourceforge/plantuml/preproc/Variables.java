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
package net.sourceforge.plantuml.preproc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variables {

	private final List<DefineVariable> all = new ArrayList<>();
	private final String fonctionName;
	private final String definitionQuoted;

	public Variables(String fonctionName, String definitionQuoted) {
		this.fonctionName = fonctionName;
		this.definitionQuoted = definitionQuoted;
	}

	public void add(DefineVariable var) {
		this.all.add(var);
	}

	public int countDefaultValue() {
		int result = 0;
		for (DefineVariable var : all) {
			if (var.getDefaultValue() != null) {
				result++;
			}
		}
		return result;
	}

	public Variables removeSomeDefaultValues(int nb) {
		if (nb == 0) {
			return this;
		}
		final Variables result = new Variables(fonctionName, definitionQuoted);
		for (DefineVariable v : all) {
			if (v.getDefaultValue() != null && nb > 0) {
				result.add(v.removeDefault());
				nb--;
			} else {
				result.add(v);
			}
		}
		if (nb != 0) {
			throw new IllegalArgumentException();
		}
		return result;
	}

	private String newValue;
	private Pattern regex2;

	public String applyOn(String line) {
		if (newValue == null) {
			newValue = definitionQuoted;
			final StringBuilder regex = new StringBuilder("\\b" + fonctionName + "\\(");

			final List<DefineVariable> variables = all;
			boolean appended = false;
			for (int j = 0; j < variables.size(); j++) {
				final DefineVariable variable = variables.get(j);
				final String varName = variable.getName();
				final String var2 = "(##" + varName + "##)|(##" + varName + "\\b)|(\\b" + varName + "##)|(\\b" + varName
						+ "\\b)";
				if (variable.getDefaultValue() == null) {
					regex.append("(?:(?:\\s*\"([^\"]*)\"\\s*)|(?:\\s*'([^']*)'\\s*)|\\s*"
							+ "((?:\\([^()]*\\)|[^,'\"])*?)" + ")");
					final int i = 1 + 3 * j;
					newValue = newValue.replaceAll(var2, "\\$" + i + "\\$" + (i + 1) + "\\$" + (i + 2));
					regex.append(",");
					appended = true;
				} else {
					newValue = newValue.replaceAll(var2, Matcher.quoteReplacement(variable.getDefaultValue()));
				}
			}
			if (appended == true) {
				regex.setLength(regex.length() - 1);
			}
			regex.append("\\)");
			regex2 = Pattern.compile(regex.toString());
		}
		// line = line.replaceAll(regex.toString(), newValue);
		line = regex2.matcher(line).replaceAll(newValue);
		return line;
	}

}
