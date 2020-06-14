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
package net.sourceforge.plantuml.utils;

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.core.DiagramType;

public class StartUtils {

	public static final Pattern2 patternFilename = MyPattern
			.cmpile("^[@\\\\]start[^%s{}%g]+[%s{][%s%g]*([^%g]*?)[%s}%g]*$");

	public static final String PAUSE_PATTERN = "(?i)((?:\\W|\\<[^<>]*\\>)*)[@\\\\]unpause";
	public static final String START_PATTERN = "(?i)((?:[^\\w~]|\\<[^<>]*\\>)*)[@\\\\]start";

	public static String beforeStartUml(final String s) {
		boolean inside = false;
		for (int i = 0; i < s.length(); i++) {
			final String tmp = s.substring(i, s.length());
			if (startsWithSymbolAnd("start", tmp)) {
				return s.substring(0, i);
			}
			final String single = s.substring(i, i + 1);
			if (inside) {
				if (single.equals(">")) {
					inside = false;
				}
				continue;
			}
			if (single.equals("<")) {
				inside = true;
			} else if (single.matches("[\\w~]")) {
				return null;
			}
		}
		return null;
		// final Matcher m = MyPattern.cmpile(START_PATTERN).matcher(result);
		// if (m.find()) {
		// return m.group(1);
		// }
		// return null;
	}

	public static boolean isArobaseStartDiagram(String s) {
		final String s2 = StringUtils.trinNoTrace(s);
		if (s2.startsWith("@") == false && s2.startsWith("\\") == false) {
			return false;
		}
		return DiagramType.getTypeFromArobaseStart(s2) != DiagramType.UNKNOWN;
	}

	public static boolean startsWithSymbolAnd(String value, final StringLocated tmp) {
		return tmp.getString().startsWith("@" + value) || tmp.getString().startsWith("\\" + value);
	}

	public static boolean startsWithSymbolAnd(String value, final String tmp) {
		return tmp.startsWith("@" + value) || tmp.startsWith("\\" + value);
	}

	public static boolean startOrEnd(final StringLocated s) {
		final String s2 = StringUtils.trinNoTrace(s.getString());
		if (s2.startsWith("@") == false && s2.startsWith("\\") == false) {
			return false;
		}
		return startsWithSymbolAnd("end", s2) || DiagramType.getTypeFromArobaseStart(s2) != DiagramType.UNKNOWN;
	}

	public static boolean isArobaseEndDiagram(String s) {
		final String s2 = StringUtils.trinNoTrace(s);
		return startsWithSymbolAnd("end", s2);
	}

	public static boolean isArobasePauseDiagram(String s) {
		final String s2 = StringUtils.trinNoTrace(s);
		return startsWithSymbolAnd("pause", s2);
	}

	public static boolean isArobaseUnpauseDiagram(String s) {
		final String s2 = StringUtils.trinNoTrace(s);
		return startsWithSymbolAnd("unpause", s2);
	}

	public static boolean isExit(CharSequence s) {
		final String s2 = StringUtils.trinNoTrace(s);
		return s2.equals("!exit");
	}

	private static final Pattern2 append = MyPattern.cmpile("^\\W*[@\\\\]append");

	public static StringLocated getPossibleAppend(StringLocated cs) {
		final String s = cs.getString();
		final Matcher2 m = append.matcher(s);
		if (m.find()) {
			final String tmp = s.substring(m.group(0).length(), s.length());
			return new StringLocated(StringUtils.trin(tmp), cs.getLocation(), cs.getPreprocessorError());
		}
		return null;
	}

}
