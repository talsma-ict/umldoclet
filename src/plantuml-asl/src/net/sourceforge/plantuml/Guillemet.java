/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml;

import java.util.StringTokenizer;
import java.util.regex.Matcher;

public class Guillemet {

	public static final Guillemet NONE = new Guillemet("", "");
	public static final Guillemet DOUBLE_COMPARATOR = new Guillemet("<<", ">>");
	public static final Guillemet GUILLEMET = new Guillemet("\u00AB", "\u00BB");

	private final String start;
	private final String end;

	public Guillemet fromDescription(String value) {
		if (value != null) {
			if ("false".equalsIgnoreCase(value)) {
				return Guillemet.DOUBLE_COMPARATOR;
			}
			if ("<< >>".equalsIgnoreCase(value)) {
				return Guillemet.DOUBLE_COMPARATOR;
			}
			if ("none".equalsIgnoreCase(value)) {
				return Guillemet.NONE;
			}
			if (value.contains(" ")) {
				final StringTokenizer st = new StringTokenizer(value, " ");
				return new Guillemet(st.nextToken(), st.nextToken());
			}
		}
		return Guillemet.GUILLEMET;
	}

	private Guillemet(String start, String end) {
		this.start = start;
		this.end = end;

	}

	public String manageGuillemet(String st) {
		if (this == DOUBLE_COMPARATOR) {
			return st;
		}
		return st.replaceAll("\\<\\<\\s?((?:\\<&\\w+\\>|[^<>])+?)\\s?\\>\\>", Matcher.quoteReplacement(start) + "$1"
				+ Matcher.quoteReplacement(end));
	}

	public String manageGuillemetStrict(String st) {
		if (this == DOUBLE_COMPARATOR) {
			return st;
		}
		if (st.startsWith("<< ")) {
			st = start + st.substring(3);
		} else if (st.startsWith("<<")) {
			st = start + st.substring(2);
		}
		if (st.endsWith(" >>")) {
			st = st.substring(0, st.length() - 3) + end;
		} else if (st.endsWith(">>")) {
			st = st.substring(0, st.length() - 2) + end;
		}
		return st;
	}

}
