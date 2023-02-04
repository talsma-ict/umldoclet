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
package net.sourceforge.plantuml.regex;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class ShuntingYard {

	final private List<ReToken> ouputQueue = new ArrayList<>();
	final private Deque<ReToken> operatorStack = new ArrayDeque<>();

	public ShuntingYard(Iterator<ReToken> it) {
		while (it.hasNext()) {
			final ReToken token = it.next();
//			System.err.println("token=" + token);
//			System.err.println("ouputQueue=" + ouputQueue);
//			System.err.println("operatorStack=" + operatorStack);
			if (token.getType() == ReTokenType.SIMPLE_CHAR) {
				ouputQueue.add(token);
			} else if (token.getType() == ReTokenType.ESCAPED_CHAR) {
				ouputQueue.add(token);
			} else if (token.getType() == ReTokenType.GROUP) {
				ouputQueue.add(token);
			} else if (token.getType() == ReTokenType.CLASS) {
				ouputQueue.add(token);
			} else if (token.getType() == ReTokenType.ANCHOR) {
				ouputQueue.add(token);
			} else if (token.getType() == ReTokenType.QUANTIFIER) {
				ouputQueue.add(token);
			} else if (token.getType() == ReTokenType.CONCATENATION_IMPLICIT) {
				// push it onto the operator stack.
				operatorStack.addFirst(token);
			} else if (token.getType() == ReTokenType.ALTERNATIVE) {
				while (thereIsAConcatenationAtTheTopOfTheOperatorStack())
					ouputQueue.add(operatorStack.removeFirst());
				// push it onto the operator stack.
				operatorStack.addFirst(token);
			} else if (token.getType() == ReTokenType.PARENTHESIS_OPEN) {
				operatorStack.addFirst(token);
			} else if (token.getType() == ReTokenType.PARENTHESIS_CLOSE) {
				while (operatorStack.peekFirst() != null
						&& operatorStack.peekFirst().getType() != ReTokenType.PARENTHESIS_OPEN)
					ouputQueue.add(operatorStack.removeFirst());
				final ReToken first = operatorStack.removeFirst();
//				ouputQueue.add(first);

			} else {
				throw new UnsupportedOperationException(token.toString());
			}

		}

		while (operatorStack.isEmpty() == false) {
			final ReToken token = operatorStack.removeFirst();
			ouputQueue.add(token);
		}

		// System.err.println("ouputQueue=" + ouputQueue);
	}

	private boolean thereIsAConcatenationAtTheTopOfTheOperatorStack() {
		final ReToken top = operatorStack.peekFirst();
		return top != null && top.getType() == ReTokenType.CONCATENATION_IMPLICIT;
	}

	public final List<ReToken> getOuputQueue() {
		return Collections.unmodifiableList(ouputQueue);
	}

}
