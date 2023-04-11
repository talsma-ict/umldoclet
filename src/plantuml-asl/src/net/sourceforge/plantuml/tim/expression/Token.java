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
package net.sourceforge.plantuml.tim.expression;

import net.sourceforge.plantuml.json.JsonValue;

public class Token {

	private final String surface;
	private final JsonValue json;
	private final TokenType tokenType;

	@Override
	public String toString() {
		return tokenType + "{" + surface + "}";
	}

	public Token(char surface, TokenType tokenType, JsonValue json) {
		this("" + surface, tokenType, json);
	}

	public Token(String surface, TokenType tokenType, JsonValue json) {
		this.surface = surface;
		this.tokenType = tokenType;
		this.json = json;
	}

	public TokenOperator getTokenOperator() {
		if (this.tokenType != TokenType.OPERATOR)
			throw new IllegalStateException();

		final char ch2 = surface.length() > 1 ? surface.charAt(1) : 0;
		return TokenOperator.getTokenOperator(surface.charAt(0), ch2);
	}

	public final String getSurface() {
		return surface;
	}

	public final TokenType getTokenType() {
		return tokenType;
	}

	public Token muteToFunction() {
		if (this.tokenType != TokenType.PLAIN_TEXT)
			throw new IllegalStateException();

		return new Token(surface, TokenType.FUNCTION_NAME, null);
	}

	public JsonValue getJson() {
		if (this.tokenType != TokenType.JSON_DATA)
			throw new IllegalStateException();

		return json;
	}

}
