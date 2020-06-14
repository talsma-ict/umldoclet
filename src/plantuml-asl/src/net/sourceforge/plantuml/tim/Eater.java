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
package net.sourceforge.plantuml.tim;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.json.Json;
import net.sourceforge.plantuml.json.JsonValue;
import net.sourceforge.plantuml.tim.expression.TValue;
import net.sourceforge.plantuml.tim.expression.Token;
import net.sourceforge.plantuml.tim.expression.TokenStack;
import net.sourceforge.plantuml.tim.expression.TokenType;

public abstract class Eater {

	private int i = 0;
	private final String s;

	public Eater(String s) {
		this.s = s;
	}

	public abstract void execute(TContext context, TMemory memory) throws EaterException;

	public int getCurrentPosition() {
		return i;
	}

	final protected String eatAllToEnd() throws EaterException {
		final String result = s.substring(i);
		i = s.length();
		return result;
	}

	final protected TValue eatExpression(TContext context, TMemory memory) throws EaterException {
		if (peekChar() == '{') {
			String data = eatAllToEnd();
			System.err.println("data=" + data);
			JsonValue json = Json.parse(data);
			System.err.println("json=" + json);
			return TValue.fromJson(json);
		}
		final TokenStack tokenStack = new TokenStack();
		addIntoTokenStack(tokenStack, false);
		return tokenStack.getResult(context, memory);
	}

	final protected TValue eatExpressionStopAtColon(TContext context, TMemory memory) throws EaterException {
		final TokenStack tokenStack = new TokenStack();
		addIntoTokenStack(tokenStack, true);
		return tokenStack.getResult(context, memory);
	}

	final protected void addIntoTokenStack(TokenStack tokenStack, boolean stopAtColon) throws EaterException {
		while (true) {
			final Token token = TokenType.eatOneToken(this, stopAtColon);
			// System.err.println("token=" + token);
			if (token == null) {
				return;
			}
			tokenStack.add(token);
		}
	}

	final public String eatAndGetQuotedString() throws EaterException {
		final char separator = peekChar();
		if (TLineType.isQuote(separator) == false) {
			throw new EaterException("quote10");
		}
		checkAndEatChar(separator);
		final StringBuilder value = new StringBuilder();
		addUpTo(separator, value);
		checkAndEatChar(separator);
		return value.toString();
	}

	final protected String eatAndGetOptionalQuotedString() throws EaterException {
		final char quote = peekChar();
		if (TLineType.isQuote(quote)) {
			return eatAndGetQuotedString();
		}
		final StringBuilder value = new StringBuilder();
		addUpTo(',', ')', value);
		return value.toString();
	}

	final public String eatAndGetNumber() throws EaterException {
		final StringBuilder result = new StringBuilder();
		while (true) {
			final char ch = peekChar();
			if (ch == 0 || TLineType.isLatinDigit(ch) == false) {
				return result.toString();
			}
			result.append(eatOneChar());
		}
	}

	final public String eatAndGetSpaces() throws EaterException {
		final StringBuilder result = new StringBuilder();
		while (true) {
			final char ch = peekChar();
			if (ch == 0 || TLineType.isSpaceChar(ch) == false) {
				return result.toString();
			}
			result.append(eatOneChar());
		}
	}

	final protected String eatAndGetVarname() throws EaterException {
		final StringBuilder varname = new StringBuilder("" + eatOneChar());
		if (TLineType.isLetterOrUnderscoreOrDollar(varname.charAt(0)) == false) {
			throw new EaterException("a002");
		}
		addUpToLastLetterOrUnderscoreOrDigit(varname);
		return varname.toString();
	}

	final protected String eatAndGetFunctionName() throws EaterException {
		final StringBuilder varname = new StringBuilder("" + eatOneChar());
		if (TLineType.isLetterOrUnderscoreOrDollar(varname.charAt(0)) == false) {
			throw new EaterException("a003");
		}
		addUpToLastLetterOrUnderscoreOrDigit(varname);
		return varname.toString();
	}

