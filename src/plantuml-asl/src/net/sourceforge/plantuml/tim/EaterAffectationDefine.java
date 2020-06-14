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

import net.sourceforge.plantuml.tim.expression.TValue;

public class EaterAffectationDefine extends Eater {

	public EaterAffectationDefine(String s) {
		super(s);
	}

	@Override
	public void execute(TContext context, TMemory memory) throws EaterException {
		skipSpaces();
		checkAndEatChar("!define");
		skipSpaces();
		final String varname = eatAndGetVarname();
		skipSpaces();
		final String tmp = eatAllToEnd();
		final String tmp2 = context.applyFunctionsAndVariables(memory, tmp);
		final TValue value = TValue.fromString(tmp2);
		// if (memory instanceof TMemoryLocal) {
		// memory = ((TMemoryLocal) memory).getGlobalForInternalUseOnly();
		// }
		memory.putVariable(varname, new TVariable(value), TVariableScope.GLOBAL);
	}

}
