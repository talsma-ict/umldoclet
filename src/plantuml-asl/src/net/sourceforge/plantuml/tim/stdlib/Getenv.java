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
package net.sourceforge.plantuml.tim.stdlib;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.security.SecurityUtils;
import net.sourceforge.plantuml.tim.EaterException;
import net.sourceforge.plantuml.tim.EaterExceptionLocated;
import net.sourceforge.plantuml.tim.TContext;
import net.sourceforge.plantuml.tim.TFunctionSignature;
import net.sourceforge.plantuml.tim.TMemory;
import net.sourceforge.plantuml.tim.expression.TValue;

public class Getenv extends SimpleReturnFunction {

	public TFunctionSignature getSignature() {
		return new TFunctionSignature("%getenv", 1);
	}

	public boolean canCover(int nbArg, Set<String> namedArgument) {
		return nbArg == 1;
	}

	public TValue executeReturnFunction(TContext context, TMemory memory, LineLocation location, List<TValue> values,
			Map<String, TValue> named) throws EaterException, EaterExceptionLocated {
		if (OptionFlags.ALLOW_INCLUDE == false) {
			return TValue.fromString("");
		}
		final String name = values.get(0).toString();
		final String value = getenv(name);
		if (value == null)
			return TValue.fromString("");

		return TValue.fromString(value);
	}

	private String getenv(String name) {
		// Check, if the script requests secret information.
		// A plantuml server should have an own SecurityManager to
		// avoid access to properties and environment variables, but we should
		// also stop here in other deployments.
		if (SecurityUtils.isSecurityEnv(name))
			return null;
		final String env = System.getProperty(name);
		if (env != null)
			return env;

		return System.getenv(name);
	}
}
