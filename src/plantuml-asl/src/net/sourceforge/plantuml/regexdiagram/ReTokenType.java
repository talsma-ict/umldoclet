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
package net.sourceforge.plantuml.regexdiagram;

public enum ReTokenType {

	SIMPLE_CHAR, //
	ESCAPED_CHAR, //
	CLASS, //
	QUANTIFIER, //
	ANCHOR, //
	GROUP, //
	ALTERNATIVE, //
	PARENTHESIS_OPEN, //
	PARENTHESIS_CLOSE, //
	CONCATENATION_IMPLICIT;

	static public boolean needImplicitConcatenation(ReTokenType token1, ReTokenType token2) {
		if (token1 == ALTERNATIVE)
			return false;
		if (token2 == ALTERNATIVE)
			return false;
		if (token2 == QUANTIFIER)
			return false;
		if (token1 == PARENTHESIS_OPEN)
			return false;
		if (token2 == PARENTHESIS_CLOSE)
			return false;
		return true;
	}

}
