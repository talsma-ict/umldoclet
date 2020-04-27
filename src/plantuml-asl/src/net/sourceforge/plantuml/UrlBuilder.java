/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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
package net.sourceforge.plantuml;

import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;

public class UrlBuilder {

	public static enum ModeUrl {
		STRICT, ANYWHERE
	}

	// private static String level0() {
	// return "(?:[^{}]|\\{[^{}]*\\})+";
	// }
	//
	// private static String levelN(int n) {
	// if (n == 0) {
	// return level0();
	// }
	// return "(?:[^{}]|\\{" + levelN(n - 1) + "\\})+";
	// }

	// private static final String URL_PATTERN_OLD =
	// "\\[\\[([%g][^%g]+[%g]|[^{}%s\\]\\[]*)(?:[%s]*\\{((?:[^{}]|\\{[^{}]*\\})+)\\})?(?:[%s]*([^\\]\\[]+))?\\]\\]";
	private static final String URL_PATTERN = "\\[\\[([%g][^%g]+[%g])?([\\w\\W]*?)\\]\\]";

	// private static final String URL_PATTERN_BAD = "\\[\\[([%g][^%g]+[%g]|[^{}%s\\]\\[]*)(?:[%s]*\\{" + "(" +
	// levelN(3)
	// + ")" + "\\})?(?:[%s]*([^\\]\\[]+))?\\]\\]";

	private final String topurl;
	private ModeUrl mode;

	public UrlBuilder(String topurl, ModeUrl mode) {
		this.topurl = topurl;
		this.mode = mode;
	}

	public Url getUrl(String s) {
		final Pattern2 p;
		if (mode == ModeUrl.STRICT) {
			p = MyPattern.cmpile("(?i)^" + URL_PATTERN + "$");
		} else if (mode == ModeUrl.ANYWHERE) {
			p = MyPattern.cmpile("(?i).*" + URL_PATTERN + ".*");
		} else {
			throw new IllegalStateException();
		}
		final Matcher2 m = p.matcher(StringUtils.trinNoTrace(s));
		if (m.matches() == false) {
			return null;
		}

		final String quotedPart = m.group(1);
		final String fullpp = m.group(2).replaceAll("\\{scale=([0-9.]+)\\}", "\uE000scale=$1\uE001");

		final int openBracket = openBracketBeforeSpace(fullpp);
		final int closeBracket;
		if (openBracket == -1) {
			closeBracket = -1;
		} else {
			closeBracket = fullpp.lastIndexOf('}');
		}
		final String full = fullpp.replace('\uE000', '{').replace('\uE001', '}');
		if (quotedPart == null) {
			if (openBracket != -1 && closeBracket != -1) {
				return new Url(withTopUrl(full.substring(0, openBracket)),
						full.substring(openBracket + 1, closeBracket), full.substring(closeBracket + 1).trim());
			}
			final int firstSpace = full.indexOf(' ');
			if (firstSpace == -1) {
				return new Url(full, null, null);
			}
			return new Url(withTopUrl(full.substring(0, firstSpace)), null, full.substring(firstSpace + 1).trim());
		}
		if (openBracket != -1 && closeBracket != -1) {
			return new Url(withTopUrl(quotedPart), full.substring(openBracket + 1, closeBracket), full.substring(
					closeBracket + 1).trim());
		}
		return new Url(withTopUrl(quotedPart), null, null);
	}

	// private int openBracketBeforeSpace(final String full) {
	// return full.indexOf('{');
	// }

	private int openBracketBeforeSpace(final String full) {
		// final int firstSpace = full.indexOf(' ');
		final int result = full.indexOf('{');
		// if (result != -1 && full.substring(result).startsWith("{scale")) {
		// return -1;
		// }
		// if (firstSpace == -1 || result == -1) {
		// return result;
		// }
		// assert firstSpace >= 0;
		// assert result >= 0;
		// if (result > firstSpace + 1) {
		// return -1;
		// }
		return result;
	}

	private String withTopUrl(String url) {
		if (url.startsWith("http:") == false && url.startsWith("https:") == false && topurl != null) {
			return topurl + url;
		}
		return url;
	}

	public static String getRegexp() {
		return URL_PATTERN;
	}

	// private static String purgeUrl(final String label) {
	// final Pattern2 p = MyPattern.cmpile("[%s]*" + URL_PATTERN + "[%s]*");
	// final Matcher2 m = p.matcher(label);
	// if (m.find() == false) {
	// return label;
	// }
	// final String url = m.group(0);
	// final int x = label.indexOf(url);
	// return label.substring(0, x) + label.substring(x + url.length());
	// }

}
