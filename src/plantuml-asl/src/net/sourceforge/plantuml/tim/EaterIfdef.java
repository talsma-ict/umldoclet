/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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

import net.sourceforge.plantuml.preproc.EvalBoolean;
import net.sourceforge.plantuml.preproc.Truth;

public class EaterIfdef extends Eater {

	private String expression;

	public EaterIfdef(String s) {
		super(s);
	}

	@Override
	public void execute(TContext context, TMemory memory) throws EaterException {
		skipSpaces();
		checkAndEatChar("!ifdef");
		skipSpaces();
		expression = eatAllToEnd();
	}

	public boolean isTrue(final TContext context, final TMemory memory) {
		final EvalBoolean eval = new EvalBoolean(expression, new Truth() {

			public boolean isTrue(String varname) {
				final TVariable currentValue = memory.getVariable(varname);
				return currentValue != null || context.doesFunctionExist(varname);
			}
		});

		return eval.eval();
	}

}
