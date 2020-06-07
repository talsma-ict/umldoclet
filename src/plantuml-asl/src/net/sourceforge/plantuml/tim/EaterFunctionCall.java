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
package net.sourceforge.plantuml.tim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.tim.expression.TValue;
import net.sourceforge.plantuml.tim.expression.TokenStack;

public class EaterFunctionCall extends Eater {

	private final List<TValue> values = new ArrayList<TValue>();
	private final Map<String, TValue> namedArguments = new HashMap<String, TValue>();
	private final boolean isLegacyDefine;
	private final boolean unquoted;

	public EaterFunctionCall(StringLocated s, boolean isLegacyDefine, boolean unquoted) {
		super(s);
		this.isLegacyDefine = isLegacyDefine;
		this.unquoted = unquoted;
	}

	@Override
	public void analyze(TContext context, TMemory memory) throws EaterException, EaterExceptionLocated {
		skipUntilChar('(');
		checkAndEatChar('(');
		skipSpaces();
		if (peekChar() == ')') {
			checkAndEatChar(')');
			return;
		}
		while (true) {
			skipSpaces();
			if (isLegacyDefine) {
				final String read = eatAndGetOptionalQuotedString();
				final String value = context.applyFunctionsAndVariables(memory, getLineLocation(), read);
				final TValue result = TValue.fromString(value);
				values.add(result);
			} else if (unquoted) {
				final String read = eatAndGetOptionalQuotedString();
				if (TokenStack.isSpecialAffectationWhenFunctionCall(read)) {
					updateNamedArguments(read, context, memory);
				} else {
					final String value = context.applyFunctionsAndVariables(memory, getLineLocation(), read);
					final TValue result = TValue.fromString(value);
					values.add(result);
				}
			} else {
				final TokenStack tokens = TokenStack.eatUntilCloseParenthesisOrComma(this).withoutSpace();
				if (tokens.isSpecialAffectationWhenFunctionCall()) {
					final String special = tokens.tokenIterator().nextToken().getSurface();
					updateNamedArguments(special, context, memory);
				} else {
					tokens.guessFunctions();
					final TValue result = tokens.getResult(getLineLocation(), context, memory);
					values.add(result);
				}
			}
			skipSpaces();
			final char ch = eatOneChar();
			if (ch == ',') {
				continue;
			}
			if (ch == ')') {
				break;
			}
			throw EaterException.located("call001");
		}
	}

	private void updateNamedArguments(String special, TContext context, TMemory memory)
			throws EaterException, EaterExceptionLocated {
		assert special.contains("=");
		final StringEater stringEater = new StringEater(special);
		final String varname = stringEater.eatAndGetVarname();
		stringEater.checkAndEatChar('=');
		final TValue expr = stringEater.eatExpression(context, memory);
		namedArguments.put(varname, expr);
	}

	public final List<TValue> getValues() {
		return Collections.unmodifiableList(values);
	}

	public final Map<String, TValue> getNamedArguments() {
		return Collections.unmodifiableMap(namedArguments);
	}

	public final String getEndOfLine() throws EaterException {
		return this.eatAllToEnd();
	}

}
