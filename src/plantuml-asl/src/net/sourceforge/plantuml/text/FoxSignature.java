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
package net.sourceforge.plantuml.text;

public class FoxSignature {

	private static final long masks[] = new long[127];
	private static final long MASK_SPACES;
	private static final long MASK_SPECIAL1;

	static {
		final String full = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0!\"#$%&\'()*+,-./:;<=>?@[\\]^_{|}~";
		long m = 1L;
		MASK_SPACES = m;
		m = m << 1;
		MASK_SPECIAL1 = m;
		m = m << 1;
		for (int i = 0; i < full.length(); i++) {
			char ch = full.charAt(i);
			masks[ch] = m;
			if (ch >= 'A' && ch <= 'Z') {
				ch = (char) (ch + ('a' - 'A'));
				masks[ch] = m;
			} else if (ch == '.' || ch == '=' || ch == '-' || ch == '~')
				masks[ch] |= MASK_SPECIAL1;
			m = m << 1;
		}
		masks[' '] = MASK_SPACES;
		masks['\t'] = MASK_SPACES;
		masks['\r'] = MASK_SPACES;
		masks['\n'] = MASK_SPACES;
		masks['\f'] = MASK_SPACES;

	}

	public static long getSpecialSpaces() {
		return MASK_SPACES;
	}

	public static long getSpecial1() {
		return MASK_SPECIAL1;
	}

	public static void printMe() {
		for (int i = 0; i < masks.length; i++)
			if (masks[i] > 0) {
				final char ch = (char) i;
				System.err.println("ch=" + ch + " " + masks[i]);
			}

	}

	private static long getMask(char ch) {
		if (ch < masks.length)
			return masks[ch];
		else if (ch == '\u00A0')
			return MASK_SPACES;

		return 0L;
	}

	public static long getFoxSignatureFromRealString(String s) {
		long result = 0;
		for (int i = 0; i < s.length(); i++)
			result = result | getMask(s.charAt(i));
		return result;
	}

	public static long getFoxSignatureFromRegex(String s) {
		long result = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '.') {
				if (s.charAt(i + 1) == '+' || s.charAt(i + 1) == '*')
					i++;
				else
					throw new IllegalArgumentException(s);
			} else if (s.charAt(i) == '\\') {
				if (s.charAt(i + 1) == 'b')
					i++;
				else if (Character.isLetterOrDigit(s.charAt(i + 1)))
					throw new IllegalArgumentException(s);
			} else {
				if (i + 1 < s.length() && (s.charAt(i + 1) == '?' || s.charAt(i + 1) == '*')) {
					i++;
					continue;
				}
				result = result | getMask(s.charAt(i));
				if (i + 1 < s.length() && s.charAt(i + 1) == '+') {
					i++;
				}
			}
		}
		return result;
	}

	public static String backToString(long check) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < masks.length; i++)
			if (masks[i] != 0L && (check & masks[i]) != 0L) {
				final char ch = (char) i;
				sb.append(ch);
			}
		return sb.toString();
	}

}
