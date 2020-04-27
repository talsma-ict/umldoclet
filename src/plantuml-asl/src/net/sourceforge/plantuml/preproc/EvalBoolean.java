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
package net.sourceforge.plantuml.preproc;

// http://stackoverflow.com/questions/3422673/evaluating-a-math-expression-given-in-string-form
public class EvalBoolean {

	private final String str;
	private int pos = -1;
	private char ch;
	private final Truth truth;

	public EvalBoolean(String str, Truth truth) {
		this.str = str;
		this.truth = truth;
	}

	private void nextChar() {
		pos++;
		if (pos < str.length()) {
			ch = str.charAt(pos);
		} else {
			ch = '\0';
		}
	}

	private boolean eat(char charToEat) {
		while (ch == ' ') {
			nextChar();
		}
		if (ch == charToEat) {
			nextChar();
			return true;
		}
		return false;
	}

	private boolean parseExpression() {
		boolean x = parseTerm();
		while (true) {
			if (eat('|')) {
				eat('|');
				x = x | parseTerm();
			} else {
				return x;
			}
		}
	}

	private boolean parseTerm() {
		boolean x = parseFactor();
		while (true) {
			if (eat('&')) {
				eat('&');
				x = x & parseFactor();
			} else {
				return x;
			}
		}
	}

	private boolean parseFactor() {
		if (eat('!')) {
			return !(parseFactor());
		}

		final boolean x;
		final int startPos = pos;
		if (eat('(')) {
			x = parseExpression();
			eat(')');
		} else if (isIdentifier()) {
			while (isIdentifier()) {
				nextChar();
			}
			final String func = str.substring(startPos, pos);
			x = truth.isTrue(func);
		} else {
			throw new IllegalArgumentException("Unexpected: " + (char) ch);
		}

		return x;
	}

	private boolean isIdentifier() {
		return ch == '_' || ch == '$' || Character.isLetterOrDigit(ch);
	}

	public boolean eval() {
		nextChar();
		final boolean x = parseExpression();
		if (pos < str.length()) {
			throw new IllegalArgumentException("Unexpected: " + (char) ch);
		}
		return x;
	}
}
