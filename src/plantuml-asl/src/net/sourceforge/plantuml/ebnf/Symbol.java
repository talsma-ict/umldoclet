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

public enum Symbol {

	LITTERAL, //

	// https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form
	DEFINITION, // =
	CONCATENATION, // ,
	TERMINATION, // ;
	ALTERNATION, // |
	OPTIONAL_OPEN, // [
	OPTIONAL_CLOSE, // ]
	OPTIONAL, //
	REPETITION_SYMBOL, // *
	REPETITION_OPEN, // {
	REPETITION_CLOSE, // }
	REPETITION_MINUS_CLOSE, // }
	REPETITION_ZERO_OR_MORE, //
	REPETITION_ONE_OR_MORE, //
	GROUPING_OPEN, // (
	GROUPING_CLOSE, // )
	TERMINAL_STRING1, // " "
	TERMINAL_STRING2, // ' '
	COMMENT_TOKEN, // (* *)
	COMMENT_BELOW, // (* *)
	COMMENT_ABOVE, // (* *)
	SPECIAL_SEQUENCE, // ? ?
	EXCEPTION; // -

	public int getPriority() {
		switch (this) {
		case REPETITION_SYMBOL:
			return 3;
		case CONCATENATION:
			return 2;
		case ALTERNATION:
			return 1;
		}
		throw new UnsupportedOperationException();
	}

	boolean isOperator() {
		return this == CONCATENATION || this == ALTERNATION || this == REPETITION_SYMBOL;
	}

	boolean isFunction() {
		return this == OPTIONAL || this == REPETITION_ZERO_OR_MORE;
	}

}
