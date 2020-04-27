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

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.tim.expression.TValue;
import net.sourceforge.plantuml.tim.expression.TokenStack;
import net.sourceforge.plantuml.tim.iterator.CodePosition;

public class ExecutionContextWhile {

	private final TokenStack whileExpression;
	private final CodePosition codePosition;
	private boolean skipMe;

	private ExecutionContextWhile(TokenStack whileExpression, CodePosition codePosition) {
		this.whileExpression = whileExpression;
		this.codePosition = codePosition;
	}

	@Override
	public String toString() {
		return whileExpression.toString() + " " + codePosition;
	}

	public static ExecutionContextWhile fromValue(TokenStack whileExpression, CodePosition codePosition) {
		return new ExecutionContextWhile(whileExpression, codePosition);
	}

	public TValue conditionValue(LineLocation location, TContext context, TMemory memory) throws EaterException, EaterExceptionLocated {
		return whileExpression.getResult(location, context, memory);
	}

	public void skipMe() {
		skipMe = true;
	}

	public final boolean isSkipMe() {
		return skipMe;
	}

	public CodePosition getStartWhile() {
		return codePosition;
	}

}
