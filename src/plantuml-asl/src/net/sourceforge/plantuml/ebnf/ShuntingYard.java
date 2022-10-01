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
package net.sourceforge.plantuml.ebnf;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class ShuntingYard {

	final private List<Token> ouputQueue = new ArrayList<>();
	final private Deque<Token> operatorStack = new ArrayDeque<>();

	public ShuntingYard(Iterator<Token> it) {
		while (it.hasNext()) {
			final Token token = it.next();
			if (token.getSymbol() == Symbol.LITTERAL || token.getSymbol() == Symbol.TERMINAL_STRING1) {
				ouputQueue.add(token);
			} else if (token.getSymbol() == Symbol.ALTERNATION) {
				operatorStack.addFirst(token);
			} else {
				throw new UnsupportedOperationException(token.toString());
			}

		}
		while (operatorStack.isEmpty() == false) {
			final Token token = operatorStack.removeFirst();
			ouputQueue.add(token);
		}
	}

	public final Iterator<Token> getOuputQueue() {
		return ouputQueue.iterator();
	}

}
