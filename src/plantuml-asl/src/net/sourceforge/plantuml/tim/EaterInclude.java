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

import net.sourceforge.plantuml.preproc2.PreprocessorIncludeStrategy;

public class EaterInclude extends Eater {

	private String location;
	private PreprocessorIncludeStrategy strategy = PreprocessorIncludeStrategy.DEFAULT;

	public EaterInclude(String s) {
		super(s);
	}

	@Override
	public void execute(TContext context, TMemory memory) throws EaterException {
		skipSpaces();
		checkAndEatChar("!include");
		final char peekChar = peekChar();
		if (peekChar == 'u') {
			checkAndEatChar("url");
		} else if (peekChar == '_') {
			checkAndEatChar("_");
			final char peekChar2 = peekChar();
			if (peekChar2 == 'm') {
				checkAndEatChar("many");
				this.strategy = PreprocessorIncludeStrategy.MANY;
			} else {
				checkAndEatChar("once");
				this.strategy = PreprocessorIncludeStrategy.ONCE;
			}
		}
		skipSpaces();
		this.location = context.applyFunctionsAndVariables(memory, this.eatAllToEnd());

	}

	public final String getLocation() {
		return location;
	}

	public final PreprocessorIncludeStrategy getPreprocessorIncludeStrategy() {
		return strategy;
	}

}
