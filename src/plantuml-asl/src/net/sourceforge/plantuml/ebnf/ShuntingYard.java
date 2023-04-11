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
package net.sourceforge.plantuml.ebnf;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class ShuntingYard {

	final private List<Token> ouputQueue = new ArrayList<>();
	final private Deque<Token> operatorStack = new ArrayDeque<>();

	public ShuntingYard(Iterator<Token> it) {
		while (it.hasNext()) {
			final Token token = it.next();
//			System.err.println("token=" + token);
//			System.err.println("ouputQueue=" + ouputQueue);
//			System.err.println("operatorStack=" + operatorStack);
			if (token.getSymbol() == Symbol.LITTERAL || token.getSymbol() == Symbol.TERMINAL_STRING1
					|| token.getSymbol() == Symbol.TERMINAL_STRING2 || token.getSymbol() == Symbol.SPECIAL_SEQUENCE) {
				ouputQueue.add(token);
				while (thereIsAnCommentOnTopOfTheOperatorStack())
					ouputQueue.add(operatorStack_removeFirstAbove());

			} else if (token.getSymbol() == Symbol.COMMENT_TOKEN) {
				operatorStack.addFirst(token);

			} else if (token.getSymbol().isOperator()) {
				while (thereIsAnOperatorAtTheTopOfTheOperatorStackWithGreaterPrecedence(token)
						|| thereIsAnCommentOnTopOfTheOperatorStack())
					ouputQueue.add(operatorStack_removeFirstBelow());
				operatorStack.addFirst(token);

			} else if (token.getSymbol() == Symbol.GROUPING_OPEN) {
				operatorStack.addFirst(token);

			} else if (token.getSymbol() == Symbol.GROUPING_CLOSE) {
				while (operatorStack.peekFirst().getSymbol() != Symbol.GROUPING_OPEN)
					ouputQueue.add(operatorStack_removeFirstBelow());
				if (operatorStack.peekFirst().getSymbol() == Symbol.GROUPING_OPEN)
					operatorStack.removeFirst();

			} else if (token.getSymbol() == Symbol.OPTIONAL_OPEN) {
				operatorStack.addFirst(new Token(Symbol.OPTIONAL, null));

			} else if (token.getSymbol() == Symbol.OPTIONAL_CLOSE) {
				while (thereIsAnOperatorAtTheTopOfTheOperatorStack() || thereIsAnCommentOnTopOfTheOperatorStack())
					ouputQueue.add(operatorStack_removeFirstBelow());
				final Token first = operatorStack.removeFirst();
				if (first.getSymbol() != Symbol.OPTIONAL)
					throw new IllegalStateException();
				ouputQueue.add(first);
				while (thereIsAnCommentOnTopOfTheOperatorStack())
					ouputQueue.add(operatorStack_removeFirstAbove());

			} else if (token.getSymbol() == Symbol.REPETITION_OPEN) {
				operatorStack.addFirst(new Token(Symbol.REPETITION_ZERO_OR_MORE, null));

			} else if (token.getSymbol() == Symbol.REPETITION_CLOSE) {
				while (thereIsAnOperatorAtTheTopOfTheOperatorStack() || thereIsAnCommentOnTopOfTheOperatorStack())
					ouputQueue.add(operatorStack_removeFirstBelow());
				final Token first = operatorStack.removeFirst();
				if (first.getSymbol() != Symbol.REPETITION_ZERO_OR_MORE)
					throw new IllegalStateException();
				ouputQueue.add(first);
				while (thereIsAnCommentOnTopOfTheOperatorStack())
					ouputQueue.add(operatorStack_removeFirstAbove());

			} else if (token.getSymbol() == Symbol.REPETITION_MINUS_CLOSE) {
				while (thereIsAnOperatorAtTheTopOfTheOperatorStack() || thereIsAnCommentOnTopOfTheOperatorStack())
					ouputQueue.add(operatorStack_removeFirstBelow());
				final Token first = operatorStack.removeFirst();
				if (first.getSymbol() != Symbol.REPETITION_ZERO_OR_MORE)
					throw new IllegalStateException();
				ouputQueue.add(new Token(Symbol.REPETITION_ONE_OR_MORE, null));
				while (thereIsAnCommentOnTopOfTheOperatorStack())
					ouputQueue.add(operatorStack_removeFirstAbove());

			} else {
				throw new UnsupportedOperationException(token.toString());
			}

		}
		while (operatorStack.isEmpty() == false) {
			final Token token = operatorStack.removeFirst();
			if (token.getSymbol() == Symbol.OPTIONAL || token.getSymbol() == Symbol.REPETITION_ONE_OR_MORE
					|| token.getSymbol() == Symbol.REPETITION_ZERO_OR_MORE) {
				ouputQueue.clear();
				return;
			}
			if (token.getSymbol() == Symbol.COMMENT_TOKEN)
				ouputQueue.add(new Token(Symbol.COMMENT_BELOW, token.getData()));
			else
				ouputQueue.add(token);
		}

		// System.err.println("ouputQueue=" + ouputQueue);
	}

	private Token operatorStack_removeFirstAbove() {
		final Token result = operatorStack.removeFirst();
		if (result.getSymbol() == Symbol.COMMENT_TOKEN)
			return new Token(Symbol.COMMENT_ABOVE, result.getData());
		return result;
	}

	private Token operatorStack_removeFirstBelow() {
		final Token result = operatorStack.removeFirst();
		if (result.getSymbol() == Symbol.COMMENT_TOKEN)
			return new Token(Symbol.COMMENT_BELOW, result.getData());
		return result;
	}

	private boolean thereIsAnCommentOnTopOfTheOperatorStack() {
		final Token top = operatorStack.peekFirst();
		return top != null && top.getSymbol() == Symbol.COMMENT_TOKEN;
	}

	private boolean thereIsAFunctionAtTheTopOfTheOperatorStack() {
		final Token top = operatorStack.peekFirst();
		return top != null && top.getSymbol().isFunction();
	}

	private boolean thereIsAnOperatorAtTheTopOfTheOperatorStack() {
		final Token top = operatorStack.peekFirst();
		return top != null && top.getSymbol().isOperator();
	}

	private boolean thereIsAnOperatorAtTheTopOfTheOperatorStackWithGreaterPrecedence(Token token) {
		final Token top = operatorStack.peekFirst();
		if (top != null && top.getSymbol().isOperator()
				&& top.getSymbol().getPriority() > token.getSymbol().getPriority())
			return true;
		return false;
	}

	public final List<Token> getOuputQueue() {
		return Collections.unmodifiableList(ouputQueue);
	}

}
