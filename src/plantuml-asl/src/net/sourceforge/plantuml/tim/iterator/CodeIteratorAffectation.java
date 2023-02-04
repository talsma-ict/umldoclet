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
package net.sourceforge.plantuml.tim.iterator;

import java.util.List;

import net.sourceforge.plantuml.json.ParseException;
import net.sourceforge.plantuml.tim.EaterAffectation;
import net.sourceforge.plantuml.tim.EaterException;
import net.sourceforge.plantuml.tim.EaterExceptionLocated;
import net.sourceforge.plantuml.tim.TContext;
import net.sourceforge.plantuml.tim.TLineType;
import net.sourceforge.plantuml.tim.TMemory;
import net.sourceforge.plantuml.utils.StringLocated;

public class CodeIteratorAffectation extends AbstractCodeIterator {

	private final TContext context;
	private final TMemory memory;
	private final List<StringLocated> logs;

	public CodeIteratorAffectation(CodeIterator source, TContext context, TMemory memory, List<StringLocated> log) {
		super(source);
		this.context = context;
		this.memory = memory;
		this.logs = log;
	}

	public StringLocated peek() throws EaterException, EaterExceptionLocated {
		while (true) {
			final StringLocated result = source.peek();
			if (result == null) {
				return null;
			}
			if (result.getType() == TLineType.AFFECTATION) {
				logs.add(result);
				doAffectation(result);
				next();
				continue;
			}
			return result;
		}
	}

	private void doAffectation(StringLocated result) throws EaterException, EaterExceptionLocated {
		int lastLocation = -1;
		for (int i = 0; i < 9999; i++)
			try {
				this.executeAffectation(context, memory, result);
				return;
			} catch (ParseException e) {
				if (e.getColumn() <= lastLocation) {
					throw EaterException.located("Error in JSON format");
				}
				lastLocation = e.getColumn();
				next();
				final StringLocated forward = source.peek();
				result = result.append(forward.getString());
			}
	}

	private void executeAffectation(TContext context, TMemory memory, StringLocated s) throws EaterException, EaterExceptionLocated {
		new EaterAffectation(s).analyze(context, memory);
	}

}
