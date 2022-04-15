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

import java.util.Objects;

import net.sourceforge.plantuml.json.JsonValue;

public final class TValue {

	private final int intValue;
	private final String stringValue;
	private final JsonValue jsonValue;

	private TValue(int value) {
		this.intValue = value;
		this.stringValue = null;
		this.jsonValue = null;
	}

	private TValue(String stringValue) {
		this.intValue = 0;
		this.jsonValue = null;
		this.stringValue = Objects.requireNonNull(stringValue);
	}

	public TValue(JsonValue json) {
		this.jsonValue = json;
		this.intValue = 0;
		this.stringValue = null;
	}

	public static TValue fromInt(int v) {
		return new TValue(v);
	}

	public static TValue fromBoolean(boolean b) {
		return new TValue(b ? 1 : 0);
	}

	public static TValue fromJson(JsonValue json) {
		return new TValue(json);
	}

	@Override
	public String toString() {
		if (jsonValue != null && jsonValue.isString()) {
			return jsonValue.asString();
		}
		if (jsonValue != null) {
			return jsonValue.toString();
		}
		if (stringValue == null) {
			return "" + intValue;
		}
		return stringValue;
	}

	public static TValue fromString(Token token) {
		if (token.getTokenType() != TokenType.QUOTED_STRING) {
			throw new IllegalArgumentException();
		}
		return new TValue(token.getSurface());
	}

	public static TValue fromString(String s) {
		return new TValue(s);
	}

	public static TValue fromNumber(Token token) {
		if (token.getTokenType() != TokenType.NUMBER) {
			throw new IllegalArgumentException();
		}
		return new TValue(Integer.parseInt(token.getSurface()));
	}

	public TValue add(TValue v2) {
		if (this.isNumber() && v2.isNumber()) {
			return new TValue(this.intValue + v2.intValue);
		}
		return new TValue(toString() + v2.toString());
	}

	public TValue minus(TValue v2) {
		if (this.isNumber() && v2.isNumber()) {
			return new TValue(this.intValue - v2.intValue);
		}
		return new TValue(toString() + v2.toString());
	}

	public TValue multiply(TValue v2) {
		if (this.isNumber() && v2.isNumber()) {
			return new TValue(this.intValue * v2.intValue);
		}
		return new TValue(toString() + "*" + v2.toString());
	}

	public TValue dividedBy(TValue v2) {
		if (this.isNumber() && v2.isNumber()) {
			return new TValue(this.intValue / v2.intValue);
		}
		return new TValue(toString() + "/" + v2.toString());
	}

	public boolean isNumber() {
		return this.jsonValue == null && this.stringValue == null;
	}

	public boolean isJson() {
		return this.jsonValue != null;
	}

	public boolean isString() {
		return this.stringValue != null;
	}

	public Token toToken() {
		if (isNumber()) {
			return new Token(toString(), TokenType.NUMBER, null);
		}
		if (isJson()) {
			return new Token(toString(), TokenType.JSON_DATA, jsonValue);
		}
		return new Token(toString(), TokenType.QUOTED_STRING, null);
	}

	public TValue greaterThanOrEquals(TValue v2) {
		if (this.isNumber() && v2.isNumber()) {
			return fromBoolean(this.intValue >= v2.intValue);
		}
		return fromBoolean(toString().compareTo(v2.toString()) >= 0);
	}

	public TValue greaterThan(TValue v2) {
		if (this.isNumber() && v2.isNumber()) {
			return fromBoolean(this.intValue > v2.intValue);
		}
		return fromBoolean(toString().compareTo(v2.toString()) > 0);
	}

	public TValue lessThanOrEquals(TValue v2) {
		if (this.isNumber() && v2.isNumber()) {
			return fromBoolean(this.intValue <= v2.intValue);
		}
		return fromBoolean(toString().compareTo(v2.toString()) <= 0);
	}

	public TValue lessThan(TValue v2) {
		if (this.isNumber() && v2.isNumber()) {
			return fromBoolean(this.intValue < v2.intValue);
		}
		return fromBoolean(toString().compareTo(v2.toString()) < 0);
	}

	public TValue equalsOperation(TValue v2) {
		if (this.isNumber() && v2.isNumber()) {
			return fromBoolean(this.intValue == v2.intValue);
		}
		return fromBoolean(toString().compareTo(v2.toString()) == 0);
	}

	public TValue notEquals(TValue v2) {
		if (this.isNumber() && v2.isNumber()) {
			return fromBoolean(this.intValue != v2.intValue);
		}
		return fromBoolean(toString().compareTo(v2.toString()) != 0);
	}

	public boolean toBoolean() {
		if (this.isNumber()) {
			return this.intValue != 0;
		}
		return toString().length() > 0;
	}

	public int toInt() {
		return this.intValue;
	}

	public TValue logicalAnd(TValue v2) {
		return fromBoolean(this.toBoolean() && v2.toBoolean());
	}

	public TValue logicalOr(TValue v2) {
		return fromBoolean(this.toBoolean() || v2.toBoolean());
	}

	public JsonValue toJson() {
		return jsonValue;
	}

}
