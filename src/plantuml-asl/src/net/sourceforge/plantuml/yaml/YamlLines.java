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
package net.sourceforge.plantuml.yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YamlLines implements Iterable<String> {

	public static final String KEY = "([^:]+)";

	private List<String> lines = new ArrayList<>();

	public YamlLines(List<String> rawLines) {
		for (String s : rawLines) {
			if (s.startsWith("#"))
				continue;

			s = removeDiese(s);
			if (s.trim().length() == 0)
				continue;
			lines.add(s);
		}
		mergeMultiline();
		manageSpaceTwoPoints();
		manageList();
		final int startingEmptyCols = startingEmptyCols();
		removeFirstCols(startingEmptyCols);
	}

	private void manageSpaceTwoPoints() {
		for (ListIterator<String> it = lines.listIterator(); it.hasNext();) {
			String s = it.next();
			if (s.contains("\"") == false && s.contains("'") == false && s.contains(":")
					&& s.indexOf(':') == s.lastIndexOf(':') && s.contains(": ") == false) {
				s = s.replace(":", ": ");
				it.set(s);
			}
		}

	}

	private String removeDiese(String s) {
		final int idx = s.indexOf(" #");
		if (idx == -1)
			return s;

		return s.substring(0, idx);
	}

	private void manageList() {
		final List<String> result = new ArrayList<>();
		for (String s : lines) {
			final Pattern p1 = Pattern.compile("^(\\s*[-])(\\s*\\S.*)$");
			final Matcher m1 = p1.matcher(s);
			if (s.contains(": ") && m1.matches()) {
				result.add(m1.group(1));
				result.add(s.replaceFirst("[-]", " "));
			} else if (m1.matches()) {
				result.add(" " + s);
			} else {
				result.add(s);
			}

		}
		this.lines = result;
	}

	private void removeFirstCols(int startingEmptyCols) {
		if (startingEmptyCols == 0)
			return;

		for (ListIterator<String> it = lines.listIterator(); it.hasNext();) {
			final String s = it.next().substring(startingEmptyCols);
			it.set(s);
		}
	}

	private int startingEmptyCols() {
		int result = Integer.MAX_VALUE;
		for (String s : lines) {
			result = Math.min(result, startingSpaces(s));
			if (result == 0)
				return 0;
		}
		return result;
	}

	private static int startingSpaces(String s) {
		final Pattern p1 = Pattern.compile("^(\\s*).*");
		final Matcher m1 = p1.matcher(s);
		if (m1.matches())
			return m1.group(1).length();

		return 0;
	}

	private void mergeMultiline() {
		final List<String> result = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++) {
			final String init = isMultilineStart(i);
			if (init != null) {
				final StringBuilder sb = new StringBuilder(init);
				while (i + 1 < lines.size() && textOnly(lines.get(i + 1))) {
					sb.append(" " + lines.get(i + 1).trim());
					i++;
				}
				result.add(sb.toString());
			} else {
				result.add(lines.get(i));
			}
		}
		this.lines = result;
	}

	private String isMultilineStart(int i) {
		if (nameOnly(lines.get(i)) != null && textOnly(lines.get(i + 1))) {
			final int idx = lines.get(i).indexOf(':');
			return lines.get(i).substring(0, idx + 1);
		}
		return null;
	}

	public static String nameOnly(String s) {
		final Pattern p1 = Pattern.compile("^\\s*" + KEY + "\\s*:\\s*[|>]?\\s*$");
		final Matcher m1 = p1.matcher(s);
		if (m1.matches()) {
			final String name = m1.group(1);
			return name;
		}
		return null;
	}

	private boolean textOnly(String s) {
		if (isList(s))
			return false;
		return s.indexOf(':') == -1;
	}

	private boolean isList(String s) {
		return s.trim().startsWith("-");
	}

	public Iterator<String> iterator() {
		return Collections.unmodifiableList(lines).iterator();
	}

}
