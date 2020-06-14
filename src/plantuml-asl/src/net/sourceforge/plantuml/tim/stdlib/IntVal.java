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
package net.sourceforge.plantuml.tim.stdlib;

import java.util.List;

import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.tim.EaterException;
import net.sourceforge.plantuml.tim.TContext;
import net.sourceforge.plantuml.tim.TFunctionSignature;
import net.sourceforge.plantuml.tim.TMemory;
import net.sourceforge.plantuml.tim.expression.TValue;

public class IntVal extends SimpleReturnFunction {

	public TFunctionSignature getSignature() {
		return new TFunctionSignature("%intval", 1);
	}

	public boolean canCover(int nbArg) {
		return nbArg == 1;
	}

	public TValue executeReturn(TContext context, TMemory memory, List<TValue> args) throws EaterException {
		final String s = args.get(0).toString();
		try {
			return TValue.fromInt(Integer.parseInt(s));
		} catch (Exception e) {
			Log.error("Cannot convert " + s);
		}
		return TValue.fromInt(0);
	}
}
