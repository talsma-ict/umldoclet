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

//https://en.cppreference.com/w/c/language/operator_precedence

public enum TokenOperator {
	MULTIPLICATION(100 - 3, "*") {
		public TValue operate(TValue v1, TValue v2) {
			return v1.multiply(v2);
		}
	},
	DIVISION(100 - 3, "/") {
		public TValue operate(TValue v1, TValue v2) {
			return v1.dividedBy(v2);
		}
	},
	ADDITION(100 - 4, "+") {
		public TValue operate(TValue v1, TValue v2) {
			return v1.add(v2);
		}
	},
	SUBSTRACTION(100 - 4, "" + TokenType.COMMERCIAL_MINUS_SIGN) {
		public TValue operate(TValue v1, TValue v2) {
			return v1.minus(v2);
		}
	},
	LESS_THAN(100 - 6, "<") {
		public TValue operate(TValue v1, TValue v2) {
			return v1.lessThan(v2);
		}
	},
	GREATER_THAN(100 - 6, ">") {
		public TValue operate(TValue v1, TValue v2) {
			return v1.greaterThan(v2);
		}
	},
	LESS_THAN_OR_EQUALS(100 - 6, "<=") {
		public TValue operate(TValue v1, TValue v2) {
			return v1.lessThanOrEquals(v2);
		}
	},
	GREATER_THAN_OR_EQUALS(100 - 6, ">=") {
		public TValue operate(TValue v1, TValue v2) {
			return v1.greaterThanOrEquals(v2);
		}
	},
	EQUALS(100 - 7, "==") {
		public TValue operate(TValue v1, TValue v2) {
			return v1.equalsOperation(v2);
		}
	},
	NOT_EQUALS(100 - 7, "!=") {
		public TValue operate(TValue v1, TValue v2) {
			return v1.notEquals(v2);
		}
	},
	LOGICAL_AND(100 - 11, "&&") {
		public TValue operate(TValue v1, TValue v2) {
			return v1.logicalAnd(v2);
		}
	},
	LOGICAL_OR(100 - 12, "||") {
		public TValue operate(TValue v1, TValue v2) {
			return v1.logicalOr(v2);
		}
	};

	private final int precedence;
	private final String display;

	private TokenOperator(int precedence, String display) {
		this.precedence = precedence;
		this.display = display;
	}

	public boolean isLeftAssociativity() {
		return true;
	}

	public static TokenOperator getTokenOperator(char ch, char ch2) {
		for (TokenOperator op : TokenOperator.values())
			if (op.display.length() == 2 && op.display.charAt(0) == ch && op.display.charAt(1) == ch2)
				return op;

		for (TokenOperator op : TokenOperator.values())
			if (op.display.length() == 1 && op.display.charAt(0) == ch)
				return op;

		return null;
	}

	public final int getPrecedence() {
		return precedence;
	}

	public abstract TValue operate(TValue v1, TValue v2);

	public final String getDisplay() {
		return display;
	}
}
