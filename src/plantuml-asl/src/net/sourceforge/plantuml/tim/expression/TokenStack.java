/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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
package net.sourceforge.plantuml.tim.expression;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.tim.Eater;
import net.sourceforge.plantuml.tim.EaterException;
import net.sourceforge.plantuml.tim.EaterExceptionLocated;
import net.sourceforge.plantuml.tim.TContext;
import net.sourceforge.plantuml.tim.TMemory;

public class TokenStack {

	final private List<Token> tokens;

//	public boolean isSpecialAffectationWhenFunctionCall() {
//		if (tokens.size() != 1) {
//			return false;
//		}
//		final Token single = tokens.get(0);
//		if (single.getTokenType() != TokenType.PLAIN_TEXT) {
//			return false;
//		}
//		return isSpecialAffectationWhenFunctionCall(single.getSurface());
//	}
//
//	public static boolean isSpecialAffectationWhenFunctionCall(String surface) {
//		final int idx = surface.indexOf('=');
//		if (idx <= 0) {
//			return false;
//		}
//		if (TLineType.isLetterOrUnderscoreOrDollar(surface.charAt(0)) == false) {
//			return false;
//		}
//		for (int i = 1; i < idx; i++) {
//			if (TLineType.isLetterOrUnderscoreOrDigit(surface.charAt(i)) == false) {
//				return false;
//			}
//		}
//		return true;
//	}

	public TokenStack() {
		this(new ArrayList<Token>());
	}

	private TokenStack(List<Token> list) {
		this.tokens = list;
	}

	public int size() {
		return tokens.size();
	}

	public TokenStack subTokenStack(int i) {
		return new TokenStack(Collections.unmodifiableList(tokens.subList(i, tokens.size())));
	}

	@Override
	public String toString() {
		return tokens.toString();
	}

	public void add(Token token) {
		this.tokens.add(token);
	}

	public TokenStack withoutSpace() {
		final TokenStack result = new TokenStack();
		for (Token token : tokens) {
			if (token.getTokenType() != TokenType.SPACES) {
				result.add(token);
			}
		}
		return result;
	}

	static public TokenStack eatUntilCloseParenthesisOrComma(Eater eater) throws EaterException {
		final TokenStack result = new TokenStack();
		int level = 0;
		while (true) {
			eater.skipSpaces();
			final char ch = eater.peekChar();
			if (ch == 0) {
				throw EaterException.unlocated("until001");
			}
			if (level == 0 && (ch == ',' || ch == ')')) {
				return result;
			}
			final Token token = TokenType.eatOneToken(eater, false);
			final TokenType type = token.getTokenType();
			if (type == TokenType.OPEN_PAREN_MATH) {
				level++;
			} else if (type == TokenType.CLOSE_PAREN_MATH) {
				level--;
			}
			result.add(token);
		}
	}

	static public void eatUntilCloseParenthesisOrComma(TokenIterator it) throws EaterException {
		int level = 0;
		while (true) {
			final Token ch = it.peekToken();
			if (ch == null) {
				throw EaterException.unlocated("until002");
			}
			final TokenType typech = ch.getTokenType();
			if (level == 0 && (typech == TokenType.COMMA || typech == TokenType.CLOSE_PAREN_MATH)
					|| typech == TokenType.CLOSE_PAREN_FUNC) {
				return;
			}
			final Token token = it.nextToken();
			final TokenType type = token.getTokenType();
			if (type == TokenType.OPEN_PAREN_MATH || type == TokenType.OPEN_PAREN_FUNC) {
				level++;
			} else if (type == TokenType.CLOSE_PAREN_MATH || type == TokenType.CLOSE_PAREN_FUNC) {
				level--;
			}
		}
	}

	private int countFunctionArg(TokenIterator it) throws EaterException {
		// return 42;
		final TokenType type1 = it.peekToken().getTokenType();
		if (type1 == TokenType.CLOSE_PAREN_MATH || type1 == TokenType.CLOSE_PAREN_FUNC) {
			return 0;
		}
		int result = 1;
		while (it.hasMoreTokens()) {
			eatUntilCloseParenthesisOrComma(it);
			final Token token = it.nextToken();
			final TokenType type = token.getTokenType();
			if (type == TokenType.CLOSE_PAREN_MATH || type == TokenType.CLOSE_PAREN_FUNC) {
				return result;
			} else if (type == TokenType.COMMA) {
				result++;
			} else {
				throw EaterException.unlocated("count13");
			}
		}
		throw EaterException.unlocated("count12");
	}

	public void guessFunctions() throws EaterException {
		final Deque<Integer> open = new ArrayDeque<>();
		final Map<Integer, Integer> parens = new HashMap<Integer, Integer>();
		for (int i = 0; i < tokens.size(); i++) {
			final Token token = tokens.get(i);
			if (token.getTokenType().equals(TokenType.OPEN_PAREN_MATH)) {
				open.addFirst(i);
			} else if (token.getTokenType().equals(TokenType.CLOSE_PAREN_MATH)) {
				parens.put(open.pollFirst(), i);
			}
		}
		// System.err.println("before=" + toString());
		// System.err.println("guessFunctions2" + parens);
		for (Map.Entry<Integer, Integer> ids : parens.entrySet()) {
			final int iopen = ids.getKey();
			final int iclose = ids.getValue();
			assert tokens.get(iopen).getTokenType() == TokenType.OPEN_PAREN_MATH;
			assert tokens.get(iclose).getTokenType() == TokenType.CLOSE_PAREN_MATH;
			if (iopen > 0 && tokens.get(iopen - 1).getTokenType() == TokenType.PLAIN_TEXT) {
				tokens.set(iopen - 1, new Token(tokens.get(iopen - 1).getSurface(), TokenType.FUNCTION_NAME, null));
				final int nbArg = countFunctionArg(subTokenStack(iopen + 1).tokenIterator());
				tokens.set(iopen, new Token("" + nbArg, TokenType.OPEN_PAREN_FUNC, null));
				tokens.set(iclose, new Token(")", TokenType.CLOSE_PAREN_FUNC, null));
			}
		}
		// System.err.println("after=" + toString());
	}

	class InternalIterator implements TokenIterator {

		private int pos = 0;

		public Token peekToken() {
			return tokens.get(pos);
		}

		public Token nextToken() {
			if (hasMoreTokens() == false) {
				return null;
			}
			return tokens.get(pos++);
		}

		public boolean hasMoreTokens() {
			return pos < tokens.size();
		}

	}

	public TokenIterator tokenIterator() {
		return new InternalIterator();
	}

	public TValue getResult(LineLocation location, TContext context, TMemory memory)
			throws EaterException, EaterExceptionLocated {
		final Knowledge knowledge = context.asKnowledge(memory, location);
		final TokenStack tmp = withoutSpace();
		tmp.guessFunctions();
		final TokenIterator it = tmp.tokenIterator();
		final ShuntingYard shuntingYard = new ShuntingYard(it, knowledge);
		final ReversePolishInterpretor rpn = new ReversePolishInterpretor(location, shuntingYard.getQueue(), knowledge,
				memory, context);
		return rpn.getResult();

	}

}
