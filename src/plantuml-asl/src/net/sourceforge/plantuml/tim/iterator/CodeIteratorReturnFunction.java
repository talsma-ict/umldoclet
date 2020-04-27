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
package net.sourceforge.plantuml.tim.iterator;

import java.util.List;

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.tim.EaterException;
import net.sourceforge.plantuml.tim.EaterExceptionLocated;
import net.sourceforge.plantuml.tim.FunctionsSet;
import net.sourceforge.plantuml.tim.TContext;
import net.sourceforge.plantuml.tim.TFunctionType;
import net.sourceforge.plantuml.tim.TLineType;
import net.sourceforge.plantuml.tim.TMemory;

public class CodeIteratorReturnFunction extends AbstractCodeIterator {

	private final FunctionsSet functionsSet;

	private final TContext context;
	private final TMemory memory;
	private final List<StringLocated> logs;

	public CodeIteratorReturnFunction(CodeIterator source, TContext context, TMemory memory, FunctionsSet functionsSet,
			List<StringLocated> logs) {
		super(source);
		this.context = context;
		this.functionsSet = functionsSet;
		this.logs = logs;
		this.memory = memory;
	}

	public StringLocated peek() throws EaterException, EaterExceptionLocated {
		while (true) {
			final StringLocated result = source.peek();
			if (result == null) {
				return null;
			}

			if (functionsSet.pendingFunction() != null
					&& functionsSet.pendingFunction().getFunctionType() == TFunctionType.RETURN_FUNCTION) {
				logs.add(result);
				if (result.getType() == TLineType.END_FUNCTION) {
					if (functionsSet.pendingFunction().doesContainReturn() == false) {
						throw EaterExceptionLocated.located(
								"This function does not have any !return directive. Declare it as a procedure instead ?",
								result);
					}
					functionsSet.executeEndfunction();
				} else {
					functionsSet.pendingFunction().addBody(result);
				}
				next();
				continue;
			}

			if (result.getType() == TLineType.DECLARE_RETURN_FUNCTION) {
				logs.add(result);
				functionsSet.executeDeclareReturnFunction(context, memory, result);
				next();
				continue;
			}

			return result;
		}
	}

}