	final public void skipSpaces() {
		while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
			i++;
		}
	}

	final protected void skipUntilChar(char ch) {
		while (i < s.length() && s.charAt(i) != ch) {
			i++;
		}
	}

	final public char peekChar() {
		if (i >= s.length()) {
			return 0;
		}
		return s.charAt(i);
	}

	final public char peekCharN2() {
		if (i + 1 >= s.length()) {
			return 0;
		}
		return s.charAt(i + 1);
	}

	final protected boolean hasNextChar() {
		return i < s.length();
	}

	final public char eatOneChar() {
		final char ch = s.charAt(i);
		i++;
		return ch;
	}

	final protected void checkAndEatChar(char ch) throws EaterException {
		if (i >= s.length() || s.charAt(i) != ch) {
			throw new EaterException("a001");
		}
		i++;
	}

	final protected boolean safeCheckAndEatChar(char ch) throws EaterException {
		if (i >= s.length() || s.charAt(i) != ch) {
			return false;
		}
		i++;
		return true;
	}

	final protected void optionallyEatChar(char ch) throws EaterException {
		if (i >= s.length() || s.charAt(i) != ch) {
			return;
		}
		assert s.charAt(i) == ch;
		i++;
	}

	final protected void checkAndEatChar(String s) throws EaterException {
		for (int j = 0; j < s.length(); j++) {
			checkAndEatChar(s.charAt(j));
		}
	}

	final protected void addUpToLastLetterOrUnderscoreOrDigit(StringBuilder sb) {
		while (i < s.length()) {
			final char ch = s.charAt(i);
			if (TLineType.isLetterOrUnderscoreOrDigit(ch) == false) {
				return;
			}
			i++;
			sb.append(ch);
		}
	}

	final protected void addUpTo(char separator, StringBuilder sb) {
		while (i < s.length()) {
			final char ch = peekChar();
			if (ch == separator) {
				return;
			}
			i++;
			sb.append(ch);
		}
	}

	final protected void addUpTo(char separator1, char separator2, StringBuilder sb) {
		while (i < s.length()) {
			final char ch = peekChar();
			if (ch == separator1 || ch == separator2) {
				return;
			}
			i++;
			sb.append(ch);
		}
	}

	final protected TFunctionImpl eatDeclareFunction(TContext context, TMemory memory, boolean unquoted,
			LineLocation location, boolean allowNoParenthesis) throws EaterException {
		final List<TFunctionArgument> args = new ArrayList<TFunctionArgument>();
		final String functionName = eatAndGetFunctionName();
		skipSpaces();
		if (safeCheckAndEatChar('(') == false) {
			if (allowNoParenthesis) {
				return new TFunctionImpl(functionName, args, unquoted);
			}
			throw new EaterException("Missing opening parenthesis");
		}
		while (true) {
			skipSpaces();
			char ch = peekChar();
			if (TLineType.isLetterOrUnderscoreOrDollar(ch)) {
				final String varname = eatAndGetVarname();
				skipSpaces();
				final TValue defValue;
				if (peekChar() == '=') {
					eatOneChar();
					final TokenStack def = TokenStack.eatUntilCloseParenthesisOrComma(this);
					def.guessFunctions();
					defValue = def.getResult(context, memory);
					// System.err.println("result=" + defValue);
				} else {
					defValue = null;
				}
				args.add(new TFunctionArgument(varname, defValue));
			} else if (ch == ',') {
				checkAndEatChar(',');
			} else if (ch == ')') {
				checkAndEatChar(")");
				break;
			} else {
				throw new EaterException("Error in function definition");
			}
		}
		skipSpaces();
		return new TFunctionImpl(functionName, args, unquoted);
	}

	final protected TFunctionImpl eatDeclareFunctionWithOptionalReturn(TContext context, TMemory memory,
			boolean unquoted, LineLocation location) throws EaterException {
		final TFunctionImpl result = eatDeclareFunction(context, memory, unquoted, location, false);
		if (peekChar() == 'r') {
			checkAndEatChar("return");
			skipSpaces();
			final String line = "!return " + eatAllToEnd();
			result.addBody(new StringLocated(line, location));
		} else if (peekChar() == '!') {
			checkAndEatChar("!return");
			skipSpaces();
			final String line = "!return " + eatAllToEnd();
			result.addBody(new StringLocated(line, location));
		}
		return result;
	}

}
