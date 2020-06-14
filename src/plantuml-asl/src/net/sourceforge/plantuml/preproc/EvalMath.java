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
package net.sourceforge.plantuml.preproc;

// http://stackoverflow.com/questions/3422673/evaluating-a-math-expression-given-in-string-form
public class EvalMath {

	private final String str;
	private int pos = -1;
	private char ch;

	public EvalMath(String str) {
		this.str = str;
	}

	private void nextChar() {
		pos++;
		if (pos < str.length()) {
			ch = str.charAt(pos);
		} else {
			ch = '\0';
		}
	}

	private boolean eat(int charToEat) {
		while (ch == ' ') {
			nextChar();
		}
		if (ch == charToEat) {
			nextChar();
			return true;
		}
		return false;
	}

	private double parseExpression() {
		double x = parseTerm();
		while (true) {
			if (eat('+')) {
				x += parseTerm();
			} else if (eat('-')) {
				x -= parseTerm();
			} else {
				return x;
			}
		}
	}

	private double parseTerm() {
		double x = parseFactor();
		while (true) {
			if (eat('*')) {
				x *= parseFactor();
			} else if (eat('/')) {
				x /= parseFactor();
			} else {
				return x;
			}
		}
	}

	private double parseFactor() {
		if (eat('+')) {
			return parseFactor();
		}
		if (eat('-')) {
			return -parseFactor();
		}

		final double x;
		final int startPos = pos;
		if (eat('(')) {
			x = parseExpression();
			eat(')');
		} else if ((ch >= '0' && ch <= '9') || ch == '.') {
			while ((ch >= '0' && ch <= '9') || ch == '.')
				nextChar();
			x = Double.parseDouble(str.substring(startPos, pos));
		} else if (ch >= 'a' && ch <= 'z') {
			while (ch >= 'a' && ch <= 'z') {
				nextChar();
			}
			final String func = str.substring(startPos, pos);
			x = parseFactor();
			throw new RuntimeException("Unknown function: " + func);
		} else {
			throw new RuntimeException("Unexpected: " + (char) ch);
		}

		return x;
	}

	public double eval() {
		nextChar();
		double x = parseExpression();
		if (pos < str.length()) {
			throw new RuntimeException("Unexpected: " + (char) ch);
		}
		return x;
	}

	public static void main(String[] args) {
		final EvalMath eval = new EvalMath("33+2*(4+1)");
		System.err.println(eval.eval());
	}
}
