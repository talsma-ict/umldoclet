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
package net.sourceforge.plantuml.regexdiagram;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.utils.CharInspector;

public class RegexExpression {
    // ::remove folder when __HAXE__

	public static List<ReToken> parse(CharInspector it) {
		final List<ReToken> result = new ArrayList<>();
		while (true) {
			final char current = it.peek(0);
			if (current == '\0')
				break;
			// System.err.println("current=" + current);
			if (isStartAnchor(it)) {
				final String s = readAnchor(it);
				result.add(new ReToken(ReTokenType.ANCHOR, s));
			} else if (isEscapedChar(it)) {
				result.add(new ReToken(ReTokenType.ESCAPED_CHAR, "" + it.peek(1)));
				it.jump();
				it.jump();
			} else if (current == '|') {
				result.add(new ReToken(ReTokenType.ALTERNATIVE, "|"));
				it.jump();
			} else if (current == '[') {
				final String s = readGroup(it);
				result.add(new ReToken(ReTokenType.GROUP, s));
			} else if (isStartOpenParenthesis(it)) {
				final String s = readOpenParenthesis(it);
				result.add(new ReToken(ReTokenType.PARENTHESIS_OPEN, s));
			} else if (current == ')') {
				result.add(new ReToken(ReTokenType.PARENTHESIS_CLOSE, ")"));
				it.jump();
			} else if (isStartQuantifier(it)) {
				final String s = readQuantifier(it);
				result.add(new ReToken(ReTokenType.QUANTIFIER, s));
			} else if (isStartClass(it)) {
				final String s = readClass(it);
				result.add(new ReToken(ReTokenType.CLASS, s));
			} else if (isSimpleLetter(current)) {
				result.add(new ReToken(ReTokenType.SIMPLE_CHAR, "" + current));
				it.jump();
			} else {
				throw new IllegalStateException();
			}
		}
		// System.err.println("result=" + result);
		return result;

	}

	private static boolean isStartOpenParenthesis(CharInspector it) {
		final char current0 = it.peek(0);
		if (current0 == '(')
			return true;
		return false;
	}

	private static String readOpenParenthesis(CharInspector it) {
		final char current0 = it.peek(0);
		it.jump();
		final StringBuilder result = new StringBuilder();
		result.append(current0);
		if (it.peek(0) == '?' && it.peek(1) == ':') {
			it.jump();
			it.jump();
			result.append("?:");
		}
		if (it.peek(0) == '?' && it.peek(1) == '!') {
			it.jump();
			it.jump();
			result.append("?!");
		}
		return result.toString();
	}

	private static boolean isStartQuantifier(CharInspector it) {
		final char current0 = it.peek(0);
		if (current0 == '*' || current0 == '+' || current0 == '?' || current0 == '{')
			return true;
		return false;
	}

	private static String readQuantifier(CharInspector it) {
		final char current0 = it.peek(0);
		it.jump();
		final StringBuilder result = new StringBuilder();
		result.append(current0);
		if (current0 == '{')
			while (it.peek(0) != 0) {
				final char ch = it.peek(0);
				result.append(ch);
				it.jump();
				if (ch == '}')
					break;
			}
		if (it.peek(0) == '?') {
			result.append('?');
			it.jump();
		}
		return result.toString();
	}

	private static boolean isEscapedChar(CharInspector it) {
		final char current0 = it.peek(0);
		if (current0 == '\\') {
			final char current1 = it.peek(1);
			if (current1 == '.' || current1 == '*' || current1 == '\\' || current1 == '?' || current1 == '^'
					|| current1 == '$' || current1 == '|' || current1 == '(' || current1 == ')' || current1 == '['
					|| current1 == ']' || current1 == '{' || current1 == '}' || current1 == '<' || current1 == '>')
				return true;
		}
		return false;
	}

	private static String readGroup(CharInspector it) {
		final char current0 = it.peek(0);
		if (current0 != '[')
			throw new IllegalStateException();
		it.jump();
		final StringBuilder result = new StringBuilder();
		while (it.peek(0) != 0) {
			char ch = it.peek(0);
			it.jump();
			if (ch == ']')
				break;
			result.append(ch);
			if (ch == '\\') {
				ch = it.peek(0);
				it.jump();
				result.append(ch);
			}

		}
		return result.toString();
	}

	private static String readClass(CharInspector it) {
		final char current0 = it.peek(0);
		if (current0 == '.') {
			it.jump();
			return "" + current0;
		}
		if (current0 == '\\') {
			it.jump();
			final String result = "" + current0 + it.peek(0);
			it.jump();
			return result;
		}
		throw new IllegalStateException();
	}

	private static boolean isStartClass(CharInspector it) {
		final char current0 = it.peek(0);
		if (current0 == '.')
			return true;
		if (current0 == '\\')
			return true;
		return false;
	}

	private static boolean isSimpleLetter(char ch) {
		if (ch == '\\' || ch == '.')
			return false;
		return true;
	}

	private static boolean isStartAnchor(CharInspector it) {
		final char current0 = it.peek(0);
		if (current0 == '^' || current0 == '$')
			return true;
		if (current0 == '\\') {
			final char current1 = it.peek(1);
			if (current1 == 'A' || current1 == 'Z' || current1 == 'z' || current1 == 'G' || current1 == 'b'
					|| current1 == 'B')
				return true;
		}
		return false;
	}

	private static String readAnchor(CharInspector it) {
		final char current0 = it.peek(0);
		if (current0 == '^' || current0 == '$') {
			it.jump();
			return "" + current0;
		}
		if (current0 == '\\') {
			it.jump();
			final String result = "" + current0 + it.peek(0);
			it.jump();
			return result;
		}
		throw new IllegalStateException();
	}

}
