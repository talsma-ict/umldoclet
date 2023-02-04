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
package net.sourceforge.plantuml.tim;

import net.sourceforge.plantuml.utils.LineLocation;
import net.sourceforge.plantuml.utils.StringLocated;

public class EaterDeclareReturnFunction extends Eater {

	private TFunctionImpl function;
	private final LineLocation location;
	private boolean finalFlag;

	public EaterDeclareReturnFunction(StringLocated s) {
		super(s.getTrimmed());
		this.location = s.getLocation();
	}

	@Override
	public void analyze(TContext context, TMemory memory) throws EaterException, EaterExceptionLocated {
		skipSpaces();
		checkAndEatChar("!");
		boolean unquoted = false;
		while (peekUnquoted() || peekFinal()) {
			if (peekUnquoted()) {
				checkAndEatChar("unquoted");
				skipSpaces();
				unquoted = true;
			} else if (peekFinal()) {
				checkAndEatChar("final");
				skipSpaces();
				finalFlag = true;
			}
		}
		checkAndEatChar("function");
		skipSpaces();
		function = eatDeclareReturnFunctionWithOptionalReturn(context, memory, unquoted, location);
	}

	private boolean peekUnquoted() {
		return peekChar() == 'u';
	}

	private boolean peekFinal() {
		return peekChar() == 'f' && peekCharN2() == 'i';
	}

	public TFunctionImpl getFunction() {
		return function;
	}

	public final boolean getFinalFlag() {
		return finalFlag;
	}

}
