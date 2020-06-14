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

public class CharHidder {

	public static String addTileAtBegin(String s) {
		return "~" + s;
	}

	public static String hide(String s) {
		// System.err.println("hide " + s);
		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if (c == '~' && i + 1 < s.length()) {
				i++;
				final char c2 = s.charAt(i);
				if (isToBeHidden(c2)) {
					result.append(hideChar(c2));
				} else {
					result.append(c);
					result.append(c2);
				}

			} else {
				result.append(c);
			}
		}
		// System.err.println("---> " + result);
		return result.toString();
	}

	private static boolean isToBeHidden(final char c) {
		if (c == '_' || c == '-' || c == '\"' || c == '#' || c == ']' || c == '[' || c == '*' || c == '.' || c == '/'
				|| c == '<') {
			return true;
		}
		return false;
	}

	private static char hideChar(char c) {
		if (c > 255) {
			throw new IllegalArgumentException();
		}
		return (char) ('\uE000' + c);
	}

	private static char unhideChar(char c) {
		if (c >= '\uE000' && c <= '\uE0FF') {
			return (char) (c - '\uE000');
		}
		return c;
	}

	public static String unhide(String s) {
		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			result.append(unhideChar(c));
		}
		// System.err.println("unhide " + result);
		return result.toString();
	}

}
